package com.example.pomodorotimer.model.SeekBar;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.example.pomodorotimer.R;
import com.example.pomodorotimer.util.HandlerSharedPreferences;
import com.example.pomodorotimer.util.HandlerTime;

import java.util.HashMap;
import java.util.Map;

public class SeekBarFactory {
    private static final String TAG = "SeekBarFactory";
    @SuppressLint("StaticFieldLeak")
    private static SeekBarFactory instance;
    @SuppressLint("StaticFieldLeak")
    private static View view;
    private final Map<Integer, SeekBar> integerSeekBarMap;

    private SeekBarFactory() throws Exception {
        integerSeekBarMap = new HashMap<>();
        initBarToRealValue(R.id.seekBarWorkDuration);
        initBarToRealValue(R.id.seekBarShortBreak);
        initBarToRealValue(R.id.seekBarLongBreak);
        initBarToRealValue(R.id.seekBarSessions);
        initBarToRealValue(R.id.seekBarDailyGoal);
    }

    public static void setView(View view) {
        SeekBarFactory.view = view;
    }

    public static SeekBarFactory getInstance() throws Exception {
        if (view == null)
            throw new Exception("view == null");

        if (instance == null)
            instance = new SeekBarFactory();
        return instance;
    }

    private void initBarToRealValue(int type) throws Exception {
        Log.d(TAG, "initBarToRealValue: " + type);
        SeekBar seekBar = getSeekBar(type);

        if (type == R.id.seekBarWorkDuration) {
            long fullValue = HandlerSharedPreferences.getInstance().getWorkTime();
            seekBar.setProgress((int) HandlerTime.getInstance().getRealTime(fullValue));
        } else if (type == R.id.seekBarShortBreak) {
            long fullValue = HandlerSharedPreferences.getInstance().getBreakTime();
            seekBar.setProgress((int) HandlerTime.getInstance().getRealTime(fullValue));
        } else if (type == R.id.seekBarLongBreak) {
            long fullValue = HandlerSharedPreferences.getInstance().getLongBreakTime();
            seekBar.setProgress((int) HandlerTime.getInstance().getRealTime(fullValue));
        } else if (type == R.id.seekBarSessions) {
            // Sessions before long break - this is just a number, not time
            int sessions = HandlerSharedPreferences.getInstance().getSessionsBeforeLongBreak();
            seekBar.setProgress(sessions);
        } else if (type == R.id.seekBarDailyGoal) {
            // Daily goal - number of sessions per day
            int dailyGoal = HandlerSharedPreferences.getInstance().getDailyGoal();
            seekBar.setProgress(dailyGoal);
        } else {
            throw new Exception("type is not identified");
        }
    }

    public SeekBar getSeekBar(int type) throws Exception {
        Log.d(TAG, "getSeekBar: " + type);
        if (integerSeekBarMap.containsKey(type)) {
            return integerSeekBarMap.get(type);
        } else {
            SeekBar seekBar = null;
            try {
                seekBar = view.findViewById(type);
            } catch (Exception e) {
                throw new Exception("type is not identified");
            }

            if (type == R.id.seekBarWorkDuration) {
                seekBar.setOnSeekBarChangeListener(new WorkSeekBar());
            } else if (type == R.id.seekBarShortBreak) {
                seekBar.setOnSeekBarChangeListener(new BreakSeekBar());
            } else if (type == R.id.seekBarLongBreak) {
                seekBar.setOnSeekBarChangeListener(new LongBreakSeekBar());
            } else if (type == R.id.seekBarSessions) {
                seekBar.setOnSeekBarChangeListener(new SessionsSeekBar());
            } else if (type == R.id.seekBarDailyGoal) {
                seekBar.setOnSeekBarChangeListener(new GoalSeekBar());
            } else {
                throw new Exception("type is not identified");
            }

            integerSeekBarMap.put(type, seekBar);
            return seekBar;
        }
    }
}