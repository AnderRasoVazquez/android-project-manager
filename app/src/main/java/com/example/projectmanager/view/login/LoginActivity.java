package com.example.projectmanager.view.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.DB;
import com.example.projectmanager.view.projects.ProjectsActivity;
import com.example.projectmanager.view.register.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

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
    }
}
