package com.example.projectmanager.view.projects;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectmanager.R;
import com.example.projectmanager.controller.Facade;
import com.example.projectmanager.utils.DBFields;
import com.example.projectmanager.utils.HttpRequest;
import com.example.projectmanager.utils.OnConnectionFailure;
import com.example.projectmanager.utils.OnConnectionSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Actividad para editar proyectos.
 */
public class EditProjectActivity extends AppCompatActivity {

    private final int CODE_CAPTURE = 0;
    String projectId;
    ImageView imgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_project);

        this.projectId = getIntent().getExtras().getString(DBFields.TABLE_PROJECTS_ID);

        if (savedInstanceState == null) {
            setData();
        }

        imgView = findViewById(R.id.imageViewProject);
        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        // Guardar proyecto.
        Button button = findViewById(R.id.saveProjectButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JSONObject json = new JSONObject();
                String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
                String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();

                if (name.isEmpty()){
                    return;
                }

                try {
                    json.put(DBFields.TABLE_PROJECTS_NAME, name);
                    if (!desc.isEmpty()){
                        json.put(DBFields.TABLE_PROJECTS_DESC, desc);
                    }

                    // TODO poner un campo mas con la imagen
                    try {
                        String imgString = getStringImage( ( (BitmapDrawable) imgView.getDrawable( ) ).getBitmap( ) );
                        System.out.println("Tamaño imagen:");
                        System.out.println(imgString.length());
                        System.out.println(imgString);
                        json.put(DBFields.TABLE_PROJECTS_IMG, imgString);
                    } catch (Exception e) {

                    }

                    editProject(projectId, json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void takePicture(){
        try {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            } else {
                sendIntentTakePicture();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendIntentTakePicture() {
        Intent elIntentFoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (elIntentFoto.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(elIntentFoto, CODE_CAPTURE);
        }
    }

    /**
     * Edita el proyecto
     * @param projectId
     * @param json
     */
    private void editProject(String projectId, JSONObject json) {
        HttpRequest.Builder builder = Facade.getInstance().modProject(projectId, json);

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
     * Introducir datos en los campos.
     */
    private void setData() {
        System.out.println("MANDANDO PETICION DE RECIBIR PROYECTO: " + projectId);
        HttpRequest.Builder builder = Facade.getInstance().getProject(projectId);

        builder.run(new OnConnectionSuccess() {
            @Override
            public void onSuccess(int statusCode, JSONObject json) {
                try {
                    JSONObject projson = json.getJSONObject("project");
                    String name = projson.getString(DBFields.TABLE_PROJECTS_NAME);
                    String desc = projson.getString(DBFields.TABLE_PROJECTS_DESC);
                    String img = projson.getString(DBFields.TABLE_PROJECTS_IMG);
                    if ((img != null) && (!img.isEmpty())) {
                        // TODO poner la imagen
                        System.out.println("hay una imagen que poner ######################3");
                        byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        imgView.setImageBitmap(decodedByte);
                    }

                    desc = desc.equals("null") ? "" : desc;

                    ((TextView) findViewById(R.id.txtTaskName)).setText(name);
                    ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);
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



//        String response = DB.getInstance(getApplicationContext()).getProject(projectId);
//        System.out.println(response);
//        try {
//            JSONObject json = new JSONObject(response);
//            String name = json.getString(DBFields.TABLE_PROJECTS_NAME);
//            String desc = json.getString(DBFields.TABLE_PROJECTS_DESC);
//
//            desc = desc.equals("null") ? "" : desc;
//
//            ((TextView) findViewById(R.id.txtTaskName)).setText(name);
//            ((TextView) findViewById(R.id.txtTaskDesc)).setText(desc);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_CAPTURE && resultCode == RESULT_OK) {
            System.out.println("HE SACADO LA FOTO #########################################");

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgView.setImageBitmap(imageBitmap);


            // Guardar foto
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String nombrefichero = "IMG_" + timeStamp + "_";

            MediaStore.Images.Media.insertImage(getContentResolver(), imageBitmap, nombrefichero, "");
        }
    }

    // TODO enviar como string codificado
    private String getStringImage(Bitmap bm){
        ByteArrayOutputStream ba=new ByteArrayOutputStream(  );
        bm.compress( Bitmap.CompressFormat.PNG,90,ba );
        byte[] by=ba.toByteArray();
        String encod= Base64.encodeToString( by,Base64.DEFAULT );
        return encod;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        String name = ((TextView) findViewById(R.id.txtTaskName)).getText().toString();
        String desc = ((TextView) findViewById(R.id.txtTaskDesc)).getText().toString();

        outState.putString("name", name);
        outState.putString("desc", desc);
        try {
            String imgString = getStringImage( ( (BitmapDrawable) imgView.getDrawable( ) ).getBitmap( ) );
            outState.putString("img", imgString);
        } catch (Exception e) { }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ((TextView) findViewById(R.id.txtTaskName)).setText(savedInstanceState.getString("name"));
        ((TextView) findViewById(R.id.txtTaskDesc)).setText(savedInstanceState.getString("desc"));

        String img = savedInstanceState.getString("img");

        if (img != null) {
            byte[] decodedString = Base64.decode(img, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            imgView.setImageBitmap(decodedByte);
        }
    }

}
