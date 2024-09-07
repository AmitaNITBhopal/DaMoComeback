package com.PalasaFamily.DaMo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
//import android.widget.ToggleButton;

import java.io.IOException;
import java.util.Calendar;

public class AlarmActivity extends Activity {

    AlarmManager alarmManager;
    private PendingIntent mPendingIntent;
    private TimePicker alarmTimePicker;
    private static AlarmActivity inst;
    private TextView alarmTextView;

    public static AlarmActivity instance() {
        return inst;
    }

    @Override
    public void onStart() {
        super.onStart();
        inst = this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_activity);
        alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        SetAlarmButton();
    }

    public void onAlarmButtonClicked(View view) {
        Button alarmbutton = (Button) view;
        // text setalarmoff implies alarm is currently on
        boolean bCurrentAlarmState = alarmbutton.getText().equals(getResources().getString(R.string.setalarmoff));

        if(bCurrentAlarmState == false) {
            Log.d("MyActivity", "Alarm is currently Off");
            startAlarm();
        } else {
            if(mPendingIntent != null) {
                alarmManager.cancel(mPendingIntent);
                mPendingIntent.cancel();
            }

            Log.d("MyActivity", "Alarm is currently On");
            AudioPlayer.instance().Stop();
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getResources().getString(R.string.amarmstate), !bCurrentAlarmState);
        editor.commit();

        SetAlarmButton();
    }

    private void startAlarm() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
        calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());
        Intent myIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        mPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, myIntent, PendingIntent.FLAG_IMMUTABLE);
        long timeInMillis = calendar.getTimeInMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, mPendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, mPendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, mPendingIntent);
        }

        /*// Initiate audio play
        AudioPlayer.instance().Initialize(this);*/

        Toast.makeText(getApplicationContext(), "Alarm set to :" + String.valueOf(timeInMillis), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    void SetAlarmButton() {
        Button alarmsetbutton = (Button) findViewById(R.id.alarmsetbutton);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        if(pref.contains(getResources().getString(R.string.amarmstate))) {
            if(pref.getBoolean("AlarmSate",false) == true) {
                alarmsetbutton.setText(getResources().getString(R.string.setalarmoff));
            }
            else {
                alarmsetbutton.setText(getResources().getString(R.string.setalarmon));
            }
        }
    }
}

