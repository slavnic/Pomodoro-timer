package com.example.pomodorotimer.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class HandlerSharedPreferences {
    private static final String TAG = "HandlerSharedPreferences";
    private static HandlerSharedPreferences instance;
    private SharedPreferences sharedPreferences;
    private List<OnTimeChangeListener> timeChangeListeners = new ArrayList<>();
    private List<OnDailyGoalChangeListener> dailyGoalChangeListeners = new ArrayList<>();

    private static final String WORK_TIME_KEY = "work_time";
    private static final String BREAK_TIME_KEY = "break_time";
    private static final String LONG_BREAK_TIME_KEY = "long_break_time";
    private static final String SESSIONS_BEFORE_LONG_BREAK_KEY = "sessions_before_long_break";
    private static final String DAILY_GOAL_KEY = "daily_goal";

    public interface OnTimeChangeListener {
        void onWorkTimeChanged(long workTimeMs);
        void onBreakTimeChanged(long breakTimeMs);
        void onLongBreakTimeChanged(long longBreakTimeMs);
    }

    public interface OnDailyGoalChangeListener {
        void onDailyGoalChanged(int newDailyGoal);
    }

    private HandlerSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("PomodoroPrefs", Context.MODE_PRIVATE);
    }

    public static synchronized HandlerSharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new HandlerSharedPreferences(context);
        }
        return instance;
    }

    public static HandlerSharedPreferences getInstance() {
        if (instance == null) {
            throw new IllegalStateException("HandlerSharedPreferences must be initialized with context first");
        }
        return instance;
    }

    // Time change listener management
    public void addOnTimeChangeListener(OnTimeChangeListener listener) {
        if (listener != null && !timeChangeListeners.contains(listener)) {
            timeChangeListeners.add(listener);
            Log.d(TAG, "Added time change listener. Total listeners: " + timeChangeListeners.size());
        }
    }

    public void removeOnTimeChangeListener(OnTimeChangeListener listener) {
        if (listener != null) {
            timeChangeListeners.remove(listener);
            Log.d(TAG, "Removed time change listener. Total listeners: " + timeChangeListeners.size());
        }
    }

    // Daily goal change listener management
    public void addDailyGoalChangeListener(OnDailyGoalChangeListener listener) {
        if (listener != null && !dailyGoalChangeListeners.contains(listener)) {
            dailyGoalChangeListeners.add(listener);
            Log.d(TAG, "Added daily goal change listener. Total listeners: " + dailyGoalChangeListeners.size());
        }
    }

    public void removeDailyGoalChangeListener(OnDailyGoalChangeListener listener) {
        if (listener != null) {
            dailyGoalChangeListeners.remove(listener);
            Log.d(TAG, "Removed daily goal change listener. Total listeners: " + dailyGoalChangeListeners.size());
        }
    }

    // Work Time
    public void setWorkTime(long workTimeMs) {
        Log.d(TAG, "setWorkTime: " + workTimeMs + " ms");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(WORK_TIME_KEY, workTimeMs);
        editor.apply();

        // Notify listeners
        for (OnTimeChangeListener listener : timeChangeListeners) {
            listener.onWorkTimeChanged(workTimeMs);
        }
    }

    public long getWorkTime() {
        long workTime = sharedPreferences.getLong(WORK_TIME_KEY, 25 * 60 * 1000L);
        Log.d(TAG, "getWorkTime: " + workTime + " ms");
        return workTime;
    }

    // Break Time
    public void setBreakTime(long breakTimeMs) {
        Log.d(TAG, "setBreakTime: " + breakTimeMs + " ms");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(BREAK_TIME_KEY, breakTimeMs);
        editor.apply();

        // Notify listeners
        for (OnTimeChangeListener listener : timeChangeListeners) {
            listener.onBreakTimeChanged(breakTimeMs);
        }
    }

    public long getBreakTime() {
        long breakTime = sharedPreferences.getLong(BREAK_TIME_KEY, 5 * 60 * 1000L);
        Log.d(TAG, "getBreakTime: " + breakTime + " ms");
        return breakTime;
    }

    // Long Break Time
    public void setLongBreakTime(long longBreakTimeMs) {
        Log.d(TAG, "setLongBreakTime: " + longBreakTimeMs + " ms");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LONG_BREAK_TIME_KEY, longBreakTimeMs);
        editor.apply();

        // Notify listeners
        for (OnTimeChangeListener listener : timeChangeListeners) {
            listener.onLongBreakTimeChanged(longBreakTimeMs);
        }
    }

    public long getLongBreakTime() {
        long longBreakTime = sharedPreferences.getLong(LONG_BREAK_TIME_KEY, 15 * 60 * 1000L);
        Log.d(TAG, "getLongBreakTime: " + longBreakTime + " ms");
        return longBreakTime;
    }

    // Sessions before long break
    public void setSessionsBeforeLongBreak(int sessions) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SESSIONS_BEFORE_LONG_BREAK_KEY, sessions);
        editor.apply();
    }

    public int getSessionsBeforeLongBreak() {
        return sharedPreferences.getInt(SESSIONS_BEFORE_LONG_BREAK_KEY, 4);
    }

    // Daily goal
    public void setDailyGoal(int goal) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DAILY_GOAL_KEY, goal);
        editor.apply();

        // Notify daily goal change listeners
        for (OnDailyGoalChangeListener listener : dailyGoalChangeListeners) {
            listener.onDailyGoalChanged(goal);
        }
    }

    public int getDailyGoal() {
        return sharedPreferences.getInt(DAILY_GOAL_KEY, 8);
    }
}