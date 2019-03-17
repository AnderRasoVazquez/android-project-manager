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

/**
 * Singleton para interactuar con la clase de datos.
 */
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
        demoProject.put(DBFields.TABLE_PROJECTS_NAME, "TFG");
        demoProject.put(DBFields.TABLE_PROJECTS_DESC, "En este proyecto se representan las tareas del TFG.");
        long idDemoProject = db.insert(DBFields.TABLE_PROJECTS, null, demoProject);

        ContentValues demoMember = new ContentValues();
        demoMember.put(DBFields.TABLE_MEMBERS_IDUSER, "demo");
        demoMember.put(DBFields.TABLE_MEMBERS_IDPROJECT, idDemoProject);
        db.insert(DBFields.TABLE_MEMBERS, null, demoMember);

        ContentValues demoTask = new ContentValues();
        demoTask.put(DBFields.TABLE_TASKS_NAME, "1.1 Una tarea ya realizada.");
        demoTask.put(DBFields.TABLE_TASKS_DESC, "Que magnífica descripción.");
        demoTask.put(DBFields.TABLE_TASKS_DUEDATE, "2019/03/20");
        demoTask.put(DBFields.TABLE_TASKS_INITDATE, "2019/02/01");
        demoTask.put(DBFields.TABLE_TASKS_EXPECTED, 7.5);
        demoTask.put(DBFields.TABLE_TASKS_PROGRESS, 100);
        demoTask.put(DBFields.TABLE_TASKS_IDPROJECT, idDemoProject);
        long idTask = db.insert(DBFields.TABLE_TASKS, null, demoTask);

        demoTask = new ContentValues();
        demoTask.put(DBFields.TABLE_TASKS_NAME, "1.2 Esta tarea no se ha terminado.");
        demoTask.put(DBFields.TABLE_TASKS_DESC, "Otra descripción impresionante.");
        demoTask.put(DBFields.TABLE_TASKS_DUEDATE, "2019/04/17");
        demoTask.put(DBFields.TABLE_TASKS_INITDATE, "2019/03/01");
        demoTask.put(DBFields.TABLE_TASKS_EXPECTED, 7.5);
        demoTask.put(DBFields.TABLE_TASKS_PROGRESS, 25);
        demoTask.put(DBFields.TABLE_TASKS_IDPROJECT, idDemoProject);
        db.insert(DBFields.TABLE_TASKS, null, demoTask);

        ContentValues demoWorktime;
        demoWorktime = new ContentValues();
        demoWorktime.put(DBFields.TABLE_WORKTIME_IDPROJECT, idDemoProject);
        demoWorktime.put(DBFields.TABLE_WORKTIME_IDTASK, idTask);
        demoWorktime.put(DBFields.TABLE_WORKTIME_IDUSER, "demo");
        demoWorktime.put(DBFields.TABLE_WORKTIME_DATE, "2018/11/02");
        demoWorktime.put(DBFields.TABLE_WORKTIME_HOURS, 7.5);
        db.insert(DBFields.TABLE_WORKTIME, null, demoWorktime);

        demoWorktime = new ContentValues();
        demoWorktime.put(DBFields.TABLE_WORKTIME_IDPROJECT, idDemoProject);
        demoWorktime.put(DBFields.TABLE_WORKTIME_IDTASK, idTask);
        demoWorktime.put(DBFields.TABLE_WORKTIME_IDUSER, "demo");
        demoWorktime.put(DBFields.TABLE_WORKTIME_DATE, "2018/11/22");
        demoWorktime.put(DBFields.TABLE_WORKTIME_HOURS, 3);
        db.insert(DBFields.TABLE_WORKTIME, null, demoWorktime);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Iniciar sesion.
     * @param loginString
     * @return
     */
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

    /**
     * Registrarse.
     * @param registerString
     * @return
     */
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

    /**
     * Comprobar si un usuario existe.
     * @param email
     * @param pass
     * @return
     */
    public boolean userExists(String email, String pass) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] fields = new String[]{DBFields.TABLE_USERS_ID, DBFields.TABLE_USERS_NAME};
        String[] args = new String[]{email, pass};

        Cursor cursor = db.query(DBFields.TABLE_USERS, fields, DBFields.TABLE_USERS_ID + "=? AND " + DBFields.TABLE_USERS_PASS + "=?", args, null, null, null);

        boolean exists;
        exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Comprobar si un usuario existe.
     * @param email
     * @return
     */
    public boolean userExists(String email) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] fields = new String[]{DBFields.TABLE_USERS_ID, DBFields.TABLE_USERS_NAME};
        String[] args = new String[]{email};

        Cursor cursor = db.query(DBFields.TABLE_USERS, fields, DBFields.TABLE_USERS_ID + "=?", args, null, null, null);

        boolean exists;
        exists = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Devuelve los proyectos en formato JSON dado un JSON con el email del usuario.
     * @param jsonUser
     * @return
     */
    public String getProjects(String jsonUser) {
        try {
            JSONObject json = new JSONObject(jsonUser);
            String email = json.getString("email");

            SQLiteDatabase db = instance.getReadableDatabase();

            String[] fields = new String[]{
                    DBFields.VIEW_PROJECTMEMBERS_IDUSER,
                    DBFields.VIEW_PROJECTMEMBERS_IDPROJECT,
                    DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT,
                    DBFields.VIEW_PROJECTMEMBERS_NAMEPROJECT,
                    DBFields.VIEW_PROJECTMEMBERS_NAMEUSER
            };

            String[] args = new String[]{email};

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


    /**
     * Devuelve un JSON de un proyecto dado el id del proyecto.
     * @param idProject
     * @return
     */
    public String getProject(int idProject) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] fields = new String[]{
                DBFields.TABLE_PROJECTS_ID,
                DBFields.TABLE_PROJECTS_NAME,
                DBFields.TABLE_PROJECTS_DESC,
        };

        String[] args = new String[]{Integer.toString(idProject)};

        Cursor cursor = db.query(DBFields.TABLE_PROJECTS, fields, DBFields.TABLE_PROJECTS_ID + "=?", args, null, null, null);
        cursor.moveToNext();

        String result = getProjectInfo(cursor).toString();
        cursor.close();
        db.close();

        return result;
    }

    /**
     * Devuelve un JSON de una tarea dado su id.
     * @param idTask
     * @return
     */
    public String getTask(int idTask) {
        SQLiteDatabase db = instance.getReadableDatabase();

        String[] args = new String[]{Integer.toString(idTask)};

        Cursor cursor = db.query(DBFields.TABLE_TASKS, null, DBFields.TABLE_TASKS_ID + "=?", args, null, null, null);
        cursor.moveToNext();

        String result = getTask(cursor).toString();

        cursor.close();
        db.close();

        return result;
    }

    /**
     * Devuelve un objeto JSON con la informacion de un proyecto dado un cursor.
     * @param cursor
     * @return
     */
    private JSONObject getProjectInfo(Cursor cursor) {
        JSONObject oneJson = new JSONObject();
        int columnIndex;
        try {
            columnIndex = cursor.getColumnIndex(DBFields.TABLE_PROJECTS_ID);
            oneJson.put(DBFields.TABLE_PROJECTS_ID, cursor.getInt(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_PROJECTS_NAME);
            oneJson.put(DBFields.TABLE_PROJECTS_NAME, cursor.getString(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_PROJECTS_DESC);
            String desc = cursor.getString(columnIndex);
            oneJson.put(DBFields.TABLE_PROJECTS_DESC, desc == null ? JSONObject.NULL : desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oneJson;
    }

    /**
     * Devuelve un objeto JSON con la informacion de un proyecto dado un cursor.
     * @param cursor
     * @return
     */
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

    /**
     * Devuelve las tareas de un proyecto en formato JSON dado un JSON con el id del proyecto.
     * @param jsonProject
     * @return
     */
    public String getTasks(String jsonProject) {
        try {
            JSONObject json = new JSONObject(jsonProject);
            String id_proyect = json.getString("id_project");

            SQLiteDatabase db = instance.getReadableDatabase();

            String[] fields = new String[]{
                    DBFields.TABLE_TASKS_ID,
                    DBFields.TABLE_TASKS_NAME,
                    DBFields.TABLE_TASKS_DESC,
                    DBFields.TABLE_TASKS_DUEDATE,
                    DBFields.TABLE_TASKS_INITDATE,
                    DBFields.TABLE_TASKS_EXPECTED,
                    DBFields.TABLE_TASKS_PROGRESS,
                    DBFields.TABLE_TASKS_IDPROJECT
            };

            String[] args = new String[]{id_proyect};

            Cursor cursor = db.query(DBFields.TABLE_TASKS, fields, DBFields.TABLE_TASKS_IDPROJECT + "=?", args, null, null, null);

            JSONObject jsonResult = new JSONObject();
            JSONArray jarray = new JSONArray();

            while (cursor.moveToNext()) {
                jarray.put(getTask(cursor));
            }

            cursor.close();
            db.close();

            jsonResult.put("tasks", jarray);

//            for(int i = 0; i < jarray.length(); i++) {
//                JSONObject oneObj = jarray.getJSONObject(i);
//                int taskId = oneObj.getInt(DBFields.TABLE_TASKS_ID);
//                oneObj.put("worktime", getWorkedTime(taskId));
//            }

            return jsonResult.toString(4);

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Añade un proyecto y devuelve su id dado un JSON con la info del proyecto.
     * @param jsonProject
     * @return
     */
    public int addProject(String jsonProject) {
        try {
            JSONObject json = new JSONObject(jsonProject);
            String name = json.getString(DBFields.TABLE_PROJECTS_NAME);
            String desc = json.getString(DBFields.TABLE_PROJECTS_DESC);
            String email = json.getString(DBFields.TABLE_MEMBERS_IDUSER);
            desc = desc.equals("null") ? null : desc;

            ContentValues values = new ContentValues();
            values.put(DBFields.TABLE_PROJECTS_NAME, name);
            values.put(DBFields.TABLE_PROJECTS_DESC, desc);

            SQLiteDatabase db = instance.getWritableDatabase();

            int idProject = (int) db.insert(DBFields.TABLE_PROJECTS, null, values);

            ContentValues member = new ContentValues();
            member.put(DBFields.TABLE_MEMBERS_IDPROJECT, idProject);
            member.put(DBFields.TABLE_MEMBERS_IDUSER, email);

            db.insert(DBFields.TABLE_MEMBERS, null, member);

            db.close();

            return idProject;

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Añade una tarea y devuelve su id dado un JSON con sus datos.
     * @param jsonTask
     * @return
     */
    public int addTask(String jsonTask) {
        try {
            JSONObject json = new JSONObject(jsonTask);
            String name = json.getString(DBFields.TABLE_TASKS_NAME);
            int idProjec = json.getInt(DBFields.TABLE_TASKS_IDPROJECT);

            ContentValues values = new ContentValues();
            values.put(DBFields.TABLE_TASKS_NAME, name);
            values.put(DBFields.TABLE_TASKS_IDPROJECT, idProjec);
//            values.put(DBFields.TABLE_TASKS_EXPECTED, 0.0);
//            values.put(DBFields.TABLE_TASKS_PROGRESS, 0);

            SQLiteDatabase db = instance.getWritableDatabase();

            int idTask = (int) db.insert(DBFields.TABLE_TASKS, null, values);

            db.close();

            return idTask;

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Actualiza un proyecto dado un JSON con sus datos.
     * @param jsonProject
     */
    public void updateProject(String jsonProject) {
        try {
            JSONObject json = new JSONObject(jsonProject);
            String name = json.getString(DBFields.TABLE_PROJECTS_NAME);
            String desc = json.getString(DBFields.TABLE_PROJECTS_DESC);
            int idProject = json.getInt(DBFields.TABLE_PROJECTS_ID);
            desc = desc.equals("null") ? null : desc;

            ContentValues values = new ContentValues();
            values.put(DBFields.TABLE_PROJECTS_NAME, name);
            values.put(DBFields.TABLE_PROJECTS_DESC, desc);

            SQLiteDatabase db = instance.getWritableDatabase();

            String[] args = new String[]{Integer.toString(idProject)};
            db.update(DBFields.TABLE_PROJECTS, values, DBFields.TABLE_PROJECTS_ID + "=?", args);
            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Actualiza una tarea dado un JSON con sus datos.
     * @param jsonTask
     */
    public void updateTask(String jsonTask) {
        try {
            JSONObject json = new JSONObject(jsonTask);

            int taskId = json.getInt(DBFields.TABLE_TASKS_ID);
            String name = json.getString(DBFields.TABLE_TASKS_NAME);
            String desc = json.getString(DBFields.TABLE_TASKS_DESC);
            String dueDate = json.getString(DBFields.TABLE_TASKS_DUEDATE);
            String initDate = json.getString(DBFields.TABLE_TASKS_INITDATE);
            double expected = json.getDouble(DBFields.TABLE_TASKS_EXPECTED);
            int progress = json.getInt(DBFields.TABLE_TASKS_PROGRESS);

            ContentValues values = new ContentValues();
            values.put(DBFields.TABLE_TASKS_NAME, name);
            values.put(DBFields.TABLE_TASKS_DESC, desc);
            values.put(DBFields.TABLE_TASKS_DUEDATE, dueDate);
            values.put(DBFields.TABLE_TASKS_INITDATE, initDate);
            values.put(DBFields.TABLE_TASKS_EXPECTED, expected);
            values.put(DBFields.TABLE_TASKS_PROGRESS, progress);

            SQLiteDatabase db = instance.getWritableDatabase();

            String[] args = new String[]{Integer.toString(taskId)};
            db.update(DBFields.TABLE_TASKS, values, DBFields.TABLE_TASKS_ID + "=?", args);
            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Devuelve un objeto JSON de una tarea dado un cursor.
     * @param cursor
     * @return
     */
    private JSONObject getTask(Cursor cursor) {
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
            oneJson.put(DBFields.TABLE_TASKS_DUEDATE, due == null ? JSONObject.NULL : due);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_INITDATE);
            String init = cursor.getString(columnIndex);
            oneJson.put(DBFields.TABLE_TASKS_INITDATE, init == null ? JSONObject.NULL : init);

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_EXPECTED);
            oneJson.put(DBFields.TABLE_TASKS_EXPECTED, cursor.getDouble(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_PROGRESS);
            oneJson.put(DBFields.TABLE_TASKS_PROGRESS, cursor.getDouble(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_TASKS_IDPROJECT);
            oneJson.put(DBFields.TABLE_TASKS_IDPROJECT, cursor.getInt(columnIndex));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return oneJson;
    }


    /**
     * Devuelve un JSON del tiempo trabajado dado un JSON con el id de la tarea.
     * @param taskJson
     * @return
     */
    public String getWorkedTime(String taskJson) {
        JSONObject json = new JSONObject();
        try {
            JSONObject oneJson = new JSONObject(taskJson);
            int idTask = oneJson.getInt("id_task");

            SQLiteDatabase db = instance.getReadableDatabase();

            String[] fields = new String[]{
//                DBFields.TABLE_WORKTIME_IDUSER,
//                DBFields.TABLE_WORKTIME_IDTASK,
//                DBFields.TABLE_WORKTIME_IDPROJECT,
                    DBFields.TABLE_WORKTIME_ID,
                    DBFields.TABLE_WORKTIME_DATE,
                    DBFields.TABLE_WORKTIME_HOURS
            };

            String[] args = new String[]{Integer.toString(idTask)};
            Cursor cursor = db.query(DBFields.TABLE_WORKTIME, fields, DBFields.TABLE_WORKTIME_IDTASK + "=?", args, null, null, null);

            JSONArray jarray = new JSONArray();

            while (cursor.moveToNext()) {
                jarray.put(formatOneWorkedTime(cursor));
            }

            json.put("worktime", jarray);
            cursor.close();
            db.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * Elimina un proyecto dado su id.
     * @param idProject
     */
    public void deleteProject(int idProject) {
        SQLiteDatabase db = instance.getWritableDatabase();

        String[] args = new String[]{Integer.toString(idProject)};
        db.delete(DBFields.TABLE_PROJECTS, DBFields.TABLE_PROJECTS_ID + "=?", args);

        db.close();
    }

    /**
     * Elimina una tarea dado su id.
     * @param idTask
     */
    public void deleteTask(int idTask) {
        SQLiteDatabase db = instance.getWritableDatabase();

        String[] args = new String[]{Integer.toString(idTask)};
        db.delete(DBFields.TABLE_TASKS, DBFields.TABLE_TASKS_ID + "=?", args);

        db.close();
    }

    /**
     * Devuelve un objeto JSON del tiempo trabajado dado un cursor.
     * @param cursor
     * @return
     */
    private JSONObject formatOneWorkedTime(Cursor cursor) {
        JSONObject json = new JSONObject();
        int columnIndex;
        try {
            columnIndex = cursor.getColumnIndex(DBFields.TABLE_WORKTIME_ID);
            json.put(DBFields.TABLE_WORKTIME_ID, cursor.getInt(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_WORKTIME_DATE);
            json.put(DBFields.TABLE_WORKTIME_DATE, cursor.getString(columnIndex));

            columnIndex = cursor.getColumnIndex(DBFields.TABLE_WORKTIME_HOURS);
            json.put(DBFields.TABLE_WORKTIME_HOURS, cursor.getDouble(columnIndex));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Elimina el tiempo trabajado dado su id.
     * @param idWork
     */
    public void deleteWork(int idWork) {
        SQLiteDatabase db = instance.getWritableDatabase();

        String[] args = new String[]{String.valueOf(idWork)};
        db.delete(DBFields.TABLE_WORKTIME, "id=?", args);

        db.close();
    }

    /**
     * Añade tiempo trabajado dado un JSON con sus datos.
     * @param jsonWork
     */
    public void addWork(String jsonWork) {
        try {
            JSONObject json = new JSONObject(jsonWork);

            String email = json.getString(DBFields.TABLE_WORKTIME_IDUSER);
            int projectId = json.getInt(DBFields.TABLE_WORKTIME_IDPROJECT);
            int taskId = json.getInt(DBFields.TABLE_WORKTIME_IDTASK);
            String date = json.getString(DBFields.TABLE_WORKTIME_DATE);
            double hours = json.getDouble(DBFields.TABLE_WORKTIME_HOURS);

            ContentValues values = new ContentValues();
            values.put(DBFields.TABLE_WORKTIME_IDUSER, email);
            values.put(DBFields.TABLE_WORKTIME_IDPROJECT, projectId);
            values.put(DBFields.TABLE_WORKTIME_IDTASK, taskId);
            values.put(DBFields.TABLE_WORKTIME_DATE, date);
            values.put(DBFields.TABLE_WORKTIME_HOURS, hours);

            SQLiteDatabase db = instance.getWritableDatabase();

            String[] args = new String[]{Integer.toString(taskId), date};

            int rows = db.update(DBFields.TABLE_WORKTIME, values, DBFields.TABLE_WORKTIME_IDTASK + "=? AND " + DBFields.TABLE_WORKTIME_DATE + "=?", args);
            if (rows == 0){
                db.insert(DBFields.TABLE_WORKTIME, null, values);
            }

            db.close();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
