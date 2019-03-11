package com.example.projectmanager.utils;

public class DBFields {

    public static final String TABLE_USERS = "users";
    public static final String TABLE_USERS_ID = "id";
    public static final String TABLE_USERS_PASS = "pass";
    public static final String TABLE_USERS_NAME = "username";

    public static final String TABLE_PROJECTS = "projects";
    public static final String TABLE_PROJECTS_ID = "id";
    public static final String TABLE_PROJECTS_NAME = "name";
    public static final String TABLE_PROJECTS_DESC = "desc";

    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_TASKS_ID = "id";
    public static final String TABLE_TASKS_NAME = "name";
    public static final String TABLE_TASKS_DESC = "desc";
    public static final String TABLE_TASKS_DUEDATE = "due_date";
    public static final String TABLE_TASKS_INITDATE = "init_date";
    public static final String TABLE_TASKS_EXPECTED = "expected";
    public static final String TABLE_TASKS_PROGRESS = "progress";
    public static final String TABLE_TASKS_IDPROJECT = "id_project";

    public static final String TABLE_WORKTIME = "worktime";
    public static final String TABLE_WORKTIME_IDUSER = "id_user";
    public static final String TABLE_WORKTIME_IDTASK = "id_task";
    public static final String TABLE_WORKTIME_IDPROJECT = "id_project";
    public static final String TABLE_WORKTIME_DATE = "date";
    public static final String TABLE_WORKTIME_HOURS = "hours";


    public static final String CREATE_TABLE_USERS = "" +
            "CREATE TABLE " + TABLE_USERS + "(" +
                TABLE_USERS_ID + "   TEXT      PRIMARY KEY," +
                TABLE_USERS_PASS + " TEXT      NOT NULL," +
                TABLE_USERS_NAME + " TEXT      NOT NULL" +
            ")";

    public static final String CREATE_TABLE_PROYECTS = "" +
            "CREATE TABLE " + TABLE_PROJECTS + "(" +
                TABLE_PROJECTS_ID + "   INTEGER PRIMARY KEY AUTOINCREMENT," +
                TABLE_PROJECTS_NAME + " TEXT       NOT NULL," +
                TABLE_PROJECTS_DESC + " TEXT      NOT NULL" +
            ")";

    public static final String CREATE_TABLE_TASKS = "" +
            "CREATE TABLE " + TABLE_TASKS + "(" +
                TABLE_TASKS_ID + "   INTEGER PRIMARY KEY AUTOINCREMENT," +
                TABLE_TASKS_NAME + " TEXT       NOT NULL," +
                TABLE_TASKS_DESC + " TEXT      NOT NULL," +

                TABLE_TASKS_DUEDATE + " DATE     ," +
                TABLE_TASKS_INITDATE + " DATE    ," +

                TABLE_TASKS_EXPECTED + " REAL      NOT NULL," +
                TABLE_TASKS_PROGRESS + " INTEGER      NOT NULL," +
                TABLE_TASKS_IDPROJECT + " INTEGER      NOT NULL," +

                " FOREIGN KEY (" + TABLE_TASKS_IDPROJECT + ") REFERENCES " + TABLE_PROJECTS + "(" + TABLE_PROJECTS_ID + ") ON DELETE CASCADE" +
            ")";

    public static final String CREATE_TABLE_WORKTIME = "" +
            "CREATE TABLE " + TABLE_WORKTIME + "(" +
                TABLE_WORKTIME_IDUSER + "   TEXT NOT NULL," +
                TABLE_WORKTIME_IDTASK + " TEXT       NOT NULL," +
                TABLE_WORKTIME_IDPROJECT + " TEXT      NOT NULL," +
                TABLE_WORKTIME_DATE + " DATE      NOT NULL," +
                TABLE_WORKTIME_HOURS + " REAL      NOT NULL," +

                " PRIMARY KEY (" + TABLE_WORKTIME_IDUSER + ", " + TABLE_WORKTIME_IDTASK + ", " + TABLE_WORKTIME_IDPROJECT + ", " + TABLE_WORKTIME_DATE + ")" +

                " FOREIGN KEY (" + TABLE_WORKTIME_IDUSER + ") REFERENCES " + TABLE_USERS + "(" + TABLE_USERS_ID + ") ON DELETE CASCADE" +
                " FOREIGN KEY (" + TABLE_WORKTIME_IDTASK + ") REFERENCES " + TABLE_TASKS + "(" + TABLE_TASKS_ID + ") ON DELETE CASCADE" +
                " FOREIGN KEY (" + TABLE_WORKTIME_IDPROJECT + ") REFERENCES " + TABLE_PROJECTS + "(" + TABLE_PROJECTS_ID + ") ON DELETE CASCADE" +
            ")";

}
