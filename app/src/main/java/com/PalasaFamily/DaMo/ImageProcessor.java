package com.PalasaFamily.DaMo;


import android.app.Activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
/* this class is added to resize the image from gallery of user to be used as background of the app
also contains method to store the resized image in the apps memory to be used in the next run
 */
public class ImageProcessor{

    public static String mResizedBitmapPath;


    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);
                //below code does compression of the image
        /*        ByteArrayOutputStream out = new ByteArrayOutputStream();
                source.compress(Bitmap.CompressFormat.PNG, 100, out);
                Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

         */

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                }
                return result;
            } else {
                int targetWidth = maxLength;

                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                }
                return result;

            }
        } catch (Exception e) {
            return source;
        }
    }

    public static void storeImage(Bitmap bm, Activity activity) {

        // commenting next line to change name to one file coz in temp folder many files are getting created
        //     String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        //create Image file name
        String imageFileName = "customBackground";

            try {
                File file = File.createTempFile(
                        imageFileName,  /* prefix */
                        ".png",         /* suffix */
                        activity.getApplication().getFilesDir()       /* directory */
                );

                mResizedBitmapPath = file.getAbsolutePath();
                FileOutputStream fOut = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                fOut.flush();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


}
