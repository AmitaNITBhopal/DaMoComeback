package com.PalasaFamily.DaMo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;




public class AddBackground {

    private static int RESULT_LOAD_IMG = 1;

    Activity refActivity;


    public AddBackground(Activity activity){
        refActivity = activity;

    }

   /* public void setBackgroundImage(){

        relativeLayout = (RelativeLayout) mainActivity.findViewById(R.id.relLayout);
        sp=mainActivity.getSharedPreferences("setback", mainActivity.MODE_PRIVATE);
        if(sp.contains("imagepath")) {
            storedpath = sp.getString("imagepath", "");
            Drawable bg = Drawable.createFromPath(storedpath);
            relativeLayout.setBackground(bg);
        }
    }

    */

    public void loadImagefromGallery(int ImagePosition) {
        // Create intent to Open Image applications like Gallery, Google Photos
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        /*Bundle b = new Bundle();
        b.putInt("ImagePositionKey", ImagePosition);
        galleryIntent.putExtras(b);*/

        //galleryIntent.putExtra("ImagePositionKey", ImagePosition);
        // Start the Intent
        refActivity.startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

    }


}
