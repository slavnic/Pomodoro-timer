package com.example.pomodorotimer.util;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import com.example.pomodorotimer.R;

public class HandlerSound {
    private static final String TAG = "HandlerSound";
    private static HandlerSound instance;
    private Context context;
    private MediaPlayer mediaPlayer;

    private HandlerSound(Context context) {
        this.context = context.getApplicationContext();
    }

    public static HandlerSound getInstance(Context context) {
        if (instance == null) {
            instance = new HandlerSound(context);
        }
        return instance;
    }

    public void playWorkTimeFinishedSound() {
        playSound(R.raw.work_time);
        Log.d(TAG, "Playing work time finished sound");
    }

    public void playShortBreakTimeFinishedSound() {
        playSound(R.raw.short_break_time);
        Log.d(TAG, "Playing short break time finished sound");
    }

    public void playLongBreakTimeFinishedSound() {
        playSound(R.raw.long_break_time);
        Log.d(TAG, "Playing long break time finished sound");
    }

    private void playSound(int soundResourceId) {
        try {
            // Release any existing MediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Create and start new MediaPlayer
            mediaPlayer = MediaPlayer.create(context, soundResourceId);
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                        mediaPlayer = null;
                    }
                });
                mediaPlayer.start();
            } else {
                Log.e(TAG, "Failed to create MediaPlayer for resource: " + soundResourceId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error playing sound: " + soundResourceId, e);
        }
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
