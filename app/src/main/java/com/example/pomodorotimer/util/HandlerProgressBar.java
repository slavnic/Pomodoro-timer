package com.example.pomodorotimer.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.example.pomodorotimer.R;
import com.example.pomodorotimer.data.HandlerDB;

import org.jetbrains.annotations.NotNull;

public class HandlerProgressBar implements HandlerSharedPreferences.OnDailyGoalChangeListener {

    private static final String TAG = "HandlerProgressBar";
    @SuppressLint("StaticFieldLeak")
    private static HandlerProgressBar instance;
    private static View root;
    private static NumberProgressBar numberProgressBar;
    private static Context context;

    private HandlerProgressBar() {
    }

    public static HandlerProgressBar getInstance() throws Exception {
        if (root == null)
            throw new Exception("root == null");

        if (instance == null) {
            instance = new HandlerProgressBar();
        }
        return instance;
    }

    @SuppressLint("ResourceType")
    private static void setColor() throws Exception {
        numberProgressBar.setProgressTextColor(HandlerColor.getInstance().getColorFromColorString(R.color.secondColor));
        numberProgressBar.setReachedBarColor(HandlerColor.getInstance().getColorFromColorString(R.color.firstColor));
        numberProgressBar.setUnreachedBarColor(HandlerColor.getInstance().getColorFromColorString(R.color.thirdColor));
    }

