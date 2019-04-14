package com.example.projectmanager.controller;

import android.content.Context;
import android.util.Base64;

import com.example.projectmanager.utils.HttpRequest;

import java.util.HashMap;

/**
 * Esta clase es la fachada desde la que se hacen peticiones al servidor.
 */
public class Facade {
    public static final String SERVER_ADDRESS = "http://192.168.1.128:5000";
//    public static final String SERVER_ADDRESS = "https://proyecto-das.herokuapp.com";
    public static final String URL_LOGIN = SERVER_ADDRESS + "/api/v1/login";

    private static Facade instance = null;

    private Facade() {}

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    public HttpRequest.Builder login(Context context, String email, String pass){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", getBasicAuth(email, pass));

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setContext(context)
                .setRequestMethod(HttpRequest.RequestMethod.GET)
                .setUrl(URL_LOGIN)
                .setHeaders(headers);
        return builder;
    }

    private static String getBasicAuth(String email, String pass) {
        // TODO en que orden va email y pass?
        String authString = email + ":" + pass;
        byte[] authStringEnc = Base64.encode(authString.getBytes(), Base64.DEFAULT);
        return "Basic " + new String(authStringEnc);
    }
}
