package com.example.pomodorotimer.model.SeekBar;

import android.util.Log;
import android.widget.SeekBar;

import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerSharedPreferences;

public class WorkSeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "WorkSeekBar";

    public WorkSeekBar() {
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

        long workTimeMinutes = seekBar.getProgress();
        try {
            HandlerSharedPreferences.getInstance().setWorkTime(workTimeMinutes);

            long workTimeMs = workTimeMinutes * 60 * 1000;

            Log.d(TAG, "Converting: " + workTimeMinutes + " minutes to " + workTimeMs + " ms");

            HandlerCountDownTime.getInstance().restartWithNewWorkTime(workTimeMs);

            Log.d(TAG, "Work time updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating work time", e);
            e.printStackTrace();
        }
    }
}