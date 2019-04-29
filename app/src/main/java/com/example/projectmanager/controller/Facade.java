package com.example.projectmanager.controller;

import android.content.Context;
import android.util.Base64;

import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Esta clase es la fachada desde la que se hacen peticiones al servidor.
 */
public class Facade {
    public static final String SERVER_ADDRESS = "http://192.168.1.128:5000";
//    public static final String SERVER_ADDRESS = "https://proyecto-das.herokuapp.com";
    public static final String URL_LOGIN = SERVER_ADDRESS + "/api/v1/login";
    public static final String URL_GET_PROJECTS = SERVER_ADDRESS + "/api/v1/projects";

    private String serverToken;

    private static Facade instance = null;

    private Facade() {}

    public static Facade getInstance() {
        if (instance == null) {
            instance = new Facade();
        }
        return instance;
    }

    /**
     * Setter del token para el servidor.
     * @param serverToken
     */
    public void setServerToken(String serverToken) {
        this.serverToken = serverToken;
    }

    /**
     * Getter del token para el servidor.
     */
    public String getServerToken() {
        return this.serverToken;
    }

    /**
     * Peticion de login.
     * @param email
     * @param pass
     * @return
     */
    // TODO me sobra el context
    public HttpRequest.Builder login(String email, String pass){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Authorization", getBasicAuth(email, pass));

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.GET)
               .setUrl(URL_LOGIN)
               .setHeaders(headers);
        return builder;
    }

    /**
     * BasicAuth necesita encriptarse en Base64, esto ayuda.
     * @param email
     * @param pass
     * @return
     */
    private static String getBasicAuth(String email, String pass) {
        String authString = email + ":" + pass;
        byte[] authStringEnc = Base64.encode(authString.getBytes(), Base64.DEFAULT);
        return "Basic " + new String(authStringEnc);
    }

    /**
     * Peticion para obtener todos los proyectos.
     * @return
     */
    public HttpRequest.Builder getProjects(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.GET)
               .setUrl(URL_GET_PROJECTS)
               .setHeaders(headers);
        return builder;
    }

    /**
     * Peticion para obtener todas las tareas.
     * @param idProject
     * @return
     */
    public HttpRequest.Builder getTasks(String idProject){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.GET)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects/" + idProject + "/tasks")
                .setHeaders(headers);
        return builder;
    }

    /**
     * Peticion para obtener todos los tiempos trabajados.
     * @param idTask
     * @return
     */
    public HttpRequest.Builder getWorks(String idTask){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.GET)
                .setUrl(SERVER_ADDRESS + "/api/v1/tasks/" + idTask + "/works")
                .setHeaders(headers);
        return builder;
    }

    /**
     * Eliminar un tiempo trabajado
     * @param idWork
     * @return
     */
    public HttpRequest.Builder deleteWork(String idWork){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.DELETE)
                .setUrl(SERVER_ADDRESS + "/api/v1/works/" + idWork)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Eliminar una tarea trabajado
     * @param idTask
     * @return
     */
    public HttpRequest.Builder deleteTask(String idTask){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.DELETE)
                .setUrl(SERVER_ADDRESS + "/api/v1/tasks/" + idTask)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Eliminar un projecto.
     * @param idProject
     * @return
     */
    public HttpRequest.Builder deleteProject(String idProject){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.DELETE)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects/" + idProject)
                .setHeaders(headers);
        return builder;
    }


    /**
     * Añade un trabajo
     * @param idTask
     * @param json
     * @return
     */
    public HttpRequest.Builder addWork(String idTask, JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/tasks/" + idTask + "/works")
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Modifica un trabajo
     * @param workId
     * @param json
     * @return
     */
    public HttpRequest.Builder modWork(String workId, JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/works/" + workId)
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }


    /**
     * Crea un proyecto
     * @param json
     * @return
     */
    public HttpRequest.Builder addProject(JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects")
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }


    /**
     * Modifica un proyecto
     * @param projectId
     * @param json
     * @return
     */
    public HttpRequest.Builder modProject(String projectId, JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects/" + projectId)
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Devuelve info de un proyecto
     * @param projectId
     * @return
     */
    public HttpRequest.Builder getProject(String projectId){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.GET)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects/" + projectId)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Crea una tarea
     * @param json
     * @return
     */
    public HttpRequest.Builder addTask(String projectId, JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        // {{domain}}/api/v1/projects/43a21182-be10-47b0-97d8-9ed6abeea0a1/tasks
        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects/" + projectId + "/tasks")
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }


    /**
     * Modifica una tarea
     * @param taskId
     * @param json
     * @return
     */
    public HttpRequest.Builder modTask(String taskId, JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/tasks/" + taskId)
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Devuelve info de una tarea
     * @param taskId
     * @return
     */
    public HttpRequest.Builder getTask(String taskId){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.GET)
                .setUrl(SERVER_ADDRESS + "/api/v1/tasks/" + taskId)
                .setHeaders(headers);
        return builder;
    }

    /**
     * Registrar al usuario.
     * @param json
     * @return
     */
    public HttpRequest.Builder register(JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/users")
                .setBody(json)
                .setHeaders(headers);
        return builder;
    }


    /**
     * Actualiza el token de firebase del usuario.
     * @param json
     * @return
     */
    public void updateFirebaseToken(JSONObject json){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);
        headers.put("Content-Type", "application/json");

        System.out.println(json.toString());

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.POST)
                .setUrl(SERVER_ADDRESS + "/api/v1/users/token")
                .setBody(json)
                .setHeaders(headers);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                System.out.println("firebase token actualizado");
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("firebase token no actualizado");
            }
        });
    }


    /**
     * Invita a un usuario a un projecto
     * @param userEmail
     * @param projectId
     */
    public HttpRequest.Builder inviteUserToProject(String userEmail, String projectId){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("x-access-token", serverToken);

        HttpRequest.Builder builder = new HttpRequest.Builder();
        builder.setRequestMethod(HttpRequest.RequestMethod.PUT)
                .setUrl(SERVER_ADDRESS + "/api/v1/projects/" + projectId + "/invite/" + userEmail)
                .setHeaders(headers);

        return builder;

    }
}
