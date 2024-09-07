package com.PalasaFamily.DaMo;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.PalasaFamily.DaMo.ui.main.MainViewModel;
import com.PalasaFamily.DaMo.ui.main.SettingsActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private ViewPager mPager;

    private MainViewModel mViewModel;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.mViewPager);
        mPagerAdapter = new ScreenSlidePagerAdaptor(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        FloatingActionButton stopAlarmFAB = (FloatingActionButton) findViewById(R.id.stopAlarmFAB);

        if (AudioPlayer.instance().IsPlaying()) {
            stopAlarmFAB.show();
        } else {
            stopAlarmFAB.hide();
        }
        InitializeViewModel();


        getSupportActionBar().setTitle("DaMo - Daily Motivation");

        /*Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        //myToolbar.setTitle(getString(R.string.app_name) );
        setSupportActionBar(myToolbar);*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
/*
        if (menu instanceof MenuBuilder) {
            MenuBuilder menuBuilder = (MenuBuilder) menu;
            menuBuilder.setOptionalIconsVisible(true);
        }
*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        int itemId = item.getItemId();
        if (itemId == R.id.action_ShareApp) {
            Share appShare = new Share(0, this);
            return appShare.ShareData();

           /* case R.id.action_Alarm:
                Intent alarmActivityIntent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(alarmActivityIntent);
                //Toast.makeText(getApplicationContext(), "setting Clicked", Toast.LENGTH_LONG).show();
                return true;*/
        } else if (itemId == R.id.action_shareScreenShot) {
            Share screenShare = new Share(1, this);
            return screenShare.ShareData();

           /* case R.id.action_settings:
                Intent bgIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(bgIntent);
                return true;*/
        } else {
            boolean b = super.onOptionsItemSelected(item);
        }

        return true;
    }

    void InitializeViewModel() {
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.setGodownQuoteList(getResources().getStringArray(R.array.godown_quotes));
    }

    public void onstopAlarmFABClicked(View view) {
        // stop alarm
        AudioPlayer.instance().Stop();
        Log.d("MainActivity", "setting Alarm Off");

        // change shared preference so that alarm button text changes
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(getResources().getString(R.string.amarmstate), false);
        editor.commit();

        // Hide button
        FloatingActionButton stopAlarmFAB = (FloatingActionButton) findViewById(R.id.stopAlarmFAB);
        stopAlarmFAB.hide();
    }

    public ViewPager GetViewPagerObj() {
        return mPager;
    }
}

