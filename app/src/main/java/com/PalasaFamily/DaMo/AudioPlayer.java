package com.PalasaFamily.DaMo;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioPlayer {
    private static AudioPlayer inst;
    private MediaPlayer mPlayer;

    private AudioPlayer() {
        mPlayer = new MediaPlayer();
    }

    public static AudioPlayer instance() {
        if(inst == null) {
            inst = new AudioPlayer();
        }

        return inst;
    }

    public void Initialize(Context context) {
        try {
            mPlayer = MediaPlayer.create(context, R.raw.joy);
        }
        catch(Exception ex) {
            Log.d("AudioPlayer", "Exception : " + ex.getLocalizedMessage());
        }
        mPlayer.setLooping(true);
    }

    public void Play()
    {
        try {
        mPlayer.start();
        } catch (IllegalStateException ex) {
        Log.d("AudioPlayer", "Exception : " + ex.getLocalizedMessage());
        }
    }

    public void Stop()
    {
        try {
            mPlayer.stop();
        } catch (IllegalStateException ex) {
            Log.d("AudioPlayer", "Exception : " + ex.getLocalizedMessage());
        }
    }

    public boolean IsPlaying()
    {
        boolean bState = false;
        try {
            bState = mPlayer.isPlaying();
        } catch (IllegalStateException ex) {
            Log.d("AudioPlayer", "Exception : " + ex.getLocalizedMessage());
        }

        return bState;
    }
}
