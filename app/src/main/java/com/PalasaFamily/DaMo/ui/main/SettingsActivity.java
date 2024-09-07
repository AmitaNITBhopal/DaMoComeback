package com.PalasaFamily.DaMo.ui.main;

import com.PalasaFamily.DaMo.ImageProcessor;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.PalasaFamily.DaMo.AddBackground;
import com.PalasaFamily.DaMo.MainActivity;
import com.PalasaFamily.DaMo.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity  {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1234;
    private static int RESULT_LOAD_IMG = 1;
    String imgpath,storedpath;
    //int mPosition = imagePathAL.size(); // use of mPosition here is to keep track of position at which gallery image is added in the arrayList
    int mChipIndex = -1;
    SharedPreferences sp;
    public static ArrayList<String> imagePathAL = new ArrayList<String>();
    private ProgressBar  pgsBar ;
    Thread resizeThread;
    Button applyButton;
    //ImageButton addPic;
    int [] imageViewIds;
    int mthePosition = -1;

    private static class ResizeImageFinshedHandler extends Handler {
        private final WeakReference activity;

        public ResizeImageFinshedHandler(SettingsActivity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SettingsActivity activity = (SettingsActivity) this.activity.get();
            int position = msg.getData().getInt("imagePosition");
            activity.OnImageLoadfromGalleryFinished(position);
        }
    }
    private  ResizeImageFinshedHandler resizeImageFinishedHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        pgsBar = (ProgressBar) findViewById(R.id.pBar);
        //fill imagepathAL from shared pref.
        InitiateImagepathAL();
        final ChipGroup backgroundChipGroup = (ChipGroup) findViewById(R.id.bkgChipGroup);
        //imageViewIds = new int []{R.id.imageView0, R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4, R.id.imageView5, R.id.imageView6};
        int noOfChoosenImages = imagePathAL.size();
        if (noOfChoosenImages > getResources().getInteger(R.integer.screen_count)) {
            noOfChoosenImages = getResources().getInteger(R.integer.screen_count);
        }

        int i = 0;
        do {
            AddChip();
            i++;
        }
        while(i < noOfChoosenImages);
        UpdateBgView();
    }

    void InitiateImagepathAL() {
        SharedPreferences pref = getApplication().getSharedPreferences(getApplication().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        if (pref.contains("imagePath")) {
            Gson gson = new Gson();
            String json = pref.getString("imagePath", "");
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();

            if(json.isEmpty() == false) {
                imagePathAL = gson.fromJson(json, type);
            }
        }
    }

    // add empty chip
    void AddChip() {
        final ChipGroup backgroundChipGroup = (ChipGroup) findViewById(R.id.bkgChipGroup);
        if (backgroundChipGroup.getChildCount() >= getResources().getInteger(R.integer.screen_count)) {
            return;
        }

        final Chip chip = new Chip(this);
        ChipDrawable drawable = ChipDrawable.createFromAttributes(this, null, 0, R.style.CustomChip);
        chip.setChipDrawable(drawable);
        /*chip.setIconStartPadding(5);*/
        chip.setText(" + ");
        chip.setCloseIconVisible(false);
        //Callback fired when chip close icon is clicked
        chip.setOnCloseIconClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove image from image array
                int index = backgroundChipGroup.indexOfChild(chip);
                if(index < imagePathAL.size()) {
                    imagePathAL.remove(index);
                }
                //remove chip
                backgroundChipGroup.removeView(chip);

                // if count has become 6 then add also
                if(imagePathAL.size() == getResources().getInteger(R.integer.screen_count)-1) {
                    AddChip();
                }
            }
        });

        chip.setClickable(true);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddButtonHL1Clicked(v);
            }
        });

        backgroundChipGroup.addView(chip);
    }


    void SetSwitchVisibility(int state) {
        Switch onOffSwitch = (Switch)  findViewById(R.id.switchBkg);
        onOffSwitch.setVisibility(state);

        if(state == View.VISIBLE) {
            // checked?
            SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
            if (pref.contains(getResources().getString(R.string.customBgState))) {
                boolean stateEnabled = pref.getBoolean(getResources().getString(R.string.customBgState), false);
                onOffSwitch.setChecked(stateEnabled);
            } else {
                onOffSwitch.setChecked(false);
            }
        }
    }

    public void onSwitchClicked(View view) {
        Switch button = (Switch) view;
        boolean buttonState = button.isChecked();

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getResources().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();

        if(imagePathAL.isEmpty() || (buttonState==false)) {
            editor.putBoolean(getResources().getString(R.string.customBgState), false);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(imagePathAL);
            editor.putString("imagePath", json);
            editor.putBoolean(getResources().getString(R.string.customBgState), true);
        }


        editor.commit();

    }


    public void onCloseButtonClicked(View view) {
       /*     // task from ResizeImageThread
        sp= this.getSharedPreferences(this.getString(R.string.SharedPreferenceFile), 0);
        SharedPreferences.Editor edit=sp.edit();
        Set<String> imagePathSet = new HashSet<>(ResizeImageThread.imagePathAL);
@ -77,6 +82,8 @@ public class SettingsActivity extends Activity {
   //     edit.putString(mActivity.getString(R.string.imagepath),storedpath);
        edit.commit();
        //task from ResizeImageThread

    */
        Intent intent = new Intent(getApplicationContext(),   MainActivity.class);
        startActivity(intent);
    }

    public void onAddButtonHL1Clicked(View view) {

        if(CheckPermissions() == false)
            return;
        //addPic.setEnabled(false);  Amita_06Aug24 dont know what thia variable is hence commented


        Chip chip = (Chip) view;
        chip.setClickable(false);
        ChipGroup backgroundChipGroup = (ChipGroup) findViewById(R.id.bkgChipGroup);
        int index = backgroundChipGroup.indexOfChild(chip);
        if(index > -1) {
            mChipIndex = index;
            AddBackground addBackground = new AddBackground(this);
            // increase the mPostion to next background
            addBackground.loadImagefromGallery(mChipIndex);

        }
    }

    public void onResizeImageFinished(){
        pgsBar.setVisibility(View.INVISIBLE);
        applyButton.setEnabled(true);
    }

    boolean CheckPermissions() {
        boolean hasPermission = false;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Write permission necessary");
                alertBuilder.setMessage("The app needs permission to add personalized background.");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SettingsActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                //           Toast.makeText(this, "Have to show a explanation", Toast.LENGTH_SHORT).show();
            } else {
                // control reached here coz user has checked "Don't  ask again" and denied the request for permission
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("The app needs storage permission to pick image and set it as background. Please open settings, go to permissions and allow.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {
            hasPermission = true;
        }

        return hasPermission;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();


                String[] filePathColumn = { MediaStore.MediaColumns.DATA };
                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgpath = cursor.getString(columnIndex);
                Log.d("path", imgpath);
                cursor.close();

                BitmapFactory options = new BitmapFactory();
                final Bitmap myBitmap = BitmapFactory.decodeFile(imgpath);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                final int screenHeight = (int) (displayMetrics.heightPixels * .5);        // .5 is multiplied to reduce the size of the image

                //  inline runnable is defined coz we need handler to handle the Progressbar and applyButton
                final SettingsActivity settingsActivity = this;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap resizedBitmap = ImageProcessor.resizeBitmap(myBitmap, screenHeight);
                        ImageProcessor.storeImage(resizedBitmap, settingsActivity);
                        String storedpath = ImageProcessor.mResizedBitmapPath;
                        //adding this to store the storedPath in ArrayList, mChipIndex ensures that the ArrayList size doesnt grow more than 7
                        imagePathAL.add(mChipIndex,storedpath);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putInt("imagePosition", mChipIndex);
                        data.putString("storedPath",storedpath);
                        msg.setData(data);
                        resizeImageFinishedHandler.sendMessage(msg);
                    }
                }).start();
                resizeImageFinishedHandler = new ResizeImageFinshedHandler(this);
                pgsBar.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(this, "Please pick an image", Toast.LENGTH_LONG).show();
                UpdateBgView();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
            e.printStackTrace();
            UpdateBgView();
        } catch (OutOfMemoryError E) {
            Toast.makeText(this, "Image is too big to upload, please resize and try again", Toast.LENGTH_LONG)
                    .show();
            UpdateBgView();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the needful

                    Toast.makeText(this, "Thanks !", Toast.LENGTH_SHORT);
                    /*AddBackground addBackground = new AddBackground(this);
                    addBackground.loadImagefromGallery(mPosition++);*/

                } else {
                    Toast.makeText(this, "Please provide write permissions to enable personalized background", Toast.LENGTH_LONG)
                            .show();
                }

                UpdateBgView();
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    public void OnImageLoadfromGalleryFinished(int position) {
        pgsBar.setVisibility(View.GONE);
        ChipGroup backgroundChipGroup = (ChipGroup) findViewById(R.id.bkgChipGroup);
        if(position > -1) {
            // set image on the existing chip
            int count = backgroundChipGroup.getChildCount();
            if(count > position) {
                Chip chip = (Chip) backgroundChipGroup.getChildAt(position);
                if(chip != null) {
                    String imagePath = imagePathAL.get(position);
                    if(imagePath.isEmpty() == false) {
                        chip.setChipIcon(Drawable.createFromPath(imagePath));
                        chip.setText("");
                        chip.setCloseIconVisible(true);
                        if (count < getResources().getInteger(R.integer.screen_count)) {
                            // add empty chip
                            AddChip();
                        }
                    }
                    chip.setClickable(true);
                    SetSwitchVisibility(View.VISIBLE);
                }
            }
            UpdateBgView();
        }
    }

    void UpdateBgView() {
        ChipGroup backgroundChipGroup = (ChipGroup) findViewById(R.id.bkgChipGroup);
        int chipCount = backgroundChipGroup.getChildCount();

        int noOfChoosenImages = imagePathAL.size();
        if (noOfChoosenImages > getResources().getInteger(R.integer.screen_count)) {
            noOfChoosenImages = getResources().getInteger(R.integer.screen_count);
        }

        if(chipCount < noOfChoosenImages) {
            //Something wrong
            return;
        }

        for (int i = 0; i < chipCount; i++) {
            final Chip chip = (Chip) backgroundChipGroup.getChildAt(i);
            if ((chip != null) && (i < noOfChoosenImages)) {
                chip.setChipIcon(Drawable.createFromPath(imagePathAL.get(i)));
                chip.setText("");
                chip.setCloseIconVisible(true);
            }
        }

        if(noOfChoosenImages == 0) {
            SetSwitchVisibility(View.GONE);
        } else {
            SetSwitchVisibility(View.VISIBLE);
        }

        if(mChipIndex > -1) {
            Chip currentChip = (Chip) backgroundChipGroup.getChildAt(mChipIndex);
            currentChip.setClickable(true);
        }
    }

}
