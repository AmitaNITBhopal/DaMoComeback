package com.PalasaFamily.DaMo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class AlarmService extends IntentService {
    //private NotificationManager alarmNotificationManager;

    public AlarmService() {
        super("AlarmService");
    }

    @Override
    public void onHandleIntent(Intent intent) {
        //start alarm sound
        try {
            // Initiate audio play
            AudioPlayer.instance().Initialize(this);
            AudioPlayer.instance().Play();
        }
        catch (IllegalStateException ex) {
            Log.d("AlarmService", "Exception : " + ex.getLocalizedMessage());
        }

        AppNotification appNotification;
        appNotification = new AppNotification(getString(R.string.alarmtitle), getString(R.string.alarmmesage), this);
        appNotification.Notify();
        Log.d("AlarmService", "Notification sent.");
    }

}


