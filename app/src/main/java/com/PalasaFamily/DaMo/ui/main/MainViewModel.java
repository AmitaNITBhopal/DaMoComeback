package com.PalasaFamily.DaMo.ui.main;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.RelativeLayout;


import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.PalasaFamily.DaMo.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainViewModel extends AndroidViewModel {

    private int mPosition;
    private String mDefaultQuote;
    //private String[] mLocalQuoteList;
    private String[] mGodownQuoteList;
    private String[] mCurrentQuoteList;
    private int[] mImageArray;
    private static Drawable[] mCustomDrawableArray = new Drawable[7];
    private static Date mDateID = new Date();

    private static final String TAG = "MainViewModel";

    public void SetPosition(int position)
    {
        mPosition = position;
    }

    public void setCurrentQuoteList(String[] currentQuoteList) {
        this.mCurrentQuoteList = currentQuoteList;
    }

    public void setGodownQuoteList(String[] godownQuoteList) {
        this.mGodownQuoteList = godownQuoteList;
    }

    public MainViewModel(Application application){
        super(application);
        mPosition=0;

        mImageArray = new int[7];
        mImageArray[0] = R.drawable.image1;
        mImageArray[1] = R.drawable.image2;
        mImageArray[2] = R.drawable.image3;
        mImageArray[3] = R.drawable.image4;
        mImageArray[4] = R.drawable.image5;
        mImageArray[5] = R.drawable.image6;
        mImageArray[6] = R.drawable.image7;

        CreateCustomImageList();
        mDefaultQuote = "“Life is what happens when you’re busy making other plans.” \nJohn Lennon";
    }

    // This method should return date as per the position
    public String getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-mPosition);
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMM d");
        String date = dateFormat.format(calendar.getTime());
        return date;
    }

    public Drawable GetDrawableBG() {
        Drawable bg;
        SharedPreferences pref = getApplication().getSharedPreferences(getApplication().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        if(pref.contains(getApplication().getString(R.string.customBgState))) {
            boolean customBgState = pref.getBoolean("customBgState", false);
            if (customBgState == true) {
                // set custom Bg
                if(mCustomDrawableArray != null) {
                    int index = GetImageListIndex(mCustomDrawableArray.length);
                    if (index > -1) {
                        bg = mCustomDrawableArray[index];
                        if(bg != null) {
                            return bg;
                        }
                    }
                }
            }
        }

        // take from default imagelist
        int index = GetImageListIndex(mImageArray.length);
        if(index > -1) {
            return ResourcesCompat.getDrawable(getApplication().getResources(), mImageArray[index], null);
        }

        return ResourcesCompat.getDrawable(getApplication().getResources(), mImageArray[0], null);
    }

    // Show image based on day of week
    public int getImageResource() {
        //int index =
        if( (mImageArray.length < 7) || (mPosition > mImageArray.length) ){
            Log.d(TAG, "image array cannot return value for mPosition,mImageArray.length" +
                    String.valueOf(mPosition) + "," + String.valueOf(mImageArray.length));
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int index = (mPosition + 8 - dayOfWeek) % 7;
        if (index < mImageArray.length) {
            return mImageArray[index];
        } else {
            return mImageArray[0];
        }

    }

    // returns -1 if index is not found
    // 0 or a bigger number
    private int GetImageListIndex(int arrayLen) {
        //position  : 0,1,2...
        //dayofweek : 1..7
        int finalIndex = -1;
        if( (arrayLen < 7) || (mPosition > arrayLen) ){
            Log.d(TAG, "image array cannot return value for mPosition,ImageArray.length" +
                    String.valueOf(mPosition) + "," + String.valueOf(arrayLen));
        }

        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int index = (mPosition + 8 - dayOfWeek) % 7;
        if (index < arrayLen) {
            finalIndex = index;
        }

        return finalIndex;
    }

    //  create custom drawable list
    public void CreateCustomImageList()
    {
        boolean bIsListRequired = false;
        ArrayList<Drawable> tempList = new ArrayList<Drawable>();

        // check is create list req?
        SharedPreferences pref = getApplication().getSharedPreferences(getApplication().getString(R.string.SharedPreferenceFile), 0); // 0 - for private mode
        if(pref.contains(getApplication().getString(R.string.customBgState))) {
            boolean customBgState = pref.getBoolean("customBgState", false);
            if (customBgState == true) {
                bIsListRequired = true;
                // set custom Bg
                if (pref.contains("imagePath")) {
                    Gson gson = new Gson();
                    String json = pref.getString("imagePath", null);
                    Type type = new TypeToken<ArrayList<String>>() {}.getType();
                    List<String> imagePathList = gson.fromJson(json, type);

                    // fill mCustomDrawableArray here
                    int i = 0;
                    int listSize = imagePathList.size();
                    for(; i < 7; i++) {
                        if(i < listSize) {
                            String path = imagePathList.get(i);
                            if (path.isEmpty()) {
                                // copy from ImageList
                                tempList.add(ResourcesCompat.getDrawable(getApplication().getResources(), mImageArray[i], null));
                            } else {
                                // copy into drawable array
                                tempList.add(Drawable.createFromPath(path));
                            }
                        } else {
                            tempList.add(ResourcesCompat.getDrawable(getApplication().getResources(), mImageArray[i], null));
                        }
                    }

                    mCustomDrawableArray = tempList.toArray(mCustomDrawableArray);
                } else {
                    Log.d("MainViewModel", "pref does not contain imagepath");
                }
            }
        }
    }


    // Check if update is required
    boolean IsUpdateCurrentListRequired() {
        boolean isRequired = true;
        if(mCurrentQuoteList != null) {
            Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            // The java.util.Date.getTime() method returns how many milliseconds have passed since January 1, 1970, 00:00:00
            //long dayID = date.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String old = sdf.format(mDateID);
            String newdate = sdf.format(date);
            isRequired = ! sdf.format(date).equals(sdf.format(mDateID));
        }

        if(isRequired) {
            // Initiate here
            mCurrentQuoteList = new String[7];
        }
        return isRequired;
    }

    void UpdateDateID() {
        mDateID = Calendar.getInstance().getTime(); //.getTime();
    }

    void UpdateCurrentQuoteList() {
        // check if godown has some data, it should never have data les than 7 items
        if( (mGodownQuoteList != null) && (mGodownQuoteList.length > 7) ) {
             /*Update of list is required
            Copy data from Godown to currentList, such that if dayOfMonth is 1, copy Godown[6]
            G0		cur-6
            G1		cur-5
            G2		cur-4
            G3		cur-3
            G4		cur-2
            G5		cur-1  position=1
            G6		cur    position=0
            Hence copy G6,G5,G4,G3,G2,G1,G0 when dayOfMonth = 1
            and G7,G6,G5,G4,G3,G2,G1 when dayOfMonth = 2*/

            int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            int indexToBeCopied = dayOfMonth + 5;
            for(int i = 0; i < 7; i++) {
                if(indexToBeCopied >= 0) {
                    mCurrentQuoteList[i] = mGodownQuoteList[indexToBeCopied];
                    indexToBeCopied = indexToBeCopied - 1;
                }
                else {
                Log.d(TAG, "index went below 0 when dayOfMonth was " + String.valueOf(dayOfMonth));
                }
            }
        }
        else {
            Log.d(TAG,"mGodownQuoteList is empty or does not have sufficient data");
        }

    }

    public String[] getText() {

        String[] strArray = mDefaultQuote.split("\n");
        if (IsUpdateCurrentListRequired()) {
            UpdateCurrentQuoteList();
            UpdateDateID();
        }

        if(mCurrentQuoteList == null) {
            Log.d(TAG,"mCurrentQuoteList is null");
            //return mDefaultQuote;
        }
        else {
            if (mPosition < mCurrentQuoteList.length) {
                strArray = mCurrentQuoteList[mPosition].split("\n");
            }
        }

        if(strArray.length < 2)
        {
            Log.d(TAG, "strArray length is " + String.valueOf(strArray.length));
            strArray = mDefaultQuote.split("\n");
        }
        return strArray;
    }

}



