package com.example.projectmanager.view.register;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.projectmanager.R;
import com.example.projectmanager.view.projects.ProjectsActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Actividad para registrarse.
 */
public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editEmail = findViewById(R.id.editEmail);
                EditText editUsername = findViewById(R.id.editUsername);
                EditText editPass = findViewById(R.id.editPassword);
                EditText editPassRepeat = findViewById(R.id.editRepeatPassword);

                String email = editEmail.getText().toString();
                String username = editUsername.getText().toString();
                String pass = editPass.getText().toString();
                String passRepeat = editPassRepeat.getText().toString();

                if (!pass.equals(passRepeat)) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.passNotEqual, Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }

                JSONObject registerJson = new JSONObject();
                try {
                    registerJson.put("email", email);
                    registerJson.put("name", username);
                    registerJson.put("pass", email);

                    boolean success = DB.getInstance(getApplicationContext()).register(registerJson.toString());

                    if (!success) {
                        Toast toast = Toast.makeText(getApplicationContext(), R.string.userExists, Toast.LENGTH_SHORT);
                        toast.show();
                    } else {
                        Intent intent = new Intent(RegisterActivity.this, ProjectsActivity.class);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
