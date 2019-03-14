package com.example.projectmanager.view.projects;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.utils.DBFields;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProjectActivity extends AppCompatActivity {

    int projectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        this.projectId = getIntent().getExtras().getInt(DBFields.TABLE_PROJECTS_ID);
        System.out.println("el id del proyecto es: " + projectId);

        setData();

        Button button = findViewById(R.id.saveProjectButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
                String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();

                try {
                    json.put(DBFields.TABLE_PROJECTS_ID, projectId);
                    json.put(DBFields.TABLE_PROJECTS_NAME, name);
                    json.put(DBFields.TABLE_PROJECTS_DESC, desc);

                    DB.getInstance(getApplicationContext()).updateProject(json.toString());
                    setResult(RESULT_OK);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setData() {
        String response = DB.getInstance(getApplicationContext()).getProject(projectId);
        System.out.println(response);
        try {
            JSONObject json = new JSONObject(response);
            String name = json.getString(DBFields.TABLE_PROJECTS_NAME);
            String desc = json.getString(DBFields.TABLE_PROJECTS_DESC);

            desc = desc.equals("null") ? "" : desc;

            ((TextView) findViewById(R.id.txtTaskName)).setText(name);
            ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
