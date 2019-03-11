package com.example.projectmanager.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.projectmanager.utils.DBFields;

import org.json.JSONException;
import org.json.JSONObject;

public class DB extends SQLiteOpenHelper {

    private static DB instance = null;

    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private DB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DB getInstance(Context context) {
        if (instance == null) {
            instance = new DB(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBFields.CREATE_TABLE_USERS);
        db.execSQL(DBFields.CREATE_TABLE_PROYECTS);
        db.execSQL(DBFields.CREATE_TABLE_TASKS);
        db.execSQL(DBFields.CREATE_TABLE_WORKTIME);

        ContentValues demoUser = new ContentValues();
        demoUser.put(DBFields.TABLE_USERS_ID, "demo");
        demoUser.put(DBFields.TABLE_USERS_NAME, "demo");
        demoUser.put(DBFields.TABLE_USERS_PASS, "demo");

        db.insert(DBFields.TABLE_USERS, null, demoUser);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public boolean login(String loginString) {
        try {
            JSONObject json = new JSONObject(loginString);
            String email = json.getString("email");
            String pass = json.getString("pass");

            return userExists(email, pass);

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String registerString) {
        try {
            JSONObject json = new JSONObject(registerString);
            String email = json.getString("email");
            String name = json.getString("name");
            String pass = json.getString("pass");

            if (!userExists(email)) {
                SQLiteDatabase db = instance.getReadableDatabase();
                ContentValues demoUser = new ContentValues();

                demoUser.put(DBFields.TABLE_USERS_ID, email);
                demoUser.put(DBFields.TABLE_USERS_NAME, name);
                demoUser.put(DBFields.TABLE_USERS_PASS, pass);

                db.insert(DBFields.TABLE_USERS, null, demoUser);
                return true;
            } else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean userExists(String email, String pass) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] fields = new String[] {DBFields.TABLE_USERS_ID, DBFields.TABLE_USERS_NAME};
        String[] args = new String[] {email, pass};

        Cursor cursor = db.query(DBFields.TABLE_USERS, fields, DBFields.TABLE_USERS_ID + "=? AND " + DBFields.TABLE_USERS_PASS + "=?", args, null, null, null);

        boolean exists;
        exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    public boolean userExists(String email) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] fields = new String[] {DBFields.TABLE_USERS_ID, DBFields.TABLE_USERS_NAME};
        String[] args = new String[] {email};

        Cursor cursor = db.query(DBFields.TABLE_USERS, fields, DBFields.TABLE_USERS_ID + "=?", args, null, null, null);

        boolean exists;
        exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }
}