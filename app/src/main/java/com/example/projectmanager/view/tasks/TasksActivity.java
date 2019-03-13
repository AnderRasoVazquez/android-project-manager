package com.example.projectmanager.view.tasks;

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
import com.example.projectmanager.model.Task;
import com.example.projectmanager.model.WorkSession;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.view.tasks.AdapterTasks;
import com.example.projectmanager.view.worktime.WorkActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class TasksActivity extends AppCompatActivity {

    ArrayList<Task> taskArrayList;
    RecyclerView recycler;
    int projectId;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
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

        recycler = findViewById(R.id.tasksReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        projectId = getIntent().getExtras().getInt("id_project");
        email = getIntent().getExtras().getString("email");
        populateArray();

        final AdapterTasks adapter = new AdapterTasks(taskArrayList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recycler.getChildLayoutPosition(v);
                int taskId = taskArrayList.get(index).getId();
                Intent intent = new Intent(TasksActivity.this, WorkActivity.class);
                intent.putExtra("id_project", projectId);
                intent.putExtra("email", email);
                intent.putExtra("id_task", taskId);
                startActivity(intent);
            }
        });

        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int index = recycler.getChildLayoutPosition(v);
                System.out.println(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(TasksActivity.this);
                builder.setTitle(R.string.choose);
                CharSequence[] opciones = {getText(R.string.edit), getText(R.string.delete)};
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            System.out.println(R.string.edit);
                        } else if (which == 1) {
                            System.out.println(R.string.delete);
                            DB.getInstance(TasksActivity.this).deleteTask(taskArrayList.get(index).getId());
                            taskArrayList.remove(index);
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

    private void populateArray() {
        try {
            JSONObject jsonProject = new JSONObject();
            jsonProject.put("id_project", projectId);
            String stringResponse = DB.getInstance(getApplicationContext()).getTasks(jsonProject.toString());
            System.out.println("LOLOLOL");
            System.out.println(stringResponse);
            JSONObject jsonResponse = new JSONObject(stringResponse);
            JSONArray jarray = jsonResponse.getJSONArray("tasks");

//            public static final String TABLE_TASKS = "tasks";
//            public static final String TABLE_TASKS_ID = "id";
//            public static final String TABLE_TASKS_NAME = "name";
//            public static final String TABLE_TASKS_DESC = "desc";
//            public static final String TABLE_TASKS_DUEDATE = "due_date";
//            public static final String TABLE_TASKS_INITDATE = "init_date";
//            public static final String TABLE_TASKS_EXPECTED = "expected";
//            public static final String TABLE_TASKS_PROGRESS = "progress";
//            public static final String TABLE_TASKS_IDPROJECT = "id_project";
            taskArrayList= new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject obj = jarray.getJSONObject(i);
                // TODO ahora no es null pero hay que manejar que algun campo lo sea
//                String desc = obj.getString(DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT);
//                desc = desc.equals("null")? getString(R.string.blankDescriptionProject) : desc;

//    public Task(int id, int progress, String name, String desc, Date due, Date init, double expected, ArrayList<WorkSession> workSessions) {
                taskArrayList.add(
                        new Task(
                                obj.getInt(DBFields.TABLE_TASKS_ID),
                                obj.getInt(DBFields.TABLE_TASKS_PROGRESS),
                                obj.getString(DBFields.TABLE_TASKS_NAME),
                                obj.getString(DBFields.TABLE_TASKS_DESC),

                                // TODO corregir
                                new Date(),
                                new Date(),
//                                obj.getString(DBFields.TABLE_TASKS_DUEDATE),
//                                obj.getString(DBFields.TABLE_TASKS_INITDATE),
                                obj.getDouble(DBFields.TABLE_TASKS_EXPECTED),
                                // TODO calcular en la BD
                                0
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
