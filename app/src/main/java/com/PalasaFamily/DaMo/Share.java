package com.PalasaFamily.DaMo;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Share {
    int mType;  // 0 : shareApp, 1 : share screenshot
    String mCurrentScreenShotPath;
    Intent mSharingIntent;
    Activity mActivity;

    public Share(int type, Activity actRef) {
        mType = type;
        mSharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        mActivity = actRef;
    }

    // the method creates bitmap screenshot
    private Bitmap GetScreenShot() {
        View view = mActivity.getWindow().getDecorView().findViewById(android.R.id.content);
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    // to add bitmap to app folder
    private void StoreImage() {
        Bitmap bm = GetScreenShot();
        // commenting next line to change name to one file coz in temp folder many files are getting created
        //     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //create Image file name
        String imageFileName = "Damo_ScreenShot";

        try {
            File file = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".png",         /* suffix */
                    mActivity.getApplication().getFilesDir()       /* directory */
            );

            mCurrentScreenShotPath = file.getAbsolutePath();
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetSharingIntent() {
        mSharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sharing via DaMo Daily Motivation App");

        switch (mType) {

            case 0:  //share App
                mSharingIntent.setType("text/plain");
                mSharingIntent.putExtra(android.content.Intent.EXTRA_TEXT
                        , mActivity.getResources().getString(R.string.shareapp_message));
                return;

            case 1:
                mSharingIntent.setType("image/*");
                mSharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                mSharingIntent.putExtra(android.content.Intent.EXTRA_TEXT
                        , mActivity.getResources().getString(R.string.share_screenshot_message));

                File file = new File(mCurrentScreenShotPath);
                //              File file = new File(Environment.getExternalStorageDirectory()+"/DaMo"+"/Screenshot.png");
                if (file.exists()) {

                    Uri uri = FileProvider.getUriForFile(mActivity.getApplicationContext(),
                            BuildConfig.APPLICATION_ID + ".provider",
                            file);

                    mSharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    mSharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                return;

            default :
                Log.d("Share", "Default in SetSharingIntent");
        }
    }

    private void StartActivityWithIntent() {

        switch (mType) {

            case 0:  //share App
                try {
                    mActivity.startActivity(Intent.createChooser(mSharingIntent, "Share App via"));
                } catch (ActivityNotFoundException e) {
                    Log.d("Share", "No App Available");
                }
                return;

            case 1:
                try {
                    mActivity.startActivity(Intent.createChooser(mSharingIntent, "Share Screenshot Via"));
                } catch (ActivityNotFoundException e) {
                    Log.d("Share", "No Screenshot Available");
                }
                return;

            default :
                Log.d("Share", "Default in StartActivityWithIntent");
        }
    }

    /*public void deleteScreenShotShared(Context context){
        File file = new File(mCurrentScreenShotPath);
        if (file.exists()) {
            boolean deleted = file.delete();
            if(!deleted){
                boolean deleted2 = false;
                try {
                    deleted2 = file.getCanonicalFile().delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!deleted2){
                    boolean deleted3 = context.deleteFile(file.getName());
                }
            }
        }
    }*/


    public boolean ShareData() {

        switch (mType) {

            case 0: //share App
                SetSharingIntent();
                StartActivityWithIntent();
                return true;
            case 1:
                StoreImage();
                SetSharingIntent();
                StartActivityWithIntent();
                return true;
            default:
                return false;
        }

    }
}



