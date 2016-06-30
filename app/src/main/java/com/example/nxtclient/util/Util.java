package com.example.nxtclient.util;

import android.util.Log;

import com.example.nxtclient.NxtApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by shakhand on 2016/6/30.
 */
 public class Util {
    static public String postHttp(String url, String[][] keyValues)
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
            Log.i(NxtApplication.TAG, "params string "+paramsBuilder.toString());

            OutputStream outputStream = urlCon.getOutputStream();
            outputStream.write(paramsBuilder.toString().getBytes("utf8"));
            outputStream.close();

            if (urlCon.getResponseCode() != 200) {
                Log.e(NxtApplication.TAG, "fail to POST HTTP");
                return null;
            }

            BufferedReader responseReader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
            String line;
            StringBuilder responseStringBuffer = new StringBuilder();
            while ((line = responseReader.readLine()) != null) {
                Log.i(NxtApplication.TAG, line);
                responseStringBuffer.append(line);
                responseStringBuffer.append('\n');
            }

            response = responseStringBuffer.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(NxtApplication.TAG, "invalid url " + url);
        } catch (java.net.SocketException e){
            Log.e(NxtApplication.TAG, "socket exception");
            e.printStackTrace();
        } catch (java.net.SocketTimeoutException e) {
            Log.e(NxtApplication.TAG, "socket timeout");
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
