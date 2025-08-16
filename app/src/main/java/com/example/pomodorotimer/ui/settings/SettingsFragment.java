package com.example.pomodorotimer.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
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

    private HandlerSharedPreferences handlerSharedPreferences;

    // Default values
    private static final int DEFAULT_WORK_DURATION = 25;
    private static final int DEFAULT_SHORT_BREAK = 5;
    private static final int DEFAULT_LONG_BREAK = 15;
    private static final int DEFAULT_SESSIONS = 4;
    private static final int DEFAULT_DAILY_GOAL = 8;

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
    }

    private void setupSeekBars() {
        seekBarWorkDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvWorkDurationValue.setText(progress + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarShortBreak.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvShortBreakValue.setText(progress + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekBarLongBreak.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvLongBreakValue.setText(progress + " minutes");
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
            // Get values from SeekBars
            int workMinutes = seekBarWorkDuration.getProgress();
            int breakMinutes = seekBarShortBreak.getProgress();
            int longBreakMinutes = seekBarLongBreak.getProgress();

            System.out.println("DEBUG - Saving work minutes: " + workMinutes);
            System.out.println("DEBUG - Saving break minutes: " + breakMinutes);
            System.out.println("DEBUG - Saving long break minutes: " + longBreakMinutes);

            // Convert to milliseconds
            long workTimeMs = workMinutes * 60 * 1000L;
            long breakTimeMs = breakMinutes * 60 * 1000L;
            long longBreakTimeMs = longBreakMinutes * 60 * 1000L;

            System.out.println("DEBUG - Converted work time: " + workTimeMs + " ms");
            System.out.println("DEBUG - Converted break time: " + breakTimeMs + " ms");
            System.out.println("DEBUG - Converted long break time: " + longBreakTimeMs + " ms");

            // Save using HandlerSharedPreferences - ovo će automatski obavestiti sve listenere
            handlerSharedPreferences.setWorkTime(workTimeMs);
            handlerSharedPreferences.setBreakTime(breakTimeMs);
            handlerSharedPreferences.setLongBreakTime(longBreakTimeMs);
            handlerSharedPreferences.setSessionsBeforeLongBreak(seekBarSessions.getProgress());
            handlerSharedPreferences.setDailyGoal(seekBarDailyGoal.getProgress());

            // Eksplicitno ažuriraj progress bar nakon promene daily goal-a
            try {
                HandlerProgressBar.getInstance().refreshProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Settings saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error saving settings", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadSettings() {
        try {
            // Load from HandlerSharedPreferences
            long workTimeMs = handlerSharedPreferences.getWorkTime();
            long breakTimeMs = handlerSharedPreferences.getBreakTime();
            long longBreakTimeMs = handlerSharedPreferences.getLongBreakTime();

            System.out.println("DEBUG - Raw work time: " + workTimeMs + " ms");
            System.out.println("DEBUG - Raw break time: " + breakTimeMs + " ms");
            System.out.println("DEBUG - Raw long break time: " + longBreakTimeMs + " ms");

            // Convert to minutes
            int workMinutes = (int) (workTimeMs / (1000 * 60));
            int breakMinutes = (int) (breakTimeMs / (1000 * 60));
            int longBreakMinutes = (int) (longBreakTimeMs / (1000 * 60));

            // Update UI
            seekBarWorkDuration.setProgress(workMinutes);
            tvWorkDurationValue.setText(workMinutes + " minutes");

            seekBarShortBreak.setProgress(breakMinutes);
            tvShortBreakValue.setText(breakMinutes + " minutes");

            seekBarLongBreak.setProgress(longBreakMinutes);
            tvLongBreakValue.setText(longBreakMinutes + " minutes");

            // Load sessions and daily goal
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
            // Reset SeekBars to default values
            seekBarWorkDuration.setProgress(DEFAULT_WORK_DURATION);
            tvWorkDurationValue.setText(DEFAULT_WORK_DURATION + " minutes");

            seekBarShortBreak.setProgress(DEFAULT_SHORT_BREAK);
            tvShortBreakValue.setText(DEFAULT_SHORT_BREAK + " minutes");

            seekBarLongBreak.setProgress(DEFAULT_LONG_BREAK);
            tvLongBreakValue.setText(DEFAULT_LONG_BREAK + " minutes");

            seekBarSessions.setProgress(DEFAULT_SESSIONS);
            tvSessionsValue.setText(DEFAULT_SESSIONS + " sessions");

            seekBarDailyGoal.setProgress(DEFAULT_DAILY_GOAL);
            tvDailyGoalValue.setText(DEFAULT_DAILY_GOAL + " sessions");

            // Save default values to SharedPreferences
            handlerSharedPreferences.setWorkTime(DEFAULT_WORK_DURATION * 60 * 1000L);
            handlerSharedPreferences.setBreakTime(DEFAULT_SHORT_BREAK * 60 * 1000L);
            handlerSharedPreferences.setLongBreakTime(DEFAULT_LONG_BREAK * 60 * 1000L);
            handlerSharedPreferences.setSessionsBeforeLongBreak(DEFAULT_SESSIONS);
            handlerSharedPreferences.setDailyGoal(DEFAULT_DAILY_GOAL);

            // Eksplicitno ažuriraj progress bar nakon reset-a
            try {
                HandlerProgressBar.getInstance().refreshProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(getContext(), "Settings reset to defaults!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error resetting settings", Toast.LENGTH_SHORT).show();
        }
    }
}