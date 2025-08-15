package com.example.pomodorotimer.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pomodorotimer.R;
import com.example.pomodorotimer.model.SeekBar.BeforeALongBreakSeekBar;
import com.example.pomodorotimer.model.SeekBar.BreakSeekBar;
import com.example.pomodorotimer.model.SeekBar.GoalSeekBar;
import com.example.pomodorotimer.model.SeekBar.LongBreakSeekBar;
import com.example.pomodorotimer.model.SeekBar.SeekBarFactory;
import com.example.pomodorotimer.model.SeekBar.WorkSeekBar;

public class SettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        SeekBarFactory.setView(root);
        try {
            SeekBarFactory.getInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Setup individual SeekBars with their own listeners
        setupSeekBars(root);

        return root;
    }

    private void setupSeekBars(View view) {
        SeekBar workTimeSeekBar = view.findViewById(R.id.work_time);
        SeekBar breakTimeSeekBar = view.findViewById(R.id.break_time);
        SeekBar longBreakTimeSeekBar = view.findViewById(R.id.long_break_time);
        SeekBar worksBeforeLongBreakSeekBar = view.findViewById(R.id.works_before_a_long_break);
        SeekBar dailyGoalSeekBar = view.findViewById(R.id.goal);

        // Set different listeners for each SeekBar
        if (workTimeSeekBar != null) {
            workTimeSeekBar.setOnSeekBarChangeListener(new WorkSeekBar());
        }
        if (breakTimeSeekBar != null) {
            breakTimeSeekBar.setOnSeekBarChangeListener(new BreakSeekBar());
        }
        if (longBreakTimeSeekBar != null) {
            longBreakTimeSeekBar.setOnSeekBarChangeListener(new LongBreakSeekBar());
        }
        if (worksBeforeLongBreakSeekBar != null) {
            worksBeforeLongBreakSeekBar.setOnSeekBarChangeListener(new BeforeALongBreakSeekBar());
        }
        if (dailyGoalSeekBar != null) {
            dailyGoalSeekBar.setOnSeekBarChangeListener(new GoalSeekBar());
        }
    }
}