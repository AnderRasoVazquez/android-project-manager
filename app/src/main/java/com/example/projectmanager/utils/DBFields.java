package com.example.projectmanager.utils;

/**
 * Clase con los nombres de tablas y campos de la base de datos.
 * Contiene consultas sql para crear tablas y vistas.
 */
public class DBFields {

    public static final String TABLE_USERS = "users";
    public static final String TABLE_USERS_ID = "id";
    public static final String TABLE_USERS_PASS = "pass";
    public static final String TABLE_USERS_NAME = "username";

    public static final String TABLE_PROJECTS = "projects";
    public static final String TABLE_PROJECTS_ID = "project_id";
    public static final String TABLE_PROJECTS_NAME = "name";
    public static final String TABLE_PROJECTS_DESC = "desc";
    public static final String TABLE_PROJECTS_IMG = "img";

    public static final String TABLE_MEMBERS = "members";
    public static final String TABLE_MEMBERS_IDUSER = "id_user";
    public static final String TABLE_MEMBERS_IDPROJECT = "id_project";

    public static final String TABLE_TASKS = "tasks";
    public static final String TABLE_TASKS_ID = "task_id";
    public static final String TABLE_TASKS_NAME = "name";
    public static final String TABLE_TASKS_DESC = "desc";
    public static final String TABLE_TASKS_DUEDATE = "due_date";
    public static final String TABLE_TASKS_INITDATE = "init_date";
    public static final String TABLE_TASKS_EXPECTED = "expected";
    public static final String TABLE_TASKS_PROGRESS = "progress";
    public static final String TABLE_TASKS_IDPROJECT = "project";

    public static final String TABLE_WORKTIME = "worktime";
    public static final String TABLE_WORKTIME_ID = "work_id";
    public static final String TABLE_WORKTIME_IDUSER = "user";
    public static final String TABLE_WORKTIME_IDTASK = "task";
    public static final String TABLE_WORKTIME_IDPROJECT = "id_project";
    public static final String TABLE_WORKTIME_DATE = "date";
    public static final String TABLE_WORKTIME_HOURS = "time";

    public static final String VIEW_PROJECTMEMBERS = "project_members";
    public static final String VIEW_PROJECTMEMBERS_IDUSER = "id_user";
    public static final String VIEW_PROJECTMEMBERS_NAMEUSER = "name_user";
    public static final String VIEW_PROJECTMEMBERS_IDPROJECT = "id_project";
    public static final String VIEW_PROJECTMEMBERS_NAMEPROJECT = "name_project";
    public static final String VIEW_PROJECTMEMBERS_DESCPROJECT = "desc_project";

}
