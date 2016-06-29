package com.example.shakhand.httptest;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import nxt.crypto.Crypto;
import nxt.util.Convert;

//import nxt.crypto.Crypto;

public class PostHttpActivity extends AppCompatActivity {

    public static final String TAG = "POST_HTTP";
    private TextView mDisplayView;
    private EditText mUrlEditText;
    private EditText mGetBalanceEditText;
    private EditText mMyAccountEditText;
    private EditText mMyPassword;
    private EditText mAmountNQT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_http);

        mDisplayView       = (TextView)findViewById(R.id.display);
        mUrlEditText       = (EditText)findViewById(R.id.urlEditText);
        mMyAccountEditText= (EditText)findViewById(R.id.myAccountEditText);
        mMyPassword        = (EditText)findViewById(R.id.myPasswordEditText);
        mGetBalanceEditText = (EditText)findViewById(R.id.getBalanceAccountEditText);
        mAmountNQT          = (EditText)findViewById(R.id.amountNQT);
    }

    public void getBalanceClick(View view) {
        String[] inputs = new String[2];
        String url = mUrlEditText.getText().toString();
        String account = mGetBalanceEditText.getText().toString();
        inputs[0] = url;
        inputs[1] = account;

        Log.i(TAG, "post " + url);
        new GetBalanceTask().execute(inputs);
    }

    public void sendMoneyClick(View view){
        Log.i(TAG, "Send money");
        String url = mUrlEditText.getText().toString();
        String account = mMyAccountEditText.getText().toString();
        String password = mMyPassword.getText().toString();
        String amountNQT = mAmountNQT.getText().toString();
        String[] inputs = {url, account, password, amountNQT};

        new SendMoneyTask().execute(inputs);
    }

    class SendMoneyTask extends AsyncTask<String, Object, String>
    {
        private String postHttp(String url, String[][] keyValues)
        {
            String response = null;
            try {
                // issue a Http Post
                HttpURLConnection urlCon = (HttpURLConnection) (new URL(url)).openConnection();
                urlCon.setConnectTimeout(10 * 1000);
                urlCon.setRequestMethod("POST");
                urlCon.setDoInput(true);
                urlCon.setDoOutput(true);

                //write parameters to Http Reques
                StringBuilder paramsBuilder = new StringBuilder();
                for (String[] keyValue:keyValues)
                {
                    String key = keyValue[0];
                    String value = keyValue[1];
                    paramsBuilder.append(key+"="+value+"&");
                }
                // remove the last char "&"
                paramsBuilder.deleteCharAt(paramsBuilder.length()-1);
                Log.i(TAG, "params string "+paramsBuilder.toString());

                OutputStream outputStream = urlCon.getOutputStream();
                outputStream.write(paramsBuilder.toString().getBytes("utf8"));
                outputStream.close();

                if (urlCon.getResponseCode() != 200) {
                    Log.e(TAG, "fail to POST HTTP");
                    return null;
                }

                BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                String line;
                StringBuilder responseStringBuffer = new StringBuilder();
                while ((line = responseReader.readLine()) != null) {
                    Log.i(TAG, line);
                    responseStringBuffer.append(line);
                    responseStringBuffer.append('\n');
                }

                response = responseStringBuffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(TAG, "invalid url " + url);
            } catch (java.net.SocketException e){
                Log.e(TAG, "socket exception");
                e.printStackTrace();
            } catch (java.net.SocketTimeoutException e) {
                Log.e(TAG, "socket timeout");
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        private String getPublicKey(String url, String account)
        {
            String publicKey = null;
            try {

                // parse public key json
                String[][] params = {{"requestType", "getAccountPublicKey"}, {"account", account}};
                String response = postHttp(url, params);

                if( response == null) {
                    return null;
                }

                JSONObject publicKeyJson = new JSONObject(response);
                publicKey = publicKeyJson.getString("publicKey");
            }
            catch (JSONException e) {
                Log.e(TAG, "Fails to parse public key json");
            }
            return publicKey;
        }

        private String unsignedSendMoney(String url, String account, String publicKey, String amountNQT)
        {
            String unsignedTransactionBytes = null;
            String[][] params = {{"requestType","sendMoney"},
                                {"recipient", account},
                                {"amountNQT",amountNQT},
                                {"publicKey",publicKey},
                                {"feeNQT","100000000"},
                                {"deadline","600"}};
            String response  = postHttp(url, params);

            try {
                JSONObject sendMoneyJson = new JSONObject(response);
                unsignedTransactionBytes = sendMoneyJson.getString("unsignedTransactionBytes");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "Fails to parse unsigned send money json");
            }

            return unsignedTransactionBytes;
        }

        private String broadcastUnsignedTransaction(String url, String unsignedTransaction)
        {
            String[][] params = {{"requestType","broadcastTransaction"},
                    {"transactionBytes", unsignedTransaction }};
            String response  = postHttp(url, params);

            return response;
        }
        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            String account = params[1];
            String password = params[2];
            String amountNQT = params[3];

            String publicKey = getPublicKey(url, account);
            if( publicKey == null )
            {
                return "Fail to get public key";
            }

            String unsignedTransactionHex = unsignedSendMoney(url, account, publicKey, amountNQT);
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
            mDisplayView.setText( result );
        }
    }
    class GetBalanceTask extends  AsyncTask<String, Object, String>
    {
        @Override
        protected String doInBackground(String... inputs) {
            String result = "";
            try {
                URL url = new URL(inputs[0]);
                String account = inputs[1];
                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setConnectTimeout(10*1000);
                urlCon.setRequestMethod("POST");
                urlCon.setDoInput(true);
                urlCon.setDoOutput(true);
                String paramsString = "requestType=getAccount&account=" + account;
                byte[] paramsBytes = paramsString.getBytes("utf8");
                OutputStream outputStream = urlCon.getOutputStream();
                outputStream.write(paramsBytes);
                outputStream.close();

                if( urlCon.getResponseCode() == 200) {
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                    String line;
                    StringBuilder responseStringBuffer = new StringBuilder();
                    while( (line =  responseReader.readLine() ) != null )
                    {
                        Log.i(TAG, line);
                        responseStringBuffer.append(line);
                        responseStringBuffer.append('\n');
                    }
                    result = responseStringBuffer.toString();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (SocketTimeoutException e){
                return "connect timeout";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            // parse result which is a json string
            // extract the balance field from it
            try {
                JSONObject jobject = new JSONObject(result);
                String balance = jobject.getString("balanceNQT");
                long value = Long.parseLong(balance)/100000000;

                mDisplayView.setText( "balance = " + value );
            } catch (JSONException e) {
                mDisplayView.setText("Parsing json fails with " + result);
            }
        }
    }

}
