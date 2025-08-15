package com.example.pomodorotimer.model.SeekBar;

import android.util.Log;
import android.widget.SeekBar;

import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerSharedPreferences;

public class LongBreakSeekBar implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = "LongBreakSeekBar";

    public LongBreakSeekBar() {
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

        long longBreakTimeMinutes = seekBar.getProgress();
        try {
            HandlerSharedPreferences.getInstance().setLongBreakTime(longBreakTimeMinutes);

            long longBreakTimeMs = longBreakTimeMinutes * 60 * 1000;

            Log.d(TAG, "Converting: " + longBreakTimeMinutes + " minutes to " + longBreakTimeMs + " ms");

            HandlerCountDownTime.getInstance().restartWithNewLongBreakTime(longBreakTimeMs);

            Log.d(TAG, "Long break time updated successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error updating long break time", e);
            e.printStackTrace();
        }
    }
}