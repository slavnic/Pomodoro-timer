package com.example.pomodorotimer.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pomodorotimer.MainActivity;
import com.example.pomodorotimer.R;
import com.example.pomodorotimer.util.HandlerSharedPreferences;
import com.example.pomodorotimer.util.HandlerTime;
import com.example.pomodorotimer.util.HandlerProgressBar;

public class SettingsFragment extends Fragment {

    private SeekBar seekBarWorkDuration, seekBarShortBreak, seekBarLongBreak, seekBarSessions, seekBarDailyGoal;
    private TextView tvWorkDurationValue, tvShortBreakValue, tvLongBreakValue, tvSessionsValue, tvDailyGoalValue;
    private Button btnSaveSettings, btnResetSettings;
    private Switch switchTimeUnit;

    private HandlerSharedPreferences handlerSharedPreferences;
    private boolean isSecondsMode = false;

    private static final int DEFAULT_WORK_DURATION = 25;
    private static final int DEFAULT_SHORT_BREAK = 5;
    private static final int DEFAULT_LONG_BREAK = 15;
    private static final int DEFAULT_SESSIONS = 4;
    private static final int DEFAULT_DAILY_GOAL = 8;
    private static final int TEST_WORK_DURATION = 10;
    private static final int TEST_SHORT_BREAK = 5;
    private static final int TEST_LONG_BREAK = 15;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        try {
            handlerSharedPreferences = HandlerSharedPreferences.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initViews(view);
        setupSeekBars();
        setupButtons();
        setupTimeUnitSwitch();
        loadSettings();

        return view;
    }

    private void initViews(View view) {
        seekBarWorkDuration = view.findViewById(R.id.seekBarWorkDuration);
        seekBarShortBreak = view.findViewById(R.id.seekBarShortBreak);
        seekBarLongBreak = view.findViewById(R.id.seekBarLongBreak);
        seekBarSessions = view.findViewById(R.id.seekBarSessions);
        seekBarDailyGoal = view.findViewById(R.id.seekBarDailyGoal);

        tvWorkDurationValue = view.findViewById(R.id.tvWorkDurationValue);
        tvShortBreakValue = view.findViewById(R.id.tvShortBreakValue);
        tvLongBreakValue = view.findViewById(R.id.tvLongBreakValue);
        tvSessionsValue = view.findViewById(R.id.tvSessionsValue);
        tvDailyGoalValue = view.findViewById(R.id.tvDailyGoalValue);

        btnSaveSettings = view.findViewById(R.id.btnSaveSettings);
        btnResetSettings = view.findViewById(R.id.btnResetSettings);
        switchTimeUnit = view.findViewById(R.id.switchTimeUnit);
    }

