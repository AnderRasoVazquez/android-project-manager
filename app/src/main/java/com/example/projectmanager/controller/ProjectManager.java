package com.example.projectmanager.controller;

/**
 * Esta clase es la fachada desde la que se hacen peticiones al servidor.
 */
public class ProjectManager {
    private static ProjectManager instance = null;

    private ProjectManager() {}

    public static ProjectManager getInstance() {
        if (instance == null) {
            instance = new ProjectManager();
        }
        return instance;
    }
}
