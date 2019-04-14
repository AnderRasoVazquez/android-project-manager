package com.example.projectmanager.utils;

import org.json.JSONObject;

public interface OnConnectionSuccess {
    void onSuccess(int statusCode, JSONObject json);
}
