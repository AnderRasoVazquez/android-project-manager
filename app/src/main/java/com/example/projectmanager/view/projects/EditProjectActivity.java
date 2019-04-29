package com.example.projectmanager.view.projects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Actividad para editar proyectos.
 */
public class EditProjectActivity extends AppCompatActivity {

    String projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        this.projectId = getIntent().getExtras().getString(DBFields.TABLE_PROJECTS_ID);

        setData();

        // Guardar proyecto.
        Button button = findViewById(R.id.saveProjectButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
                String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();

                if (name.isEmpty()){
                    return;
                }

                try {
                    json.put(DBFields.TABLE_PROJECTS_NAME, name);
                    if (!desc.isEmpty()){
                        json.put(DBFields.TABLE_PROJECTS_DESC, desc);
                    }

                    editProject(projectId, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Edita el proyecto
     * @param projectId
     * @param json
     */
    private void editProject(String projectId, JSONObject json) {
        HttpRequest.Builder builder = Facade.getInstance().modProject(projectId, json);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                setResult(RESULT_OK);
                finish();
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
    }

    /**
     * Introducir datos en los campos.
     */
    private void setData() {
        System.out.println("MANDANDO PETICION DE RECIBIR PROYECTO: " + projectId);
        HttpRequest.Builder builder = Facade.getInstance().getProject(projectId);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                try {
                    JSONObject projson = json.getJSONObject("project");
                    String name = projson.getString(DBFields.TABLE_PROJECTS_NAME);
                    String desc = projson.getString(DBFields.TABLE_PROJECTS_DESC);

                    desc = desc.equals("null") ? "" : desc;

                    ((TextView) findViewById(R.id.txtTaskName)).setText(name);
                    ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });



//        String response = DB.getInstance(getApplicationContext()).getProject(projectId);
//        System.out.println(response);
//        try {
//            JSONObject json = new JSONObject(response);
//            String name = json.getString(DBFields.TABLE_PROJECTS_NAME);
//            String desc = json.getString(DBFields.TABLE_PROJECTS_DESC);
//
//            desc = desc.equals("null") ? "" : desc;
//
//            ((TextView) findViewById(R.id.txtTaskName)).setText(name);
//            ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
