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

    public static final String TABLE_MEMBERS = "members";
    public static final String TABLE_MEMBERS_IDUSER = "id_user";
    public static final String TABLE_MEMBERS_IDPROJECT = "id_project";

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

    public static final String VIEW_PROJECTMEMBERS = "project_members";
    public static final String VIEW_PROJECTMEMBERS_IDUSER = "id_user";
    public static final String VIEW_PROJECTMEMBERS_NAMEUSER = "name_user";
    public static final String VIEW_PROJECTMEMBERS_IDPROJECT = "id_project";
    public static final String VIEW_PROJECTMEMBERS_NAMEPROJECT = "name_project";
    public static final String VIEW_PROJECTMEMBERS_DESCPROJECT = "desc_project";


    public static final String CREATE_VIEW_PROJECTMEMBERS = "" +
            " CREATE VIEW " + VIEW_PROJECTMEMBERS + " AS " +
            " SELECT " +
            TABLE_USERS + "." + TABLE_USERS_ID + ", " +
            TABLE_USERS + "." + TABLE_USERS_NAME + ", " +
            TABLE_PROJECTS + "." + TABLE_PROJECTS_ID + ", " +
            TABLE_PROJECTS + "." + TABLE_PROJECTS_NAME + ", " +
            TABLE_PROJECTS + "." + TABLE_PROJECTS_DESC +
            " FROM " + TABLE_PROJECTS +
            " INNER JOIN " + TABLE_MEMBERS + " ON " + TABLE_MEMBERS + "." + TABLE_MEMBERS_IDPROJECT +"="+ TABLE_PROJECTS +"."+ TABLE_PROJECTS_ID +
            " INNER JOIN " + TABLE_USERS + " ON " + TABLE_MEMBERS + "." + TABLE_MEMBERS_IDUSER +"="+ TABLE_USERS +"."+ TABLE_USERS_ID +
            ";";

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
                TABLE_PROJECTS_DESC + " TEXT      " +
            ")";

    public static final String CREATE_TABLE_MEMBERS = "" +
            "CREATE TABLE " + TABLE_MEMBERS + "(" +
            TABLE_MEMBERS_IDUSER + "   TEXT NOT NULL," +
            TABLE_MEMBERS_IDPROJECT + " INTEGER      NOT NULL," +

            " PRIMARY KEY (" + TABLE_MEMBERS_IDUSER + ", " + TABLE_MEMBERS_IDPROJECT + ")" +

            " FOREIGN KEY (" + TABLE_MEMBERS_IDUSER + ") REFERENCES " + TABLE_USERS + "(" + TABLE_USERS_ID + ") ON DELETE CASCADE" +
            " FOREIGN KEY (" + TABLE_MEMBERS_IDPROJECT + ") REFERENCES " + TABLE_PROJECTS + "(" + TABLE_PROJECTS_ID + ") ON DELETE CASCADE" +
            ")";

    public static final String CREATE_TABLE_TASKS = "" +
            "CREATE TABLE " + TABLE_TASKS + "(" +
                TABLE_TASKS_ID + "   INTEGER PRIMARY KEY AUTOINCREMENT," +
                TABLE_TASKS_NAME + " TEXT       NOT NULL," +
                TABLE_TASKS_DESC + " TEXT      ," +

                TABLE_TASKS_DUEDATE + " DATE     ," +
                TABLE_TASKS_INITDATE + " DATE    ," +

                TABLE_TASKS_EXPECTED + " REAL      ," +
                TABLE_TASKS_PROGRESS + " INTEGER      ," +
                TABLE_TASKS_IDPROJECT + " INTEGER      ," +

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

                " FOREIGN KEY (" + TABLE_WORKTIME_IDUSER + ") REFERENCES " + TABLE_USERS + "(" + TABLE_USERS_ID + ")" +
                " FOREIGN KEY (" + TABLE_WORKTIME_IDTASK + ") REFERENCES " + TABLE_TASKS + "(" + TABLE_TASKS_ID + ") ON DELETE CASCADE" +
                " FOREIGN KEY (" + TABLE_WORKTIME_IDPROJECT + ") REFERENCES " + TABLE_PROJECTS + "(" + TABLE_PROJECTS_ID + ") ON DELETE CASCADE" +
            ")";

}
