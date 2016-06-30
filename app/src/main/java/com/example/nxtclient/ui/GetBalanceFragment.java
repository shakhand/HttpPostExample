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

/**
 * Created by shakhand on 2016/6/30.
 */
public class GetBalanceFragment extends Fragment implements View.OnClickListener {
    private EditText mAccountEditText;
    private TextView mDisplayView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstance)
    {
        return inflater.inflate(R.layout.get_balance_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstance)
    {
        mAccountEditText = ((EditText)view.findViewById(R.id.account));
        mDisplayView = (TextView)view.findViewById(R.id.display);
        view.findViewById(R.id.okBtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String acount = mAccountEditText.getText().toString();
        String[] args = new String[]{
                NxtApplication.url, acount
        };
        Log.i(NxtApplication.TAG, "post " + NxtApplication.url);
        new GetBalanceTask().execute(args);
    }

    class GetBalanceTask extends AsyncTask<String, Object, String>
    {
        @Override
        protected String doInBackground(String... inputs) {
            String result = "";
            String url = inputs[0];
            String account = inputs[1];
            String[][] parameters = new String[][]{
                    {"requestType", "getAccount"},
                    {"account", account}
            };
            String response = Util.postHttp(url, parameters);
            if( response == null )
            {
                return "fail to get balance";
            }

            try {
                JSONObject jobject = new JSONObject(response);
                String balance = jobject.getString("balanceNQT");
                long value = Long.parseLong(balance)/100000000;
                result = String.format("%d", value);
            } catch (JSONException e) {
               return "Parsing json fails with " + response;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            mDisplayView.setText( result );
        }
    }

}
