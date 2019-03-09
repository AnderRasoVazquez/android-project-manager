package com.example.projectmanager.view.projects;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.projectmanager.R;
import com.example.projectmanager.model.Project;
import com.example.projectmanager.view.projects.AdapterProjects;

import java.util.ArrayList;

public class ProjectsActivity extends AppCompatActivity {

    ArrayList<Project> projectArrayList;
    RecyclerView recycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        recycler = findViewById(R.id.projectsReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        populateArray();

        AdapterProjects adapter = new AdapterProjects(projectArrayList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recycler.getChildLayoutPosition(v);
                System.out.println("Esta es la posicion");
                System.out.println(index);
            }
        });
        recycler.setAdapter(adapter);
    }

    /**
     * Llena el array con el contenido del proyecto
     */
    private void populateArray() {
        projectArrayList = new ArrayList<>();
        for (int i = 0; i <= 30; i++) {
            projectArrayList.add(new Project(2, "New title", "Another description"));
        }
    }


}
