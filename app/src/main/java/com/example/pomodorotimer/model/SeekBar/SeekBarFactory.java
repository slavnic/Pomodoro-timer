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
        initBarToRealValue(R.id.work_time);
        initBarToRealValue(R.id.break_time);
        initBarToRealValue(R.id.long_break_time);
        initBarToRealValue(R.id.works_before_a_long_break);
        initBarToRealValue(R.id.goal);
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
        long fullValue;

        if (type == R.id.work_time) {
            fullValue = HandlerSharedPreferences.getInstance().getWorkTime();
        } else if (type == R.id.break_time) {
            fullValue = HandlerSharedPreferences.getInstance().getBreakTime();
        } else if (type == R.id.long_break_time) {
            fullValue = HandlerSharedPreferences.getInstance().getLongBreakTime();
        } else if (type == R.id.works_before_a_long_break) {
            fullValue = HandlerSharedPreferences.getInstance().getWorksBeforeLongBreakTime();
        } else if (type == R.id.goal) {
            fullValue = HandlerSharedPreferences.getInstance().getDailyGoal();
        } else {
            throw new Exception("type is not identified");
        }

        seekBar.setProgress((int) HandlerTime.getInstance().getRealTime(fullValue));
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

            if (type == R.id.work_time) {
                seekBar.setOnSeekBarChangeListener(new WorkSeekBar());
            } else if (type == R.id.break_time) {
                seekBar.setOnSeekBarChangeListener(new BreakSeekBar());
            } else if (type == R.id.long_break_time) {
                seekBar.setOnSeekBarChangeListener(new LongBreakSeekBar());
            } else if (type == R.id.works_before_a_long_break) {
                seekBar.setOnSeekBarChangeListener(new BeforeALongBreakSeekBar());
            } else if (type == R.id.goal) {
                seekBar.setOnSeekBarChangeListener(new GoalSeekBar());
            } else {
                throw new Exception("type is not identified");
            }

            integerSeekBarMap.put(type, seekBar);
            return seekBar;
        }
    }
}
