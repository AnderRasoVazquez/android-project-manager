package com.example.projectmanager.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.projectmanager.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class ServiceFirebase extends FirebaseMessagingService {
    public ServiceFirebase() {
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("TOKEN", "creando token nuevo" + s);
        System.out.println("creando token nuevo " + s);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("firebase_token", s);
        edit.apply();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            showNotification(data.get("user"), data.get("project"));
        }
    }

    /**
     * Notificar que se ha añadido un miembro nuevo al proyecto
     * @param project
     */
    private void showNotification(String user, String project) {
        NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
            elManager.createNotificationChannel(elCanal);
        }
        String invit = getString(R.string.projectInvitation);
        elBuilder.setSmallIcon(android.R.drawable.star_on)
                .setContentTitle(String.format(invit, user, project))
                .setVibrate(new long[]{0, 250, 250, 250})
                .setAutoCancel(true);
        elManager.notify(0, elBuilder.build());
    }

}
