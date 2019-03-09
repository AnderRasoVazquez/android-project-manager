package com.example.projectmanager;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.projectmanager.view.projects.ProjectsActivity;
import com.example.projectmanager.view.tasks.TasksActivity;

public class Debug extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug);

        setButtonOnClickActivity(R.id.btnGoProyects, ProjectsActivity.class);
        setButtonOnClickActivity(R.id.btnGoTasks, TasksActivity.class);
    }

    private void setButtonOnClickActivity(int idButton, final Class activityClass){
        Button btnLogin = findViewById(idButton);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Debug.this, activityClass);
                startActivity(i);
            }
        });

    }
}
