package com.example.projectmanager.utils;

import org.json.JSONObject;

public interface OnConnectionFailure {
    void onFailure(int statusCode, JSONObject json);
}
