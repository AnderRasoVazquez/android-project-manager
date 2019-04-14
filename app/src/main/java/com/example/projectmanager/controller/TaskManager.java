package com.example.projectmanager.controller;

/**
 * Esta clase es la fachada desde la que se hacen peticiones al servidor.
 */
public class TaskManager {
    private static TaskManager instance = null;

    private TaskManager() {}

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }
}
