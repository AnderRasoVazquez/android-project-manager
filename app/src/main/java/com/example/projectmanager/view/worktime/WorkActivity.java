package com.example.projectmanager.view.worktime;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.model.WorkSession;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.DateUtils;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;

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
    String taskId;
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

        taskId = getIntent().getExtras().getString("id_task");
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
                        JSONObject json = buildCreateWorkJSON(date, hoursDouble);
                        createWork(taskId, json);
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
                final int index = recycler.getChildLayoutPosition(v);

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
                                JSONObject json = buildModWorkJSON(hoursDouble);
                                modWork(workSessionArrayList.get(index).getId(), json);
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
                            deleteWork(workSessionArrayList.get(index).getId(), index);
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
     * Peticion para modificar un trabajo.
     * @param workId
     * @param json
     */
    private void modWork(String workId, JSONObject json){

        HttpRequest.Builder builder = Facade.getInstance().modWork(workId, json);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                populateArray();
                adapter.notifyDataSetChanged();
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
        updateTotalTime();
    }
    /**
     * Peticion para crear un trabajo.
     * @param taskId
     * @param json
     */
    private void createWork(String taskId, JSONObject json){

        HttpRequest.Builder builder = Facade.getInstance().addWork(taskId, json);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                populateArray();
                adapter.notifyDataSetChanged();
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
        updateTotalTime();
    }

    /**
     * Elimina un tiempo trabajado.
     * @param id
     * @param index
     */
    private void deleteWork(String id, final int index){

        HttpRequest.Builder builder = Facade.getInstance().deleteWork(id);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                workSessionArrayList.remove(index);
                adapter.notifyItemRemoved(index);
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
        updateTotalTime();
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
        HttpRequest.Builder builder = Facade.getInstance().getWorks(taskId);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                System.out.println("Status code " + statusCode);
                try {
                    System.out.println(json.toString(4));

                    JSONArray jarray = json.getJSONArray("works");

                    workSessionArrayList.clear();
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject obj = jarray.getJSONObject(i);
                        String id = obj.getString(DBFields.TABLE_WORKTIME_ID);
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
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    // TODO mostrar error
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
//        try {
//
//            JSONObject jsonTask = new JSONObject();
//            jsonTask.put("id_task", taskId);
//            String stringResponse = DB.getInstance(getApplicationContext()).getWorkedTime(jsonTask.toString());
//            System.out.println("WORK TIME JSON");
//            System.out.println(stringResponse);
//            JSONObject jsonResponse = new JSONObject(stringResponse);
//            JSONArray jarray = jsonResponse.getJSONArray("worktime");
//
//            workSessionArrayList.clear();
//            for (int i = 0; i < jarray.length(); i++) {
//                JSONObject obj = jarray.getJSONObject(i);
//                String id = obj.getString(DBFields.TABLE_WORKTIME_ID);
//                String dateString = obj.getString(DBFields.TABLE_WORKTIME_DATE);
//                Date date = DateUtils.toDate(dateString);
//                double timeWorked = obj.getDouble(DBFields.TABLE_WORKTIME_HOURS);
//
//                workSessionArrayList.add(
//                        new WorkSession(
//                                id,
//                                date,
//                                timeWorked
//                        )
//                );
//            }
//            updateTotalTime();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Construye un objeto JSON dada una fecha y sus horas.
     * @param date
     * @param hours
     * @return
     * @throws JSONException
     */
    private JSONObject buildCreateWorkJSON(String date, double hours) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(DBFields.TABLE_WORKTIME_DATE, date);
        json.put(DBFields.TABLE_WORKTIME_HOURS, hours);
        return json;
    }

    /**
     * Construye un objeto JSON dada sus horas.
     * @param hours
     * @return
     * @throws JSONException
     */
    private JSONObject buildModWorkJSON(double hours) throws JSONException {
        JSONObject json = new JSONObject();
        json.put(DBFields.TABLE_WORKTIME_HOURS, hours);
        return json;
    }
}
