package com.example.shakhand.httptest;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

    public static final String TAG = "POSTHTTP";
    private TextView mDisplayView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_http);

        mDisplayView = (TextView)findViewById(R.id.display);

    }

    public void postHttpClick(View view) {
        Log.i(TAG, "post it");
        new HttpPostTask().execute(new Object());
    }

    class HttpPostTask extends  AsyncTask<Object, Object, String>
    {
        @Override
        protected String doInBackground(Object... params) {
            String result = "";
            try {
                URL url = new URL("http://192.168.95.1:7876/nxt");
                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("POST");
                urlCon.setDoInput(true);
                urlCon.setDoOutput(true);
                String paramsString = "requestType=getAccount&account=NXT-WGS8-B6T6-4ZJL-DPLQM";
                byte[] paramsBytes = paramsString.getBytes("utf8");
                OutputStream outputStream = urlCon.getOutputStream();
                outputStream.write(paramsBytes);
                outputStream.close();

                if( urlCon.getResponseCode() == 200) {
                    BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
                    String line;
                    StringBuffer responseStringBuffer = new StringBuffer();
                    while( (line =  responseReader.readLine() ) != null )
                    {
                        Log.i(TAG, line);
                        responseStringBuffer.append(line+'\n');
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
            mDisplayView.setText( result);
        }
    };

}
