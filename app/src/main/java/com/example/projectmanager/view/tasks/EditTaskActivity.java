package com.example.projectmanager.view.tasks;

import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.DateUtils;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import static com.example.projectmanager.utils.DateUtils.addPopUpCalendar;

/**
 * Actividad para editar tareas.
 */
public class EditTaskActivity extends AppCompatActivity {

    String taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);


        this.taskId = getIntent().getExtras().getString(DBFields.TABLE_TASKS_ID);
        System.out.println("el id del task es: " + taskId);

        final TextView editDueDate = findViewById(R.id.editDueDate);
        TextView editWaitDate = findViewById(R.id.editWaitDate);

        // Actualizar la info de progreso al mover la barra.
        SeekBar progressBar = findViewById(R.id.progressBar);
        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TextView progressText = findViewById(R.id.textProgress);
                progressText.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Añadir dialogos de calendarios al hacer click.
        addPopUpCalendar(editDueDate, EditTaskActivity.this);
        addPopUpCalendar(editWaitDate, EditTaskActivity.this);

        if (savedInstanceState == null) {
            setData();
        }

        // Guardar los datos.
        Button button = findViewById(R.id.saveTaskButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();

                String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
                String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();
                String dueDate = ((TextView) findViewById(R.id.editDueDate)).getText().toString();
                String initDate = ((TextView) findViewById(R.id.editWaitDate)).getText().toString();
                double expected = Double.parseDouble(((TextView) findViewById(R.id.editExpected)).getText().toString());
                int progress = ((SeekBar) findViewById(R.id.progressBar)).getProgress();

                try {
                    if (name.isEmpty()) {
                        return;
                    }
                    json.put(DBFields.TABLE_TASKS_NAME, name);

                    if (!desc.isEmpty()){
                        json.put(DBFields.TABLE_TASKS_DESC, desc);
                    }
                    if (!dueDate.isEmpty()) {
                        json.put(DBFields.TABLE_TASKS_DUEDATE, dueDate);
                    }
                    if (!initDate.isEmpty()) {
                        json.put(DBFields.TABLE_TASKS_INITDATE, initDate);
                    }
                    json.put(DBFields.TABLE_TASKS_EXPECTED, expected);
                    json.put(DBFields.TABLE_TASKS_PROGRESS, progress);

                    editTask(taskId, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        // Abrir calendario.
        Button btnCalendar = findViewById(R.id.buttonCalendar);
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent (Intent.ACTION_VIEW, android.net.Uri.parse("content://com.android.calendar/time/")));
                addEventToCalendar();
            }
        });
    }

    private void addEventToCalendar(){
        String dueDate = ((TextView) findViewById(R.id.editDueDate)).getText().toString();
        if (dueDate.isEmpty()) {
            return;
        }
        String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
        String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();

        Date due;
        try {
            due = DateUtils.toDate(dueDate);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Calendar beginTime = Calendar.getInstance();
        beginTime.setTimeInMillis(due.getTime());
//        Calendar endTime = Calendar.getInstance();
//        endTime.setTimeInMillis(when+3600000);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
//                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, name)
                .putExtra(CalendarContract.Events.DESCRIPTION, desc);
//                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
        startActivity(intent);
    }

    /**
     * Edita la tarea
     * @param taskId
     * @param json
     */
    private void editTask(String taskId, JSONObject json) {
        HttpRequest.Builder builder = Facade.getInstance().modTask(taskId, json);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                setResult(RESULT_OK);
                finish();
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
    }

    /**
     * Introducir los datos de la tarea.
     */
    private void setData() {
        HttpRequest.Builder builder = Facade.getInstance().getTask(taskId);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                try {
                    JSONObject taskJson = json.getJSONObject("task");
                    String name = taskJson.getString(DBFields.TABLE_TASKS_NAME);
                    String desc = taskJson.getString(DBFields.TABLE_TASKS_DESC);
                    desc = desc.equals("null") ? "" : desc;

                    String dueDate = taskJson.getString(DBFields.TABLE_TASKS_DUEDATE);
                    dueDate = dueDate.equals("null") ? "" : dueDate;

                    String initDate = taskJson.getString(DBFields.TABLE_TASKS_INITDATE);
                    initDate = initDate.equals("null") ? "" : initDate;

                    double expected = taskJson.getDouble(DBFields.TABLE_TASKS_EXPECTED);

                    int progress = taskJson.getInt(DBFields.TABLE_TASKS_PROGRESS);

                    ((TextView) findViewById(R.id.txtTaskName)).setText(name);
                    ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);
                    ((TextView) findViewById(R.id.editDueDate)).setText(dueDate);
                    ((TextView) findViewById(R.id.editWaitDate)).setText(initDate);
                    ((TextView) findViewById(R.id.editExpected)).setText(Double.toString(expected));
                    ((SeekBar) findViewById(R.id.progressBar)).setProgress(progress);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });



//        String response = DB.getInstance(getApplicationContext()).getTask(taskId);
//        System.out.println(response);
//        try {
//            JSONObject json = new JSONObject(response);
//            String name = json.getString(DBFields.TABLE_TASKS_NAME);
//            String desc = json.getString(DBFields.TABLE_TASKS_DESC);
//            desc = desc.equals("null") ? "" : desc;
//
//            String dueDate = json.getString(DBFields.TABLE_TASKS_DUEDATE);
//            dueDate = dueDate.equals("null") ? "" : dueDate;
//
//            String initDate = json.getString(DBFields.TABLE_TASKS_INITDATE);
//            initDate = initDate.equals("null") ? "" : initDate;
//
//            double expected = json.getDouble(DBFields.TABLE_TASKS_EXPECTED);
//
//            int progress = json.getInt(DBFields.TABLE_TASKS_PROGRESS);
//
//            ((TextView) findViewById(R.id.txtTaskName)).setText(name);
//            ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);
//            ((TextView) findViewById(R.id.editDueDate)).setText(dueDate);
//            ((TextView) findViewById(R.id.editWaitDate)).setText(initDate);
//            ((TextView) findViewById(R.id.editExpected)).setText(Double.toString(expected));
//            ((SeekBar) findViewById(R.id.progressBar)).setProgress(progress);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
        String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();
        String dueDate = ((TextView) findViewById(R.id.editDueDate)).getText().toString();
        String initDate = ((TextView) findViewById(R.id.editWaitDate)).getText().toString();
        double expected = Double.parseDouble(((TextView) findViewById(R.id.editExpected)).getText().toString());
        int progress = ((SeekBar) findViewById(R.id.progressBar)).getProgress();


        outState.putString("name", name);
        outState.putString("desc", desc);
        outState.putString("due_date", dueDate);
        outState.putString("init_date", initDate);
        outState.putDouble("expected", expected);
        outState.putInt("progress", progress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ((TextView) findViewById(R.id.txtTaskName)).setText(savedInstanceState.getString("name"));
        ((TextView) findViewById(R.id.txtTaskDesc)).setText(savedInstanceState.getString("desc"));
        ((TextView) findViewById(R.id.editDueDate)).setText(savedInstanceState.getString("due_date"));
        ((TextView) findViewById(R.id.editWaitDate)).setText(savedInstanceState.getString("init_date"));
        String expected = Double.toString(savedInstanceState.getDouble("expected"));
        ((TextView) findViewById(R.id.editExpected)).setText(expected);
        ((SeekBar) findViewById(R.id.progressBar)).setProgress(savedInstanceState.getInt("progress"));
    }

}
