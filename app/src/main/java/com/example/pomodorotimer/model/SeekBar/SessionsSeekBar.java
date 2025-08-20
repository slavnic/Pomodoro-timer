package com.example.pomodorotimer.model.SeekBar;

import android.widget.SeekBar;
import com.example.pomodorotimer.util.HandlerSharedPreferences;

public class SessionsSeekBar implements SeekBar.OnSeekBarChangeListener {

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        try {
            HandlerSharedPreferences.getInstance().setSessionsBeforeLongBreak(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }
}