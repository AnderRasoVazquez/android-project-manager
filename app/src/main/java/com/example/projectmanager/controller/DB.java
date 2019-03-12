package com.example.projectmanager.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.example.projectmanager.R;
import com.example.projectmanager.utils.DBFields;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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
        db.execSQL(DBFields.CREATE_TABLE_MEMBERS);
        db.execSQL(DBFields.CREATE_TABLE_TASKS);
        db.execSQL(DBFields.CREATE_TABLE_WORKTIME);
        db.execSQL(DBFields.CREATE_VIEW_PROJECTMEMBERS);

        ContentValues demoUser = new ContentValues();
        demoUser.put(DBFields.TABLE_USERS_ID, "demo");
        demoUser.put(DBFields.TABLE_USERS_NAME, "demo");
        demoUser.put(DBFields.TABLE_USERS_PASS, "demo");

        db.insert(DBFields.TABLE_USERS, null, demoUser);

        ContentValues demoProject = new ContentValues();
        demoProject.put(DBFields.TABLE_PROJECTS_NAME, "Demo");
//        demoProject.put(DBFields.TABLE_PROJECTS_DESC, "");
        long idDemoProject = db.insert(DBFields.TABLE_PROJECTS, null, demoProject);

        ContentValues demoMember = new ContentValues();
        demoMember.put(DBFields.TABLE_MEMBERS_IDUSER, "demo");
        demoMember.put(DBFields.TABLE_MEMBERS_IDPROJECT, idDemoProject);
        db.insert(DBFields.TABLE_MEMBERS, null, demoMember);

        ContentValues demoTask = new ContentValues();
        demoTask.put(DBFields.TABLE_TASKS_NAME, "Task no title");
        demoTask.put(DBFields.TABLE_TASKS_DESC, "Task no desc");
        demoTask.put(DBFields.TABLE_TASKS_DUEDATE, "18/10/02");
        demoTask.put(DBFields.TABLE_TASKS_INITDATE, "17/10/02");
        demoTask.put(DBFields.TABLE_TASKS_EXPECTED, 7.5);
        demoTask.put(DBFields.TABLE_TASKS_PROGRESS, 50);
        demoTask.put(DBFields.TABLE_TASKS_IDPROJECT, idDemoProject);
        db.insert(DBFields.TABLE_TASKS, null, demoTask);
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

                ContentValues newUser = new ContentValues();
                newUser.put(DBFields.TABLE_USERS_ID, email);
                newUser.put(DBFields.TABLE_USERS_NAME, name);
                newUser.put(DBFields.TABLE_USERS_PASS, pass);

                db.insert(DBFields.TABLE_USERS, null, newUser);
                db.close();
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

    public String getProjects(String jsonUser) {
        try {
            JSONObject json = new JSONObject(jsonUser);
            String email = json.getString("email");

            SQLiteDatabase db = instance.getReadableDatabase();

            String[] fields = new String[] {
                    DBFields.VIEW_PROJECTMEMBERS_IDUSER,
                    DBFields.VIEW_PROJECTMEMBERS_IDPROJECT,
                    DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT,
                    DBFields.VIEW_PROJECTMEMBERS_NAMEPROJECT,
                    DBFields.VIEW_PROJECTMEMBERS_NAMEUSER
            };

            String[] args = new String[] {email};

            Cursor cursor = db.query(DBFields.VIEW_PROJECTMEMBERS, fields, DBFields.VIEW_PROJECTMEMBERS_IDUSER + "=?", args, null, null, null);

            JSONObject jsonResult = new JSONObject();
            JSONArray jarray = new JSONArray();

            while (cursor.moveToNext()) {
                jarray.put(getProject(cursor));
            }

            jsonResult.put("projects", jarray);
            cursor.close();
            db.close();

            return jsonResult.toString(4);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getProject(Cursor cursor) {
        JSONObject oneJson = new JSONObject();
        int columnIndex;
        try {
            columnIndex = cursor.getColumnIndex(DBFields.VIEW_PROJECTMEMBERS_IDPROJECT);
            oneJson.put(DBFields.VIEW_PROJECTMEMBERS_IDPROJECT, cursor.getInt(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.VIEW_PROJECTMEMBERS_NAMEPROJECT);
            oneJson.put(DBFields.VIEW_PROJECTMEMBERS_NAMEPROJECT, cursor.getString(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT);
            String desc = cursor.getString(columnIndex);
            oneJson.put(DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT, desc == null ? JSONObject.NULL : desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oneJson;
    }

    public String getTasks(String jsonProject) {
        try {
            JSONObject json = new JSONObject(jsonProject);
            String id_proyect = json.getString("id_project");

            SQLiteDatabase db = instance.getReadableDatabase();

            String[] fields = new String[] {
                    DBFields.TABLE_TASKS_ID,
                    DBFields.TABLE_TASKS_NAME,
                    DBFields.TABLE_TASKS_DESC,
                    DBFields.TABLE_TASKS_DUEDATE,
                    DBFields.TABLE_TASKS_INITDATE,
                    DBFields.TABLE_TASKS_EXPECTED,
                    DBFields.TABLE_TASKS_PROGRESS,
                    DBFields.TABLE_TASKS_IDPROJECT
            };

            String[] args = new String[] {id_proyect};

            Cursor cursor = db.query(DBFields.TABLE_TASKS, fields, DBFields.TABLE_TASKS_IDPROJECT + "=?", args, null, null, null);

            JSONObject jsonResult = new JSONObject();
            JSONArray jarray = new JSONArray();

            while (cursor.moveToNext()) {
                jarray.put(getTask(cursor));
            }

            jsonResult.put("tasks", jarray);
            cursor.close();
            db.close();

            for(int i = 0; i < jarray.length(); i++) {
                JSONObject oneObj = jarray.getJSONObject(i);
                int taskId = oneObj.getInt(DBFields.TABLE_TASKS_ID);
                oneObj.put("worktime", getWorkedTime(taskId));
            }

            return jsonResult.toString(4);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject getTask(Cursor cursor) {
        JSONObject oneJson = new JSONObject();
        int columnIndex;
        try {
            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_ID);
            int taskId = cursor.getInt(columnIndex);
            oneJson.put(DBFields.TABLE_TASKS_ID, taskId);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_NAME);
            oneJson.put(DBFields.TABLE_TASKS_NAME, cursor.getString(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_DESC);
            String desc = cursor.getString(columnIndex);
            oneJson.put(DBFields.TABLE_TASKS_DESC, desc == null ? JSONObject.NULL : desc);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_DUEDATE);
            String due = cursor.getString(columnIndex);
            oneJson.put(DBFields.TABLE_TASKS_DUEDATE, desc == null ? JSONObject.NULL : due);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_INITDATE);
            String init = cursor.getString(columnIndex);
            oneJson.put(DBFields.TABLE_TASKS_INITDATE, desc == null ? JSONObject.NULL : init);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_EXPECTED);
            double expected = cursor.getDouble(columnIndex);
            oneJson.put(DBFields.TABLE_TASKS_EXPECTED, desc == null ? JSONObject.NULL : expected);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_PROGRESS);
            oneJson.put(DBFields.TABLE_TASKS_PROGRESS, cursor.getDouble(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_IDPROJECT);
            oneJson.put(DBFields.TABLE_TASKS_IDPROJECT, cursor.getInt(columnIndex));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oneJson;
    }


    public JSONArray getWorkedTime(int idTask) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] fields = new String[] {
//                DBFields.TABLE_WORKTIME_IDUSER,
//                DBFields.TABLE_WORKTIME_IDTASK,
//                DBFields.TABLE_WORKTIME_IDPROJECT,
                DBFields.TABLE_WORKTIME_DATE,
                DBFields.TABLE_WORKTIME_HOURS
        };

        String[] args = new String[] {Integer.toString(idTask)};
        Cursor cursor = db.query(DBFields.TABLE_WORKTIME, fields, DBFields.TABLE_WORKTIME_IDTASK + "=?", args, null, null, null);

        JSONArray jarray = new JSONArray();

        while (cursor.moveToNext()) {
            jarray.put(formatOneWorkedTime(cursor));
        }

        cursor.close();
        db.close();

        return jarray;
    }

    public void deleteProject(int idProject) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] args = new String[] {Integer.toString(idProject)};
        db.delete(DBFields.TABLE_PROJECTS, DBFields.TABLE_PROJECTS_ID + "=?", args);

        db.close();
    }

    public void deleteTask(int idTask) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] args = new String[] {Integer.toString(idTask)};
        db.delete(DBFields.TABLE_TASKS, DBFields.TABLE_TASKS_ID + "=?", args);

        db.close();
    }

    public JSONObject formatOneWorkedTime(Cursor cursor) {
        JSONObject json = new JSONObject();
        int columnIndex;
        try {
            columnIndex = cursor.getColumnIndex(DBFields.TABLE_WORKTIME_DATE);
            json.put(DBFields.TABLE_WORKTIME_DATE, cursor.getString(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_WORKTIME_HOURS);
            json.put(DBFields.TABLE_WORKTIME_HOURS, cursor.getDouble(columnIndex));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
}
