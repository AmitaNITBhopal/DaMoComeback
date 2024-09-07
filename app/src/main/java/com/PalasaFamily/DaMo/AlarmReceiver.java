package com.PalasaFamily.DaMo;

import com.PalasaFamily.DaMo.AlarmActivity;
import com.PalasaFamily.DaMo.AlarmService;
import com.google.android.gms.stats.GCoreWakefulBroadcastReceiver;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import static androidx.legacy.content.WakefulBroadcastReceiver.startWakefulService;
//import android.support.v4.content.WakefulBroadcastReceiver;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        //this will send a notification message
        ComponentName comp = new ComponentName(context.getPackageName(),
                AlarmService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
