package com.example.projectmanager.view.worktime;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.renderscript.ScriptGroup;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
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
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.model.WorkSession;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.DateUtils;
import com.example.projectmanager.view.login.LoginActivity;
import com.example.projectmanager.view.projects.ProjectsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Actividad para mostrar los tiempos trabajados.
 */
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

        //
        long startTime = getIntent().getExtras().getLong("startTime");
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        System.out.println("elapsed");
        System.out.println(elapsedTime);
        //

        editDate = findViewById(R.id.editDate);
        DateUtils.addPopUpCalendar(editDate, this);
        editHours = findViewById(R.id.editHours);

        recycler = findViewById(R.id.workReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        projectId = getIntent().getExtras().getInt("id_project");
        email = getIntent().getExtras().getString("email");
        taskId = getIntent().getExtras().getInt("id_task");
        System.out.println("projectId " + projectId);
        System.out.println("email " + email);
        System.out.println("taskId " + taskId);

        populateArray();
        adapter = new AdapterWork(workSessionArrayList);

        // Añadir tiempo trabajado.
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

        // Editar tiempo trabajado.
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

        // Borrar tiempo trabajado.
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
                            updateTotalTime();
                        }
                    }
                });
                builder.show();
                return true;
            }
        });
        recycler.setAdapter(adapter);


        // Notificaciones
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        final NotificationCompat.Builder theBuilder = new NotificationCompat.Builder(this, "IdCanal");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(elCanal);
        }

        // Mostrar notificacion al hacer click
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                theBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle(getText(R.string.reminderTitle))
                        .setContentText(getText(R.string.reminderDesc))
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true);
                notificationManager.notify(1, theBuilder.build());
                Snackbar.make(view, R.string.reminderSet, Snackbar.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Actualizar el total del tiempo trabajado.
     */
    private void updateTotalTime() {
        TextView totalWorked = findViewById(R.id.txtTimeWorked);
        double totalTime = 0;
        for (WorkSession w: workSessionArrayList) {
            totalTime += w.getTime();
        }
        totalWorked.setText(Double.toString(totalTime));
    }

    /**
     * Rellenar el array de tiempos trabajados.
     */
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
            updateTotalTime();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Construye un objeto JSON dada una fecha y sus horas.
     * @param date
     * @param hours
     * @return
     * @throws JSONException
     */
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
