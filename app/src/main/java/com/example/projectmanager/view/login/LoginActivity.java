package com.example.projectmanager.view.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
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
import com.example.projectmanager.controller.DB;
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
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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

                conexionBDWebService c = new conexionBDWebService();
                c.execute();

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

    private class conexionBDWebService extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... args) {
//            String server = "http://192.168.1.128:5000";
            String server = "https://proyecto-das.herokuapp.com";
            String apiLogin = "/api/v1/login";
            String direccion = server + apiLogin;
            String authString = "admin:admin";
            byte[] authStringEnc = Base64.encode(authString.getBytes(), Base64.DEFAULT);
            String basicAuth = "Basic " + new String(authStringEnc);

            HttpURLConnection urlConnection = null;
            URL destino = null;
            try {
                destino = new URL(direccion);
                urlConnection = (HttpURLConnection) destino.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty ("Authorization", basicAuth);
                int statusCode = urlConnection.getResponseCode();
                System.out.println("el status code" + statusCode);
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                String response = convertInputStreamToString(inputStream);
                JSONObject json = new JSONObject(response);
                System.out.println(json.toString(4));
//                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
//                out.print(parametrosJSON.toString());
//                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.println("On post execute: " + s);
            super.onPostExecute(s);
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