    private void setupTimeUnitSwitch() {
        switchTimeUnit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isSecondsMode = isChecked;
            updateSeekBarRanges();
            loadSettings();
        });
    }

    private void updateSeekBarRanges() {
        if (isSecondsMode) {
            seekBarWorkDuration.setMax(60);
            seekBarShortBreak.setMax(30);
            seekBarLongBreak.setMax(60);
        } else {
            seekBarWorkDuration.setMax(60);
            seekBarShortBreak.setMax(30);
            seekBarLongBreak.setMax(60);
        }
    }

    private void setupSeekBars() {
        seekBarWorkDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String unit = isSecondsMode ? " seconds" : " minutes";
                tvWorkDurationValue.setText(progress + unit);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarShortBreak.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String unit = isSecondsMode ? " seconds" : " minutes";
                tvShortBreakValue.setText(progress + unit);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarLongBreak.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String unit = isSecondsMode ? " seconds" : " minutes";
                tvLongBreakValue.setText(progress + unit);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarSessions.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvSessionsValue.setText(progress + " sessions");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarDailyGoal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvDailyGoalValue.setText(progress + " sessions");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    private void setupButtons() {
        btnSaveSettings.setOnClickListener(v -> saveSettings());
        btnResetSettings.setOnClickListener(v -> resetToDefaults());
    }

    private void saveSettings() {
        try {
            int workValue = seekBarWorkDuration.getProgress();
            int breakValue = seekBarShortBreak.getProgress();
            int longBreakValue = seekBarLongBreak.getProgress();

            System.out.println("DEBUG - Saving work value: " + workValue + (isSecondsMode ? " seconds" : " minutes"));
            System.out.println("DEBUG - Saving break value: " + breakValue + (isSecondsMode ? " seconds" : " minutes"));
            System.out.println("DEBUG - Saving long break value: " + longBreakValue + (isSecondsMode ? " seconds" : " minutes"));

            long workTimeMs, breakTimeMs, longBreakTimeMs;
            if (isSecondsMode) {
                workTimeMs = workValue * 1000L;
                breakTimeMs = breakValue * 1000L;
                longBreakTimeMs = longBreakValue * 1000L;
            } else {
                workTimeMs = workValue * 60 * 1000L;
                breakTimeMs = breakValue * 60 * 1000L;
                longBreakTimeMs = longBreakValue * 60 * 1000L;
            }

            System.out.println("DEBUG - Converted work time: " + workTimeMs + " ms");
            System.out.println("DEBUG - Converted break time: " + breakTimeMs + " ms");
            System.out.println("DEBUG - Converted long break time: " + longBreakTimeMs + " ms");

            handlerSharedPreferences.setWorkTime(workTimeMs);
            handlerSharedPreferences.setBreakTime(breakTimeMs);
            handlerSharedPreferences.setLongBreakTime(longBreakTimeMs);
            handlerSharedPreferences.setSessionsBeforeLongBreak(seekBarSessions.getProgress());
            handlerSharedPreferences.setDailyGoal(seekBarDailyGoal.getProgress());

            try {
                HandlerProgressBar.getInstance().refreshProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }

            String unit = isSecondsMode ? "seconds" : "minutes";
            Toast.makeText(getContext(), "Settings saved successfully! Using " + unit + " mode.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSettings() {
        try {
            long workTimeMs = handlerSharedPreferences.getWorkTime();
            long breakTimeMs = handlerSharedPreferences.getBreakTime();
            long longBreakTimeMs = handlerSharedPreferences.getLongBreakTime();

            System.out.println("DEBUG - Raw work time: " + workTimeMs + " ms");
            System.out.println("DEBUG - Raw break time: " + breakTimeMs + " ms");
            System.out.println("DEBUG - Raw long break time: " + longBreakTimeMs + " ms");

            int workValue, breakValue, longBreakValue;
            String unit;

            if (isSecondsMode) {
                workValue = (int) (workTimeMs / 1000);
                breakValue = (int) (breakTimeMs / 1000);
                longBreakValue = (int) (longBreakTimeMs / 1000);
                unit = " seconds";

                workValue = Math.max(1, Math.min(60, workValue));
                breakValue = Math.max(1, Math.min(30, breakValue));
                longBreakValue = Math.max(5, Math.min(60, longBreakValue));
            } else {
                workValue = (int) (workTimeMs / (1000 * 60));
                breakValue = (int) (breakTimeMs / (1000 * 60));
                longBreakValue = (int) (longBreakTimeMs / (1000 * 60));
                unit = " minutes";

                workValue = Math.max(1, Math.min(60, workValue));
                breakValue = Math.max(1, Math.min(30, breakValue));
                longBreakValue = Math.max(5, Math.min(60, longBreakValue));
            }

            seekBarWorkDuration.setProgress(workValue);
            tvWorkDurationValue.setText(workValue + unit);

            seekBarShortBreak.setProgress(breakValue);
            tvShortBreakValue.setText(breakValue + unit);

            seekBarLongBreak.setProgress(longBreakValue);
            tvLongBreakValue.setText(longBreakValue + unit);

            int sessions = handlerSharedPreferences.getSessionsBeforeLongBreak();
            int dailyGoal = handlerSharedPreferences.getDailyGoal();

            seekBarSessions.setProgress(sessions);
            tvSessionsValue.setText(sessions + " sessions");

            seekBarDailyGoal.setProgress(dailyGoal);
            tvDailyGoalValue.setText(dailyGoal + " sessions");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetToDefaults() {
        try {
            int workDefault, breakDefault, longBreakDefault;
            String unit;

            if (isSecondsMode) {
                workDefault = TEST_WORK_DURATION;
                breakDefault = TEST_SHORT_BREAK;
                longBreakDefault = TEST_LONG_BREAK;
                unit = " seconds";
            } else {
                workDefault = DEFAULT_WORK_DURATION;
                breakDefault = DEFAULT_SHORT_BREAK;
                longBreakDefault = DEFAULT_LONG_BREAK;
                unit = " minutes";
            }

            seekBarWorkDuration.setProgress(workDefault);
            tvWorkDurationValue.setText(workDefault + unit);

            seekBarShortBreak.setProgress(breakDefault);
            tvShortBreakValue.setText(breakDefault + unit);

            seekBarLongBreak.setProgress(longBreakDefault);
            tvLongBreakValue.setText(longBreakDefault + unit);

            seekBarSessions.setProgress(DEFAULT_SESSIONS);
            tvSessionsValue.setText(DEFAULT_SESSIONS + " sessions");

            seekBarDailyGoal.setProgress(DEFAULT_DAILY_GOAL);
            tvDailyGoalValue.setText(DEFAULT_DAILY_GOAL + " sessions");

            long workTimeMs, breakTimeMs, longBreakTimeMs;
            if (isSecondsMode) {
                workTimeMs = workDefault * 1000L;
                breakTimeMs = breakDefault * 1000L;
                longBreakTimeMs = longBreakDefault * 1000L;
            } else {
                workTimeMs = workDefault * 60 * 1000L;
                breakTimeMs = breakDefault * 60 * 1000L;
                longBreakTimeMs = longBreakDefault * 60 * 1000L;
            }

            handlerSharedPreferences.setWorkTime(workTimeMs);
            handlerSharedPreferences.setBreakTime(breakTimeMs);
            handlerSharedPreferences.setLongBreakTime(longBreakTimeMs);
            handlerSharedPreferences.setSessionsBeforeLongBreak(DEFAULT_SESSIONS);
            handlerSharedPreferences.setDailyGoal(DEFAULT_DAILY_GOAL);

            try {
                HandlerProgressBar.getInstance().refreshProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Settings reset to defaults! Using " + (isSecondsMode ? "seconds" : "minutes") + " mode.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error resetting settings", Toast.LENGTH_SHORT).show();
        }
    }
}
