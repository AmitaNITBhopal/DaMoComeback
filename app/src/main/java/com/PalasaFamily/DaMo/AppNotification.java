package com.PalasaFamily.DaMo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

//import Notification;

public class AppNotification
{
    private final NotificationManager mNotificationManager;
    Notification mNotification;
    Service mServiceContext;


    public AppNotification(String title, String message, Service serviceContext) {

        Log.d("AppNotification", "Preparing to send notification...: " + message);
        mServiceContext = serviceContext;
        mNotificationManager = (NotificationManager) mServiceContext
                .getSystemService(Context.NOTIFICATION_SERVICE);

        // Sets an ID for the notification, so it can be updated.
        int notifyID = 1;
        long pattern[] = {0, 1000, 500, 1000};
        String CHANNEL_ID = "alarm_channel";// The id of the channel.
        CharSequence name = "DaMo Channel";// The user-visible name of the channel.
        int importance = NotificationManager.IMPORTANCE_HIGH;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.setDescription("Description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(pattern);
            notificationChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        // to display notification in DND Mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = mNotificationManager.getNotificationChannel(CHANNEL_ID);
            channel.canBypassDnd();
        }

        PendingIntent contentIntent = PendingIntent.getActivity(mServiceContext, 0,
                new Intent(mServiceContext, MainActivity.class), PendingIntent.FLAG_IMMUTABLE);

        // Create a notification and set the notification channel.
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mServiceContext, CHANNEL_ID);
        Notification notification = builder
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(getNotificationIcon(builder))
                .setChannelId(CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(Notification.DEFAULT_ALL)
                .setColor(ContextCompat.getColor(mServiceContext, R.color.colorPrimaryDark))
                .setAutoCancel(true)
                .setColorized(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build();

    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x008000;
            notificationBuilder.setColor(color);
            return R.drawable.ic_stat_name;

        }
        return R.mipmap.ic_launcher;
    }

    public void Notify() {
        Log.d("AppNotification", "To initiate Notification.");
        mNotificationManager.notify(1, mNotification );
        Log.d("AppNotification", "Notification sent.");
    }
}

