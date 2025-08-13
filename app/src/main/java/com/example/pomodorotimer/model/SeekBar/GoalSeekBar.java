package com.example.pomodorotimer.model.SeekBar;

import android.util.Log;
import android.widget.SeekBar;


public class GoalSeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "DailyGoalSeekBar";

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
    }
}