package com.example.projectmanager.view.projects;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.model.Project;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;
import com.example.projectmanager.view.tasks.TasksActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Interfaz para mostrar los proyectos del usuario.
 */
public class ProjectsActivity extends AppCompatActivity {

    ArrayList<Project> projectArrayList = new ArrayList<>();
    RecyclerView recycler;
    AdapterProjects adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_projects);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Crear proyecto.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject initProj = new JSONObject();
                try {
                    initProj.put(DBFields.TABLE_PROJECTS_NAME, getText(R.string.blankTitleProject));

                    createProject(initProj);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        recycler = findViewById(R.id.projectsReciclerView);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        populateArray();
        adapter = new AdapterProjects(projectArrayList);

        // Ir a las tareas del proyecto.
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = recycler.getChildLayoutPosition(v);
                Intent intent = new Intent(ProjectsActivity.this, TasksActivity.class);
                String projectID = projectArrayList.get(index).getId();
                intent.putExtra("id_project", projectID);
//                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        // Editar o borrar al hacer click prolongado.
        adapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final int index = recycler.getChildLayoutPosition(v);
                System.out.println(index);
                AlertDialog.Builder builder = new AlertDialog.Builder(ProjectsActivity.this);
                builder.setTitle(R.string.choose);
                CharSequence[] opciones = {getText(R.string.edit), getText(R.string.delete), getText(R.string.invite)};
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(ProjectsActivity.this, EditProjectActivity.class);
                            intent.putExtra(DBFields.TABLE_PROJECTS_ID, projectArrayList.get(index).getId());
                            startActivityForResult(intent, 0);
                        } else if (which == 1) {
                            deleteProject(projectArrayList.get(index).getId(), index);
                        } else if (which == 2) {
                            showInviteDialgo(index);
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        recycler.setAdapter(adapter);
    }

    private void showInviteDialgo(int index) {
        final String projectId = projectArrayList.get(index).getId();

        final AlertDialog.Builder alert = new AlertDialog.Builder(ProjectsActivity.this);

        final EditText editEmailDialog = new EditText(ProjectsActivity.this);

        alert.setTitle(R.string.invite);
        alert.setMessage(R.string.enterUserEmail);
        alert.setView(editEmailDialog);

        alert.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String email = editEmailDialog.getText().toString();
                if (!TextUtils.isEmpty(email)) {
                    HttpRequest.Builder builder = Facade.getInstance().inviteUserToProject(email, projectId);
                    builder.run(new OnConnectionSuccess() {
                        @Override
                        public void onSuccess(int statusCode, JSONObject json) {
                            System.out.println("usuario invitado");
                        }
                    }, new OnConnectionFailure() {
                        @Override
                        public void onFailure(int statusCode, JSONObject json) {
                            System.out.println("usuario no invitado");
                        }
                    });
                }
            }
        });
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void createProject(JSONObject json) {

        HttpRequest.Builder builder = Facade.getInstance().addProject(json);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                try {
                    String idAddedProject = json.getJSONObject("project").getString(DBFields.TABLE_PROJECTS_ID);
                    Intent intent = new Intent(ProjectsActivity.this, EditProjectActivity.class);
                    intent.putExtra(DBFields.TABLE_PROJECTS_ID, idAddedProject);
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
     * Llena el array con el contenido del proyecto.
     */
    private void populateArray() {
        HttpRequest.Builder builder = Facade.getInstance().getProjects();

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                System.out.println("Status code " + statusCode);
                try {
                    System.out.println(json.toString(4));

                    JSONArray jarray = json.getJSONArray("projects");

                    projectArrayList.clear();
                    for (int i = 0; i < jarray.length(); i++) {
                        JSONObject obj = jarray.getJSONObject(i);
                        String desc = obj.getString(DBFields.TABLE_PROJECTS_DESC);
                        desc = desc.equals("null")? "" : desc;
                        projectArrayList.add(
                                new Project(
                                        obj.getString(DBFields.TABLE_PROJECTS_ID),
                                        obj.getString(DBFields.TABLE_PROJECTS_NAME),
                                        desc
                                )
                        );
                    }
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    // TODO mostrar error
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
//            JSONObject jsonUser = new JSONObject();
////            jsonUser.put("email", email);
//            String stringResponse = DB.getInstance(getApplicationContext()).getProjects(jsonUser.toString());
//            JSONObject jsonResponse = new JSONObject(stringResponse);
//            JSONArray jarray = jsonResponse.getJSONArray("projects");
//
//            projectArrayList.clear();
//            for (int i = 0; i < jarray.length(); i++) {
//                JSONObject obj = jarray.getJSONObject(i);
//                String desc = obj.getString(DBFields.VIEW_PROJECTMEMBERS_DESCPROJECT);
//                desc = desc.equals("null")? "" : desc;
//                projectArrayList.add(
//                        new Project(
//                                obj.getInt(DBFields.VIEW_PROJECTMEMBERS_IDPROJECT),
//                                obj.getString(DBFields.VIEW_PROJECTMEMBERS_NAMEPROJECT),
//                                desc
//                        )
//                );
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Elimina un proyecto.
     * @param id
     * @param index
     */
    private void deleteProject(String id, final int index){

        HttpRequest.Builder builder = Facade.getInstance().deleteProject(id);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                projectArrayList.remove(index);
                adapter.notifyItemRemoved(index);
            }
        }, new OnConnectionFailure() {
            @Override
            public void onFailure(int statusCode, JSONObject json) {
                System.out.println("Connection failure!");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) { // proyecto añadido
            if (resultCode == RESULT_OK) {
                populateArray();
                adapter.notifyDataSetChanged();
            }
        }
    }
}
