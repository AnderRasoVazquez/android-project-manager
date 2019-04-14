package com.example.projectmanager.controller;

/**
 * Esta clase es la fachada desde la que se hacen peticiones al servidor.
 */
public class UserManager {
    private static UserManager instance = null;

    private UserManager() {}

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
}
