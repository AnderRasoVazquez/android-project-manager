package com.example.projectmanager.view.projects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.model.Project;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.view.projects.AdapterProjects;
import com.example.projectmanager.view.tasks.TasksActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProjectsActivity extends AppCompatActivity {

    ArrayList<Project> projectArrayList;
    RecyclerView recycler;
    String email;

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

        this.email = getIntent().getExtras().getString("email");
        populateArray();

        final AdapterProjects adapter = new AdapterProjects(projectArrayList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recycler.getChildLayoutPosition(v);
                Intent intent = new Intent(ProjectsActivity.this, TasksActivity.class);
                int projectID = projectArrayList.get(index).getId();
                intent.putExtra("id_project", projectID);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int index = recycler.getChildLayoutPosition(v);
                System.out.println(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(ProjectsActivity.this);
                builder.setTitle(R.string.choose);
                CharSequence[] opciones = {getText(R.string.edit), getText(R.string.delete)};
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            System.out.println("edit " + index);
                        } else if (which == 1) {
                            System.out.println("remove " + index);
                            DB.getInstance(ProjectsActivity.this).deleteProject(projectArrayList.get(index).getId());
                            projectArrayList.remove(index);
                            adapter.notifyItemRemoved(index);
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        recycler.setAdapter(adapter);
    }

    /**
     * Llena el array con el contenido del proyecto
     */
    private void populateArray() {
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("email", email);
            String stringResponse = DB.getInstance(getApplicationContext()).getProjects(jsonUser.toString());
            JSONObject jsonResponse = new JSONObject(stringResponse);
            JSONArray jarray = jsonResponse.getJSONArray("projects");

            projectArrayList = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject obj = jarray.getJSONObject(i);
                String desc = obj.getString(DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT);
                desc = desc.equals("null")? getString(R.string.blankDescriptionProject) : desc;
                projectArrayList.add(
                        new Project(
                                obj.getInt(DBFields.VIEW_PROJECTMEMBERS_IDPROJECT),
                                obj.getString(DBFields.VIEW_PROJECTMEMBERS_NAMEPROJECT),
                                desc
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
