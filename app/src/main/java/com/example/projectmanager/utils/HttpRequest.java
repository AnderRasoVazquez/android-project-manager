package com.example.projectmanager.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/*
Bibliografia:
https://stackoverflow.com/questions/47135619/asynctask-in-android-app-for-more-than-one-rest-endpoint
 */
//public class HttpRequest<M extends BaseModel> extends AsyncTask<Object, Integer, M> {
public class HttpRequest extends AsyncTask<Void, Void, JSONObject> {
    public enum RequestMethod {
        GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

        private final String requestMethod;

        RequestMethod(String requestMethod) {
            this.requestMethod = requestMethod;
        }

        public String getValue() {
            return requestMethod;
        }
    }

    private String url;
    private OnConnectionSuccess onConnectionSuccess;
    private OnConnectionFailure onConnectionFailure;
    private RequestMethod method;
    private int statusCode;
    private String message;
    private HashMap<String, String> headers;
    private JSONObject body = null;

    private HttpRequest() { }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        try {
            HttpURLConnection connection = getHttpConnection();
            connection.connect();

            this.statusCode = connection.getResponseCode();

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            String response = convertInputStreamToString(inputStream);
            System.out.println("Response: " + response);
            JSONObject json = new JSONObject(response);
            return json;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private HttpURLConnection getHttpConnection() throws IOException {
        URL url = new URL(this.url);

        HttpURLConnection connection = (HttpURLConnection)
                url.openConnection();
        connection.setRequestMethod(method.getValue());

        connection.setReadTimeout(30000);
        connection.setConnectTimeout(30000);

        // hashmap para poner las cabeceras
        if (headers != null) {
            for (Map.Entry<String, String> entry: headers.entrySet()){
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        // si hay un json lo añade
        if (body != null) {
            System.out.println("BODY NOT NULL");
            connection.setDoOutput(true);
            OutputStreamWriter wr= new OutputStreamWriter(connection.getOutputStream());
            wr.write(body.toString());
            wr.flush();
            wr.close();
        }

        // TODO aqui poner json a enviar
//        if (method == RequestMethod.POST) {
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//
//            if (body != null) {
//                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
//                writer.write(new Gson().toJson(body));
//                writer.flush();
//            }
//        }

        return connection;
    }

    @Override
    protected void onPostExecute(JSONObject json) {
        System.out.println(this.statusCode + "\n" + json);
        if (this.statusCode / 100 != 2) {
            onConnectionFailure.onFailure(this.statusCode, json);
        } else {
            onConnectionSuccess.onSuccess(this.statusCode, json);
        }
//        if (json == null) {
//            if ((message != null && !message.equals("") && statusCode != 0)) {
//                HttpException httpException = new HttpException(statusCode, message);
//                onConnectionFailure.onFailure(httpException);
//            } else {
//                onConnectionFailure.onFailure("unknown error");
//            }
//        } else {
//            onConnectionSuccess.onSuccess(json);
//        }

    }


    public static class Builder {
        HttpRequest t = new HttpRequest();

        public Builder setUrl(String url) {
            t.url = url;
            return this;
        }

        public Builder setRequestMethod(RequestMethod method) {
            t.method = method;
            return this;
        }

        public Builder setBody(JSONObject body) {
            t.body = body;
            return this;
        }

        public Builder setHeaders(HashMap<String, String> headers) {
            t.headers = headers;
            return this;
        }

        public HttpRequest get() {
            return t;
        }

        public HttpRequest run(OnConnectionSuccess onConnectionSuccess, OnConnectionFailure onConnectionFailure) {
            t.onConnectionSuccess = onConnectionSuccess;
            t.onConnectionFailure = onConnectionFailure;
            t.execute();
            return t;
        }

        public Builder() {
        }
    }
}
