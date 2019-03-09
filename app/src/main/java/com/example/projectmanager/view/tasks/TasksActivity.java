package com.example.projectmanager.view.tasks;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.projectmanager.R;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.model.WorkSession;
import com.example.projectmanager.view.tasks.AdapterTasks;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class TasksActivity extends AppCompatActivity {

    ArrayList<Task> taskArrayList;
    RecyclerView recycler;

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

        populateArray();

        AdapterTasks adapter = new AdapterTasks(taskArrayList);
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

    private void populateArray() {
        taskArrayList = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i <= 30; i++) {
            taskArrayList.add(new Task(0, rand.nextInt(100),
                    "1.2 Deploy to production server", "Amazing! What a great description.", new Date(), 2000, new ArrayList<WorkSession>()));
        }
    }

}
