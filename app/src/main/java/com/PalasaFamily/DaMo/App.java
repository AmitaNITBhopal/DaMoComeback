package com.PalasaFamily.DaMo;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;

public class App extends Application
        implements LifecycleObserver {

    private ForceUpgradeManager forceUpgradeManager;

    private static App application;

    @Override public void onCreate() {
        super.onCreate();
        application = this;
        initForceUpgradeManager();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
        SecurityCheck check = new SecurityCheck(this);
        if(check.IsPass()) {
            //TODO add firebase notification
        }
        else
        {
            Log.d("App", "Cannot start");
        }
    }


    public void initForceUpgradeManager() {
        if (forceUpgradeManager == null) {
            forceUpgradeManager = new ForceUpgradeManager(application);
        }
    }

}