    private static void init() throws Exception {
        numberProgressBar = root.findViewById(R.id.number_progress_bar);

        int dailyGoal = HandlerSharedPreferences.getInstance().getDailyGoal();
        Log.d(TAG, "init - dailyGoal: " + dailyGoal + " sessions");

        numberProgressBar.setMax(100);

        try {
            int todaySessionCount = HandlerDB.getInstance().getTotalSessionsToday();
            int progressPercentage = 0;

            if (dailyGoal > 0) {
                progressPercentage = Math.min(100, (todaySessionCount * 100) / dailyGoal);
            }

            Log.d(TAG, "init - current progress: " + progressPercentage + "% (" + todaySessionCount + "/" + dailyGoal + " sessions)");
            numberProgressBar.setProgress(progressPercentage);

        } catch (Exception e) {
            Log.e(TAG, "Error loading current progress, starting from 0%", e);
            numberProgressBar.setProgress(0);
        }

        numberProgressBar.setReachedBarHeight(10);
        numberProgressBar.setProgressTextSize(35);

        try {
            setColor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        numberProgressBar.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                if (current >= 100) {
                    try {
                        HandlerAlert.getInstance().showToast("DAILY GOAL COMPLETE!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        try {
            if (instance != null) {
                HandlerSharedPreferences.getInstance().addDailyGoalChangeListener(instance);
                Log.d(TAG, "Registered for daily goal changes");
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to register for daily goal changes", e);
        }
    }

    public static void setView(@NotNull View root) throws Exception {
        HandlerProgressBar.root = root;
        HandlerProgressBar.context = root.getContext();
        init();
    }

    public void setMax(int max) {
        Log.d(TAG, "setMax: " + max + " sessions");
        updateProgressBasedOnSessions();
    }

    public void updateDailyGoal(int newDailyGoal) {
        try {
            int currentSessions = HandlerDB.getInstance().getTotalSessionsToday();
            int progressPercentage = 0;

            if (newDailyGoal > 0) {
                progressPercentage = Math.min(100, (currentSessions * 100) / newDailyGoal);
            }

            Log.d(TAG, "updateDailyGoal - new goal: " + newDailyGoal + ", current sessions: " + currentSessions + ", progress: " + progressPercentage + "%");

            if (numberProgressBar != null) {
                numberProgressBar.setProgress(progressPercentage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating daily goal", e);
        }
    }

    private void updateProgressBasedOnSessions() {
        try {
            int dailyGoal = (int) HandlerSharedPreferences.getInstance().getDailyGoal();
            int todaySessionCount = HandlerDB.getInstance().getTotalSessionsToday();

            int progressPercentage = 0;
            if (dailyGoal > 0) {
                progressPercentage = Math.min(100, (todaySessionCount * 100) / dailyGoal);
            }

            Log.d(TAG, "updateProgressBasedOnSessions - setting progress to: " + progressPercentage + "% (" + todaySessionCount + "/" + dailyGoal + " sessions)");
            if (numberProgressBar != null) {
                numberProgressBar.setProgress(progressPercentage);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating progress based on sessions", e);
        }
    }

    public int getProgress() {
        return numberProgressBar != null ? numberProgressBar.getProgress() : 0;
    }

    public void setPercent(int percent) {
        Log.d(TAG, "setPercent: " + percent + "%");
        if (numberProgressBar != null) {
            numberProgressBar.setProgress(percent);
        }
    }

    public void resetDailyProgress() {
        Log.d(TAG, "resetDailyProgress - resetting to 0% due to daily goal change");
        if (numberProgressBar != null) {
            numberProgressBar.setProgress(0);
        }
    }

    public void initializeTodayProgress() {
        Log.d(TAG, "Initializing today's progress");
        try {
            int completedSessions = HandlerDB.getInstance().getTotalSessionsToday();
            int dailyGoal = HandlerSharedPreferences.getInstance().getDailyGoal();

            Log.d(TAG, "Today's progress: " + completedSessions + "/" + dailyGoal + " sessions");
            updateProgressBar(completedSessions, dailyGoal);

        } catch (Exception e) {
            Log.e(TAG, "Error initializing today's progress", e);
        }
    }

    public void incrementTodaysProgress() {
        try {
            int dailyGoal = (int) HandlerSharedPreferences.getInstance().getDailyGoal();
            if (dailyGoal > 0) {
                int currentSessions = HandlerDB.getInstance().getTotalSessionsToday();

                int progressPercentage = Math.min(100, (currentSessions * 100) / dailyGoal);
                Log.d(TAG, "incrementTodaysProgress - setting progress to: " + progressPercentage + "% (" + currentSessions + "/" + dailyGoal + " sessions)");

                if (numberProgressBar != null) {
                    numberProgressBar.setProgress(progressPercentage);
                }
            } else {
                Log.w(TAG, "Daily goal is 0, cannot increment progress");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error incrementing today's progress", e);
        }
    }

    private void updateProgressBar(int completedSessions, int dailyGoal) {
        if (numberProgressBar != null) {
            int progressPercentage = 0;
            if (dailyGoal > 0) {
                progressPercentage = Math.min(100, (completedSessions * 100) / dailyGoal);
            }

            numberProgressBar.setProgress(progressPercentage);

            Log.d(TAG, "Progress bar updated: " + completedSessions + "/" + dailyGoal + " sessions (" + progressPercentage + "%)");

            // Ažuriraj tekst ako postoji
            updateProgressText(completedSessions, dailyGoal);
        } else {
            Log.w(TAG, "Progress bar is null");
        }
    }

    public void onSessionCompleted() {
        Log.d(TAG, "onSessionCompleted called");
        try {
            int completedSessions = HandlerDB.getInstance().getTotalSessionsToday();
            int dailyGoal = HandlerSharedPreferences.getInstance().getDailyGoal();

            Log.d(TAG, "Completed sessions today: " + completedSessions + ", Daily goal: " + dailyGoal);

            updateProgressBar(completedSessions, dailyGoal);

        } catch (Exception e) {
            Log.e(TAG, "Error updating progress bar", e);
            e.printStackTrace();
        }
    }

    private void updateProgressText(int completedSessions, int dailyGoal) {
    }

    @Override
    public void onDailyGoalChanged(int newDailyGoal) {
        if (numberProgressBar != null && context != null) {
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() -> {
                    updateDailyGoal(newDailyGoal);
                });
            } else {
                android.os.Handler mainHandler = new android.os.Handler(android.os.Looper.getMainLooper());
                mainHandler.post(() -> {
                    updateDailyGoal(newDailyGoal);
                });
            }
        } else {
            updateDailyGoal(newDailyGoal);
        }
    }

    public void onDailyGoalChanged() {
        Log.d(TAG, "Daily goal changed - updating progress bar");
        try {
            int completedSessions = HandlerDB.getInstance().getTotalSessionsToday();
            int newDailyGoal = HandlerSharedPreferences.getInstance().getDailyGoal();

            Log.d(TAG, "New daily goal: " + newDailyGoal + ", Completed sessions: " + completedSessions);
            updateProgressBar(completedSessions, newDailyGoal);

        } catch (Exception e) {
            Log.e(TAG, "Error updating progress bar after daily goal change", e);
        }
    }

    public void refreshProgress() {
        Log.d(TAG, "Refreshing progress bar");
        initializeTodayProgress();
    }


    public void destroy() {
        try {
            if (instance != null) {
                HandlerSharedPreferences.getInstance().removeDailyGoalChangeListener(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
