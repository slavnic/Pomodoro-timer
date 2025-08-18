package com.example.pomodorotimer.model.SeekBar;

import android.util.Log;
import android.widget.SeekBar;

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

        int min = 5;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            min = seekBar.getMin();
        }
        long longBreakTimeMinutes = seekBar.getProgress() + min;
        try {
            HandlerSharedPreferences.getInstance().setLongBreakTime(longBreakTimeMinutes * 60 * 1000L);
            Log.d(TAG, "Long break time updated in preferences: " + longBreakTimeMinutes + " min");
        } catch (Exception e) {
            Log.e(TAG, "Error updating long break time", e);
            e.printStackTrace();
        }
    }
}