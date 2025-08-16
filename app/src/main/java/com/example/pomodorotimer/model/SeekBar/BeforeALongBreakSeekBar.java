package com.example.pomodorotimer.model.SeekBar;

import android.util.Log;
import android.widget.SeekBar;

import com.example.pomodorotimer.util.HandlerSharedPreferences;


public class BeforeALongBreakSeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "WorksBeforeLongBreakSeekBar";

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: " + (progress + 1));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: " + (seekBar.getProgress() + 1));
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch: " + (seekBar.getProgress() + 1));

        int sessionsBeforeLongBreak = seekBar.getProgress() + 1;
        try {
            HandlerSharedPreferences.getInstance().setSessionsBeforeLongBreak(sessionsBeforeLongBreak);
            Log.d(TAG, "Successfully saved sessions before long break: " + sessionsBeforeLongBreak);
        } catch (Exception e) {
            Log.e(TAG, "Error saving sessions before long break", e);
            e.printStackTrace();
        }
    }
}