package com.example.projectmanager.view.worktime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.renderscript.ScriptGroup;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

    ArrayList<WorkSession> workSessionArrayList = new ArrayList<>();
    RecyclerView recycler;
    int projectId;
    String email;
    int taskId;
    EditText editDate, editHours;
    AdapterWork adapter;

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

        editDate = findViewById(R.id.editDate);
        DateUtils.addPopUpCalendar(editDate, this);
        editHours = findViewById(R.id.editHours);

        recycler = findViewById(R.id.workReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        projectId = getIntent().getExtras().getInt("id_project");
        email = getIntent().getExtras().getString("email");
        taskId = getIntent().getExtras().getInt("id_task");

        populateArray();
        adapter = new AdapterWork(workSessionArrayList);

        Button buttonAdd = findViewById(R.id.buttonAddWork);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = editDate.getText().toString();
                String hours = editHours.getText().toString();

                if (!TextUtils.isEmpty(date) && !TextUtils.isEmpty(hours)) {
                    double hoursDouble = Double.parseDouble(hours);
                    try {
                        JSONObject json = buildJSON(date, hoursDouble);
                        DB.getInstance(getApplicationContext()).addWork(json.toString());
                        populateArray();
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recycler.getChildLayoutPosition(v);

                final EditText editHourForDialog = new EditText(WorkActivity.this);
                editHourForDialog.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                editHourForDialog.setText(Double.toString(workSessionArrayList.get(index).getTime()));

                final AlertDialog.Builder alert = new AlertDialog.Builder(WorkActivity.this);
                final String date = DateUtils.toString( workSessionArrayList.get(index).getDate());

                alert.setMessage(date);
                alert.setTitle(R.string.changeHours);
                alert.setView(editHourForDialog);

                alert.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String text = editHourForDialog.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            double hoursDouble = Double.parseDouble(text);
                            try {
                                JSONObject json = buildJSON(date, hoursDouble);
                                DB.getInstance(getApplicationContext()).addWork(json.toString());
                                populateArray();
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

                alert.show();
            }
        });

        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int index = recycler.getChildLayoutPosition(v);
                System.out.println(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(WorkActivity.this);
                builder.setTitle(R.string.choose);
                CharSequence[] opciones = {getText(R.string.delete)};
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
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
            System.out.println("WORK TIME JSON");
            System.out.println(stringResponse);
            JSONObject jsonResponse = new JSONObject(stringResponse);
            JSONArray jarray = jsonResponse.getJSONArray("worktime");

            workSessionArrayList.clear();
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

    private JSONObject buildJSON(String date, double hours) throws JSONException {
        JSONObject json = new JSONObject();

        json.put(DBFields.TABLE_WORKTIME_IDUSER, email);
        json.put(DBFields.TABLE_WORKTIME_IDPROJECT, projectId);
        json.put(DBFields.TABLE_WORKTIME_IDTASK, taskId);
        json.put(DBFields.TABLE_WORKTIME_DATE, date);
        json.put(DBFields.TABLE_WORKTIME_HOURS, hours);

        return json;
    }

}
