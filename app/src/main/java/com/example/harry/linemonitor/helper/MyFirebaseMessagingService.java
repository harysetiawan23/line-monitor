package com.example.harry.linemonitor.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.harry.linemonitor.R;
import com.example.harry.linemonitor.view.activity.LandingActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM_Service";
    private NotificationManager notificationManager;
    public static  int NOTIFICATION_ID = 1;

    MyFirebaseMessagingService() {

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Setting up Notification channels for android O and above
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            setupChannels();
        }
        int notificationId = new Random().nextInt(60000);
        Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.notif);

        Intent intent = new Intent(this, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "ALL")
                .setSmallIcon(R.mipmap.ic_launcher)  //a resource for your custom small icon
                .setContentTitle("Line Monitor Notif Test") //the "title" value you sent in your notification
                .setVisibility(Notification.VISIBILITY_PUBLIC)
//                .setContentIntent(pendingIntent)
                .setContentText(remoteMessage.getNotification().getBody()) //ditto
//                .setSound(sound)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build());

        Toast.makeText(this,remoteMessage.getNotification().getBody(),Toast.LENGTH_SHORT).show();
//        generateNotification(remoteMessage.getNotification().getBody());


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels() {
        CharSequence adminChannelName = "ADMIN";
        String adminChannelDescription = "Channe; Description";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel("ALL", adminChannelName, NotificationManager.IMPORTANCE_LOW);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }


    private void generateNotification(String body) {
        Intent intent = new Intent(this, LandingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_edit)
                .setContentText("Firebase Notification")
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (NOTIFICATION_ID > 1073741824){
            NOTIFICATION_ID = 0;
        }

        notificationManager.notify(NOTIFICATION_ID++, mNotificationBuilder.build());

    }
}