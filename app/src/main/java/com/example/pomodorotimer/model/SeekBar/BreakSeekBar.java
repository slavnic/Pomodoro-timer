package com.example.pomodorotimer.model.SeekBar;

import android.util.Log;
import android.widget.SeekBar;

import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerSharedPreferences;


public class BreakSeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "BreakSeekBar";

    public BreakSeekBar() {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "onProgressChanged: " + progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStartTrackingTouch: " + seekBar.getProgress());
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG, "onStopTrackingTouch: " + seekBar.getProgress());

        long breakTimeMinutes = seekBar.getProgress();
        try {
            HandlerSharedPreferences.getInstance().setBreakTime(breakTimeMinutes);

            long breakTimeMs = breakTimeMinutes * 60 * 1000;

            Log.d(TAG, "Converting: " + breakTimeMinutes + " minutes to " + breakTimeMs + " ms");

            HandlerCountDownTime.getInstance().restartWithNewBreakTime(breakTimeMs);

            Log.d(TAG, "Break time updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating break time", e);
            e.printStackTrace();
        }
    }
}