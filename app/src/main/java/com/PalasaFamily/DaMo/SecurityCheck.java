package com.PalasaFamily.DaMo;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;

//import io.fabric.sdk.android.BuildConfig;

public class SecurityCheck {

    private static final String KEY_SIGNATURE = "firebase_signature";
    private static  final String TAG = "SecurityCheck";
    private static final String PLAY_STORE_APP_ID = "com.android.vending";
    private Context mContext;


public SecurityCheck(Context context) {
    mContext = context;
}

    public boolean IsPass() {
        boolean result = false;
        // TODO enable while posting on playstore
        // if release build
        if (BuildConfig.BUILD_TYPE.equals("debug") == false)
        {
            result = checkAppSignature() && verifyInstaller();
        }
        return result;
    }

    private String GetFirebaseSignature() {
        final String[] firebase_signature = {""};
        /*final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        remoteConfig.fetch(0)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            remoteConfig.activateFetched();
                        }
                        else {
                            Log.d(TAG, "remote config is not fetched.");
                        }

                        firebase_signature[0] = remoteConfig.getString(KEY_SIGNATURE);

                    }
                });*/

        firebase_signature[0] = "TbuFfSSxWVTS5uC95Du6gl3fvIw=\n";
        return firebase_signature[0];
    }

    //Verifying app's signing certificate at runtime
    private boolean checkAppSignature() {
        boolean result = false;
        try {
            PackageInfo packageInfo = mContext.getPackageManager()
                    .getPackageInfo(mContext.getPackageName(), PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                byte[] signatureBytes = signature.toByteArray();

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                final String currentSignature = Base64.encodeToString(md.digest(), Base64.DEFAULT);

                //compare signatures
                if (GetFirebaseSignature().equals(currentSignature)){
                    result = true;
                };
            }

        } catch (Exception e) {
            Log.d(TAG,"Signature check was not possible due to exception " + e.getLocalizedMessage());
        }

        return result;
    }



    private boolean verifyInstaller() {

    boolean bResult = false;
        final String installer = mContext.getPackageManager()
                .getInstallerPackageName(mContext.getPackageName());

        bResult = installer != null
                && installer.startsWith(PLAY_STORE_APP_ID);

        Log.d("SecurityCheck", "verify installer failed");
        return bResult;
    }
}
