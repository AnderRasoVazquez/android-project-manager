package com.example.projectmanager.view.login;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;
import com.example.projectmanager.view.projects.ProjectsActivity;
import com.example.projectmanager.view.register.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;

/**
 * Actividad inicial. Se utiliza para hacer login en la app.
 */
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button registerButton = findViewById(R.id.btnGoRegister);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        final Button loginButton = findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editEmail = findViewById(R.id.editEmail);
                EditText editPass = findViewById(R.id.editPassword);

                String email = editEmail.getText().toString();
                String pass = editPass.getText().toString();
                JSONObject loginJson = new JSONObject();
                String loginString = null;
                try {
                    loginJson.put("email", email);
                    loginJson.put("pass", pass);
                    loginString = loginJson.toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                String server = "http://192.168.1.128:5000";
////              String server = "https://proyecto-das.herokuapp.com";
//                String apiLogin = "/api/v1/login";
//                String direccion = server + apiLogin;
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("Authorization", getBasicAuth(email, pass));

//                HttpRequest.Builder builder = new HttpRequest.Builder();

                HttpRequest.Builder builder = Facade.getInstance().login(email, pass);

                builder.run(new OnConnectionSuccess() {
                            @Override
                            public void onSuccess(int statusCode, JSONObject json) {
                                System.out.println("Status code " + statusCode);
                                try {
                                    System.out.println(json.toString(4));
                                    // guardar token
                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor edit = prefs.edit();
                                    String serverToken = json.getString("token");
                                    edit.putString("server_token", serverToken);
                                    edit.apply();
                                    Facade.getInstance().setServerToken(serverToken);
                                    // ir a projectos
                                    Intent intent = new Intent(LoginActivity.this, ProjectsActivity.class);
                                    startActivity(intent);
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

//                conexionBDWebService c = new conexionBDWebService();
//                c.execute();

//                boolean success = true;
//
//                if (!success) {
//                    Toast toast = Toast.makeText(getApplicationContext(), R.string.cantLogin, Toast.LENGTH_SHORT);
//                    toast.show();
//                } else {
//                    Intent intent = new Intent(LoginActivity.this, ProjectsActivity.class);
//                    intent.putExtra("email", email);
//                    startActivity(intent);
//                }
            }
        });


        ImageButton exportButton = findViewById(R.id.buttonExport);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportDatabase();
            }
        });
    }

    /**
     * Exporta la base de datos.
     */
    private void exportDatabase() {
        // TODO peticion para recuperar base de datos
//        try {
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
//            } else {
//                File download_folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//
//                if (download_folder.canWrite()) {
//                    String backupDBPath = "project.db";
//                    File currentDB = getDatabasePath(DB.getInstance(getApplicationContext()).getDatabaseName());
//                    File backupDB = new File(download_folder, backupDBPath);
//                    if (currentDB.exists()) {
//                        FileChannel src = new FileInputStream(currentDB).getChannel();
//                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
//                        dst.transferFrom(src, 0, src.size());
//                        src.close();
//                        dst.close();
//                        Toast.makeText(getApplicationContext(), getString(R.string.dbSaved), Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), getString(R.string.dbSaved), Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(getApplicationContext(), getString(R.string.noWritePrivileges), Toast.LENGTH_SHORT).show();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) { // registro conseguido
            if (resultCode == RESULT_OK) {
                EditText editEmail = findViewById(R.id.editEmail);
                EditText editPass = findViewById(R.id.editPassword);
                editEmail.setText(data.getExtras().getString("email"));
                editPass.setText(data.getExtras().getString("password"));
            }
        }
    }
}
