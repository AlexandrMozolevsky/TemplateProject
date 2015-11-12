package com.itibo.templateproject.BlockModule;

import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by erick on 12.11.15.
 */
public class Module {
    public static abstract class Callback {
        public abstract void onSuccess(Model model);

        public abstract void onError(String error);
    }

    public static void blockModuleCheckoutAsync(final Callback callback) {
        new AsyncTask<Void, Void, Model>() {
            @Override
            protected Model doInBackground(Void... params) {
                String resultString = "";
                String responseLine = "";
                Model returnModel = new Model();

                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("vkcheck.hol.es")
                        .appendPath("main")
                        .appendPath("templateCheck");
                String myUrl = builder.build().toString();

                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(myUrl).openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Length", String.valueOf(("secret=mozolevski999&app=template&code=" + Constant.APP_CODE).length()));
                    connection.setDoOutput(true);
                    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                    outputStream.writeBytes("secret=" + Constant.HOST_SECRET + "&app=" + Constant.APP_NAME + "&code=" + Constant.APP_CODE);
                    outputStream.flush();
                    outputStream.close();
                    connection.setConnectTimeout(10000);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((responseLine = bufferedReader.readLine()) != null) {
                        resultString += responseLine;
                    }
                    bufferedReader.close();

                    JSONObject jsonObject = new JSONObject(resultString);
                    returnModel.setMessage(jsonObject.getString("message"));
                    returnModel.setCode(jsonObject.getString("code"));
                    returnModel.setStatus(jsonObject.getString("status"));
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                return returnModel;
            }

            @Override
            protected void onPostExecute(Model result) {
                callback.onSuccess(result);
            }
        }.execute();
    }
}
