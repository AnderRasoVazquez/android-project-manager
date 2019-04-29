package com.example.projectmanager.view.tasks;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.model.Task;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.DateUtils;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;
import com.example.projectmanager.view.worktime.WorkActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Actividad para mostrar tareas.
 */
public class TasksActivity extends AppCompatActivity {

    ArrayList<Task> taskArrayList = new ArrayList<>();
    RecyclerView recycler;
    String projectId;
    AdapterTasks adapter;
    boolean showCompletedTasks = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        projectId = getIntent().getExtras().getString("id_project");

        final FloatingActionButton btnShowFinished = findViewById(R.id.taskShowCompleted);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Dependiendo de las preferencias el Button tiene diferente icono
        this.showCompletedTasks = prefs.getBoolean("show_completed", true);
        if (showCompletedTasks) {
            btnShowFinished.setImageResource(R.drawable.ic_visibility_black_24dp);
        } else {
            btnShowFinished.setImageResource(R.drawable.ic_visibility_off_black_24dp);
        }

        // Cambiar preferencia de ver tareas completadas al hacer click en el Button
        btnShowFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor edit = prefs.edit();
                String message;
                if (showCompletedTasks) {
                    btnShowFinished.setImageResource(R.drawable.ic_visibility_off_black_24dp);
                    edit.putBoolean("show_completed", false);
                    message = getText(R.string.toastDontShowCompleted).toString();
                } else {
                    btnShowFinished.setImageResource(R.drawable.ic_visibility_black_24dp);
                    edit.putBoolean("show_completed", true);
                    message = getText(R.string.toastShowCompleted).toString();
                }
                showCompletedTasks = !showCompletedTasks;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                edit.apply();
                populateArray();
                adapter.notifyDataSetChanged();
            }
        });


        // Añadir tarea nueva.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject initTask = new JSONObject();
                try {
                    initTask.put(DBFields.TABLE_TASKS_NAME, getText(R.string.blankTitleTask));

                    createTask(projectId, initTask);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        recycler = findViewById(R.id.tasksReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        populateArray();

        // Ver tiempos trabajados.
        adapter = new AdapterTasks(taskArrayList);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recycler.getChildLayoutPosition(v);
                String taskId = taskArrayList.get(index).getId();
                Intent intent = new Intent(TasksActivity.this, WorkActivity.class);
                intent.putExtra("id_task", taskId);
                startActivity(intent);
            }
        });

        // Editar o borrar tarea.
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
                            Intent intent = new Intent(TasksActivity.this, EditTaskActivity.class);
                            intent.putExtra(DBFields.TABLE_TASKS_ID, taskArrayList.get(index).getId());
                            startActivityForResult(intent, 0);
                        } else if (which == 1) {
                            System.out.println(R.string.delete);
                            deleteTask(taskArrayList.get(index).getId(), index);
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
     * Crea una tarea
     * @param id
     * @param json
     */
    private void createTask(String id, JSONObject json){

        HttpRequest.Builder builder = Facade.getInstance().addTask(id, json);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                String idAddedTask = null;
                try {
                    idAddedTask = json.getJSONObject("task").getString(DBFields.TABLE_TASKS_ID);
                    Intent intent = new Intent(TasksActivity.this, EditTaskActivity.class);
                    intent.putExtra(DBFields.TABLE_TASKS_ID, idAddedTask);
                    startActivityForResult(intent, 0);
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
    }

    /**
     * Elimina una tarea.
     * @param id
     * @param index
     */
    private void deleteTask(String id, final int index){

        HttpRequest.Builder builder = Facade.getInstance().deleteTask(id);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                taskArrayList.remove(index);
                adapter.notifyItemRemoved(index);
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
    }

    /**
     * Rellenar el array de tareas.
     */
    private void populateArray() {
        HttpRequest.Builder builder = Facade.getInstance().getTasks(projectId);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                System.out.println("Status code " + statusCode);
                try {
                    JSONArray jarray = json.getJSONArray("tasks");

                    taskArrayList.clear();
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject obj = jarray.getJSONObject(i);
                        int progress = obj.getInt(DBFields.TABLE_TASKS_PROGRESS);

                        if (!showCompletedTasks) {
                            if (progress == 100) {
                                continue;
                            }
                        }

                        String desc = obj.getString(DBFields.TABLE_TASKS_DESC);
                        desc = desc.equals("null") ? null : desc;
                        String dueDate = obj.getString(DBFields.TABLE_TASKS_DUEDATE);
                        Date due;
                        if (dueDate.equals("null") || dueDate.equals("")) {
                            due = null;
                        } else {
                            due = DateUtils.toDate(dueDate);
                        }

                        String initDate = obj.getString(DBFields.TABLE_TASKS_INITDATE);
                        Date init;
                        if (initDate.equals("null") || initDate.equals("")) {
                            init = null;
                        } else {
                            init = DateUtils.toDate(initDate);
                        }

                        taskArrayList.add(
                                new Task(
                                        obj.getString(DBFields.TABLE_TASKS_ID),
                                        progress,
                                        obj.getString(DBFields.TABLE_TASKS_NAME),
                                        desc,
                                        due,
                                        init,
                                        obj.getDouble(DBFields.TABLE_TASKS_EXPECTED)
                                )
                        );

                    }
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
//            JSONObject jsonProject = new JSONObject();
//            jsonProject.put("id_project", projectId);
//            String stringResponse = DB.getInstance(getApplicationContext()).getTasks(jsonProject.toString());
//            JSONObject jsonResponse = new JSONObject(stringResponse);
//            JSONArray jarray = jsonResponse.getJSONArray("tasks");
//
//            taskArrayList.clear();
//            for (int i = 0; i < jarray.length(); i++) {
//                JSONObject obj = jarray.getJSONObject(i);
//                int progress = obj.getInt(DBFields.TABLE_TASKS_PROGRESS);
//
//                if (!showCompletedTasks) {
//                    if (progress == 100) {
//                        continue;
//                    }
//                }
//
//                String desc = obj.getString(DBFields.TABLE_TASKS_DESC);
//                desc = desc.equals("null")? null : desc;
//                String dueDate = obj.getString(DBFields.TABLE_TASKS_DUEDATE);
//                Date due;
//                if (dueDate.equals("null") || dueDate.equals("")) {
//                    due = null;
//                } else {
//                    due = DateUtils.toDate(dueDate);
//                }
//
//                String initDate = obj.getString(DBFields.TABLE_TASKS_INITDATE);
//                Date init;
//                if (initDate.equals("null") || initDate.equals("")) {
//                    init = null;
//                } else {
//                    init = DateUtils.toDate(initDate);
//                }
//
//                taskArrayList.add(
//                        new Task(
//                                obj.getInt(DBFields.TABLE_TASKS_ID),
//                                progress,
//                                obj.getString(DBFields.TABLE_TASKS_NAME),
//                                desc,
//                                due,
//                                init,
//                                obj.getDouble(DBFields.TABLE_TASKS_EXPECTED)
//                        )
//                );
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) { // proyecto añadido
            if (resultCode == RESULT_OK) {
                System.out.println("he llegado aqui");
                populateArray();
                adapter.notifyDataSetChanged();
            }
        }
    }
}
