package com.example.projectmanager.controller;

/**
 * Esta clase es la fachada desde la que se hacen peticiones al servidor.
 */
public class WorkManager {
    private static WorkManager instance = null;

    private WorkManager() {}

    public static WorkManager getInstance() {
        if (instance == null) {
            instance = new WorkManager();
        }
        return instance;
    }
}
