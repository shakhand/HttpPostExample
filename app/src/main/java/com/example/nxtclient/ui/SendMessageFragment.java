package com.example.nxtclient.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.nxtclient.NxtApplication;
import com.example.nxtclient.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import nxt.crypto.Crypto;
import nxt.util.Convert;

/**
 * Created by shakhand on 2016/6/30.
 */
public class SendMessageFragment extends Fragment implements View.OnClickListener {
    private EditText mRecipientEditText;
    private EditText mMessageEditText;
    private TextView mDisplayTextView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance)
    {
        return inflater.inflate(R.layout.send_message_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance)
    {
        mRecipientEditText = ((EditText)view.findViewById(R.id.recipientAccountEditText));
        mMessageEditText = ((EditText)view.findViewById(R.id.messageEditText));
        mDisplayTextView = (TextView)view.findViewById(R.id.display);
        view.findViewById(R.id.okBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.i(NxtApplication.TAG, "SendMessage");
        String recipient = mRecipientEditText.getText().toString();
        String message = mMessageEditText.getText().toString();
        String[] inputs = {NxtApplication.url, NxtApplication.myAccount,  recipient, NxtApplication.myPassword, message};
        new SendMessageTask().execute(inputs);
    }

    class SendMessageTask extends AsyncTask<String, Object, String>
    {
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String sender = params[1];
            String recipient = params[2];
            String password = params[3];
            String message = params[4];

            String publicKey = getPublicKey(url, sender);
            if( publicKey == null )
            {
                return "Fail to get public key";
            }

            String unsignedTransactionHex = unsignedSendMessage(url, recipient, publicKey, message);
            if( unsignedTransactionHex == null )
            {
                return "Fails to get unsigned transaction bytes";
            }

            byte[] unsignedTransactionBytes = Convert.parseHexString(unsignedTransactionHex);
            byte[] signatureBytes = Crypto.sign(unsignedTransactionBytes, password);
            String signatureHex = Convert.toHexString(signatureBytes);
            String singedTransactionHex = unsignedTransactionHex.substring(0, 192) + signatureHex + unsignedTransactionHex.substring(320);
            String status = broadcastUnsignedTransaction(url, singedTransactionHex);
            return  status;
        }

        @Override
        protected void onPostExecute(String result)
        {
            mDisplayTextView.setText( result );
        }

        private String getPublicKey(String url, String account)
        {
            String publicKey = null;
            try {

                // parse public key json
                String[][] params = {{"requestType", "getAccountPublicKey"}, {"account", account}};
                String response = Util.postHttp(url, params);

                if( response == null) {
                    return null;
                }

                JSONObject publicKeyJson = new JSONObject(response);
                publicKey = publicKeyJson.getString("publicKey");
            }
            catch (JSONException e) {
                Log.e(NxtApplication.TAG, "Fails to parse public key json");
            }
            return publicKey;
        }

        private String unsignedSendMessage(String url, String recipient, String publicKey, String message)
        {
            String unsignedTransactionBytes = null;
            String[][] params = {{"requestType","sendMessage"},
                    {"recipient", recipient},
                    {"message",message},
                    {"publicKey",publicKey},
                    {"feeNQT","100000000"},
                    {"deadline","600"}};
            String response  = Util.postHttp(url, params);

            try {
                JSONObject sendMoneyJson = new JSONObject(response);
                unsignedTransactionBytes = sendMoneyJson.getString("unsignedTransactionBytes");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(NxtApplication.TAG, "Fails to parse unsigned send money json");
            }

            return unsignedTransactionBytes;
        }

        private String broadcastUnsignedTransaction(String url, String unsignedTransaction)
        {
            String[][] params = {{"requestType","broadcastTransaction"},
                    {"transactionBytes", unsignedTransaction }};
            String response  = Util.postHttp(url, params);

            return response;
        }
    }
}
