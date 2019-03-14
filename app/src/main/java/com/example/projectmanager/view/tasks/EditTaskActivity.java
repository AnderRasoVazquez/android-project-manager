package com.example.projectmanager.view.tasks;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.utils.DBFields;

import org.json.JSONException;
import org.json.JSONObject;

public class EditTaskActivity extends AppCompatActivity {

    int taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_task);


        this.taskId = getIntent().getExtras().getInt(DBFields.TABLE_TASKS_ID);
        System.out.println("el id del task es: " + taskId);

        setData();

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
                    json.put(DBFields.TABLE_TASKS_ID, taskId);
                    json.put(DBFields.TABLE_TASKS_NAME, name);
                    json.put(DBFields.TABLE_TASKS_DESC, desc);
                    json.put(DBFields.TABLE_TASKS_DUEDATE, dueDate);
                    json.put(DBFields.TABLE_TASKS_INITDATE, initDate);
                    json.put(DBFields.TABLE_TASKS_EXPECTED, expected);
                    json.put(DBFields.TABLE_TASKS_PROGRESS, progress);
//
                    DB.getInstance(getApplicationContext()).updateTask(json.toString());

                    setResult(RESULT_OK);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setData() {
        String response = DB.getInstance(getApplicationContext()).getTask(taskId);
        System.out.println(response);
        try {
            JSONObject json = new JSONObject(response);
            String name = json.getString(DBFields.TABLE_TASKS_NAME);
            String desc = json.getString(DBFields.TABLE_TASKS_DESC);
            desc = desc.equals("null") ? "" : desc;

            String dueDate = json.getString(DBFields.TABLE_TASKS_DUEDATE);
            dueDate = dueDate.equals("null") ? "" : dueDate;

            String initDate = json.getString(DBFields.TABLE_TASKS_INITDATE);
            initDate = initDate.equals("null") ? "" : initDate;

            double expected = json.getDouble(DBFields.TABLE_TASKS_EXPECTED);

            int progress = json.getInt(DBFields.TABLE_TASKS_PROGRESS);

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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }
}
