package com.example.projectmanager.view.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.view.projects.ProjectsActivity;
import com.example.projectmanager.view.register.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

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
                startActivity(intent);
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

                boolean success = DB.getInstance(getApplicationContext()).login(loginString);

                if (!success) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.cantLogin, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, ProjectsActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
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

    // TODO mirar que funciona con Android 5
    private void exportDatabase() {
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                File download_folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                if (download_folder.canWrite()) {
                    String backupDBPath = "project.db";
                    File currentDB = getDatabasePath(DB.getInstance(getApplicationContext()).getDatabaseName());
                    File backupDB = new File(download_folder, backupDBPath);
                    if (currentDB.exists()) {
                        FileChannel src = new FileInputStream(currentDB).getChannel();
                        FileChannel dst = new FileOutputStream(backupDB).getChannel();
                        dst.transferFrom(src, 0, src.size());
                        src.close();
                        dst.close();
                        Toast.makeText(getApplicationContext(), getString(R.string.dbSaved), Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("Path doesn't exist");
                        Toast.makeText(getApplicationContext(), getString(R.string.dbSaved), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.noWritePrivileges), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
