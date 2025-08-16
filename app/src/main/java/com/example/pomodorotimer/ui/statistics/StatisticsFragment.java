package com.example.pomodorotimer.ui.statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pomodorotimer.R;
import com.example.pomodorotimer.data.HandlerDB;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class StatisticsFragment extends Fragment {

    private CalendarView calendarView;
    private TextView tvTotalMinutes, tvSessionCount, tvBreakTime, tvSelectedDate;
    private TextView tvMonthlyWork, tvMonthlyBreak, tvMonthlySessions;
    private LinearLayout dailyStatsLayout, monthlyStatsLayout;
    private Button btnDailyStats, btnMonthlyStats;
    private HandlerDB handlerDB;

    private boolean isDailyView = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);

        initViews(view);
        setupCalendar();
        setupButtons();

        try {
            handlerDB = HandlerDB.getInstance();
            loadTodayStatistics();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        tvTotalMinutes = view.findViewById(R.id.tvTotalMinutes);
        tvSessionCount = view.findViewById(R.id.tvSessionCount);
        tvBreakTime = view.findViewById(R.id.tvBreakTime);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);

        tvMonthlyWork = view.findViewById(R.id.tvMonthlyWork);
        tvMonthlyBreak = view.findViewById(R.id.tvMonthlyBreak);
        tvMonthlySessions = view.findViewById(R.id.tvMonthlySessions);

        dailyStatsLayout = view.findViewById(R.id.dailyStatsLayout);
        monthlyStatsLayout = view.findViewById(R.id.monthlyStatsLayout);

        btnDailyStats = view.findViewById(R.id.btnDailyStats);
        btnMonthlyStats = view.findViewById(R.id.btnMonthlyStats);
    }

    private void setupButtons() {
        btnDailyStats.setOnClickListener(v -> {
            isDailyView = true;
            updateViewVisibility();
            loadTodayStatistics();
        });

        btnMonthlyStats.setOnClickListener(v -> {
            isDailyView = false;
            updateViewVisibility();
            loadCurrentMonthStatistics();
        });

        updateViewVisibility();
    }

    private void updateViewVisibility() {
        if (isDailyView) {
            dailyStatsLayout.setVisibility(View.VISIBLE);
            monthlyStatsLayout.setVisibility(View.GONE);

            // Update button styles
            btnDailyStats.setBackgroundTintList(getResources().getColorStateList(R.color.firstColor));
            btnMonthlyStats.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            dailyStatsLayout.setVisibility(View.GONE);
            monthlyStatsLayout.setVisibility(View.VISIBLE);

            // Update button styles
            btnDailyStats.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
            btnMonthlyStats.setBackgroundTintList(getResources().getColorStateList(R.color.firstColor));
        }
    }

    private void setupCalendar() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                Date selectedDate = calendar.getTime();

                if (isDailyView) {
                    loadStatisticsForDate(selectedDate);
                    updateSelectedDateText(selectedDate);
                } else {
                    loadMonthlyStatistics(year, month);
                    updateSelectedMonthText(year, month);
                }
            }
        });
    }

    private void loadTodayStatistics() {
        Date today = new Date();
        loadStatisticsForDate(today);
        updateSelectedDateText(today);
    }

    private void loadCurrentMonthStatistics() {
        Calendar calendar = Calendar.getInstance();
        loadMonthlyStatistics(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
        updateSelectedMonthText(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH));
    }

    private void loadStatisticsForDate(Date date) {
        try {
            // Koristi nove metode za odvojeno čitanje work i break minuta
            int workMinutes = handlerDB.getTotalWorkMinutesForDate(date);
            int breakMinutes = handlerDB.getTotalBreakMinutesForDate(date);
            int sessionCount = handlerDB.getSessionCountForDate(date);

            // Prikaži SAMO work minute u totalMinutes
            tvTotalMinutes.setText(String.valueOf(workMinutes));
            tvSessionCount.setText(String.valueOf(sessionCount));
            // Prikaži break minute odvojeno
            tvBreakTime.setText(String.valueOf(breakMinutes));
        } catch (Exception e) {
            e.printStackTrace();
            tvTotalMinutes.setText("0");
            tvSessionCount.setText("0");
            tvBreakTime.setText("0");
        }
    }

    private void loadMonthlyStatistics(int year, int month) {
        try {
            Map<String, Integer> workStats = handlerDB.getMonthlyWorkStatistics(year, month);
            int monthlyBreakMinutes = handlerDB.getMonthlyBreakMinutes(year, month);

            tvMonthlyWork.setText(String.valueOf(workStats.get("workTime")));
            tvMonthlyBreak.setText(String.valueOf(monthlyBreakMinutes));
            tvMonthlySessions.setText(String.valueOf(workStats.get("sessions")));
        } catch (Exception e) {
            e.printStackTrace();
            tvMonthlyWork.setText("0");
            tvMonthlyBreak.setText("0");
            tvMonthlySessions.setText("0");
        }
    }

    private void updateSelectedDateText(Date date) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
        tvSelectedDate.setText("Statistics for: " + sdf.format(date));
    }

    private void updateSelectedMonthText(int year, int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        tvSelectedDate.setText("Statistics for: " + monthNames[month] + " " + year);
    }
}