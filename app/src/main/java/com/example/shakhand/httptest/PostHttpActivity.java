package com.example.shakhand.httptest;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PostHttpActivity extends AppCompatActivity {

    public static final String TAG = "POST_HTTP";
    private TextView mDisplayView;
    private EditText mUrlEditText;
    private EditText mAccountEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_http);

        mDisplayView       = (TextView)findViewById(R.id.display);
        mUrlEditText       = (EditText)findViewById(R.id.urlEditText);
        mAccountEditText  = (EditText)findViewById(R.id.accountEditText);
    }

    public void postHttpClick(View view) {
        String[] inputs = new String[2];
        String url = mUrlEditText.getText().toString();
        String account = mAccountEditText.getText().toString();
        inputs[0] = url;
        inputs[1] = account;

        Log.i(TAG, "post " + url);
        new HttpPostTask().execute(inputs);
    }

    class HttpPostTask extends  AsyncTask<String, Object, String>
    {
        @Override
        protected String doInBackground(String... inputs) {
            String result = "";
            try {
                URL url = new URL(inputs[0]);
                String account = inputs[1];
                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
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
            } catch (IOException e) {
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
                mDisplayView.setText( "balance = " + balance);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
