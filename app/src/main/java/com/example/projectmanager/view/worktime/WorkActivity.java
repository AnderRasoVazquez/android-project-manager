package com.example.projectmanager.view.worktime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.model.WorkSession;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class WorkActivity extends AppCompatActivity {

    ArrayList<WorkSession> workSessionArrayList;
    RecyclerView recycler;
    int projectId;
    String email;
    int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work);
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

        recycler = findViewById(R.id.workReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        projectId = getIntent().getExtras().getInt("id_project");
        email = getIntent().getExtras().getString("email");
        taskId = getIntent().getExtras().getInt("id_task");

        populateArray();

        final AdapterWork adapter = new AdapterWork(workSessionArrayList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO editar?
                int index = recycler.getChildLayoutPosition(v);
                System.out.println("Click corto");
                System.out.println(index);
            }
        });

        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int index = recycler.getChildLayoutPosition(v);
                System.out.println(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkActivity.this);
                builder.setTitle(R.string.choose);
                CharSequence[] opciones = {getText(R.string.edit), getText(R.string.delete)};
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            System.out.println(R.string.edit);
                        } else if (which == 1) {
                            System.out.println(R.string.delete);

                            DB.getInstance(WorkActivity.this).deleteWork(workSessionArrayList.get(index).getId());

                            workSessionArrayList.remove(index);
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
            JSONObject jsonTask = new JSONObject();
            jsonTask.put("id_task", taskId);
            String stringResponse = DB.getInstance(getApplicationContext()).getWorkedTime(jsonTask.toString());
            JSONObject jsonResponse = new JSONObject(stringResponse);
            JSONArray jarray = jsonResponse.getJSONArray("worktime");

            workSessionArrayList = new ArrayList<>();
            for (int i = 0; i < jarray.length(); i++) {
                JSONObject obj = jarray.getJSONObject(i);
                int id = obj.getInt(DBFields.TABLE_WORKTIME_ID);
                String dateString = obj.getString(DBFields.TABLE_WORKTIME_DATE);
                Date date = DateUtils.toDate(dateString);
                double timeWorked = obj.getDouble(DBFields.TABLE_WORKTIME_HOURS);

                workSessionArrayList.add(
                        new WorkSession(
                                id,
                                date,
                                timeWorked
                        )
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
