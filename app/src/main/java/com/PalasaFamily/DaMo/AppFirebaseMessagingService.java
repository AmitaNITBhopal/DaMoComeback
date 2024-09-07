package com.PalasaFamily.DaMo;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d("AppFireb..Mess..Service", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        //sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> params = remoteMessage.getData();
        /*JSONObject object = new JSONObject(params);
        Log.e("JSON_OBJECT", object.toString());*/

        Log.d("AppFireb..Mess..Service", "Preparing to send notification...: ");
        AppNotification appNotification;
        appNotification = new AppNotification(params.get("title"), params.get("body")  , this);
        appNotification.Notify();
    }
}
