package com.example.pomodorotimer.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.pomodorotimer.R;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class HandlerSharedPreferences {

    private static final String TAG = "HandlerSharedPreferences";
    @SuppressLint("StaticFieldLeak")
    private static HandlerSharedPreferences instance;
    private static Activity activity;

    private final String WORKS_BEFORE_LONG_BREAK_TIME_ID = "WORKS_BEFORE_LONG_BREAK_TIME_ID";
    private final String WORK_ID = "WORK_ID";
    private final String BREAK_ID = "BREAK_ID";
    private final String LONG_BREAK_ID = "LONG_BREAK_ID";
    private final String DAILY_GOAL_ID = "DAILY_GOAL_ID";

    // Initialize default values properly
    private final long DEFAULT_TIME_WORK;
    private final long DEFAULT_TIME_BREAK;
    private final long DEFAULT_TIME_WORKS_BEFORE_LONG_BREAK;
    private final long DEFAULT_TIME_LONG_BREAK;
    private final long DEFAULT_TIME_DAILY_GOAL;

    // For real-time updates
    private Set<OnWorkTimeChangeListener> workTimeListeners = new HashSet<>();
    private Set<OnBreakTimeChangeListener> breakTimeListeners = new HashSet<>();
    private Set<OnLongBreakTimeChangeListener> longBreakTimeListeners = new HashSet<>();
    private Set<OnWorksBeforeLongBreakChangeListener> worksBeforeLongBreakListeners = new HashSet<>();
    private Set<OnDailyGoalChangeListener> dailyGoalListeners = new HashSet<>();

    // Interface za sve listener-e
    public interface OnWorkTimeChangeListener {
        void onWorkTimeChanged(long newWorkTime);
    }

    public interface OnBreakTimeChangeListener {
        void onBreakTimeChanged(long newBreakTime);
    }

    public interface OnLongBreakTimeChangeListener {
        void onLongBreakTimeChanged(long newLongBreakTime);
    }

    public interface OnWorksBeforeLongBreakChangeListener {
        void onWorksBeforeLongBreakChanged(int newWorksBeforeLongBreak);
    }

    public interface OnDailyGoalChangeListener {
        void onDailyGoalChanged(int newDailyGoal);
    }

    private HandlerSharedPreferences() throws Exception {
        HandlerStringToInt.setContext(activity.getBaseContext());
        DEFAULT_TIME_WORK = HandlerStringToInt.getInstance().getIntToString(R.string.work_time_default);
        DEFAULT_TIME_BREAK = HandlerStringToInt.getInstance().getIntToString(R.string.break_time_default);
        DEFAULT_TIME_WORKS_BEFORE_LONG_BREAK = HandlerStringToInt.getInstance().getIntToString(R.string.works_before_a_long_break_default);
        DEFAULT_TIME_LONG_BREAK = HandlerStringToInt.getInstance().getIntToString(R.string.long_break_time_default);
        DEFAULT_TIME_DAILY_GOAL = HandlerStringToInt.getInstance().getIntToString(R.string.daily_goal_default);
    }

    public static HandlerSharedPreferences getInstance() throws Exception {
        if (activity == null)
            throw new Exception("activity == null");

        if (instance == null)
            instance = new HandlerSharedPreferences();
        return instance;
    }

    public static void setActivity(@NotNull Activity activity) {
        HandlerSharedPreferences.activity = activity;
    }

    public void addWorkTimeChangeListener(OnWorkTimeChangeListener listener) {
        if (listener != null) {
            workTimeListeners.add(listener);
            Log.d(TAG, "Added work time listener: " + listener.getClass().getSimpleName());
        } else {
            Log.w(TAG, "Attempted to add null work time listener");
        }
    }

    public void removeWorkTimeChangeListener(OnWorkTimeChangeListener listener) {
        workTimeListeners.remove(listener);
    }

    public void addBreakTimeChangeListener(OnBreakTimeChangeListener listener) {
        if (listener != null) {
            breakTimeListeners.add(listener);
            Log.d(TAG, "Added break time listener: " + listener.getClass().getSimpleName());
        } else {
            Log.w(TAG, "Attempted to add null break time listener");
        }
    }

    public void removeBreakTimeChangeListener(OnBreakTimeChangeListener listener) {
        breakTimeListeners.remove(listener);
    }

    public void addLongBreakTimeChangeListener(OnLongBreakTimeChangeListener listener) {
        if (listener != null) {
            longBreakTimeListeners.add(listener);
            Log.d(TAG, "Added long break time listener: " + listener.getClass().getSimpleName());
        } else {
            Log.w(TAG, "Attempted to add null long break time listener");
        }
    }

    public void removeLongBreakTimeChangeListener(OnLongBreakTimeChangeListener listener) {
        longBreakTimeListeners.remove(listener);
    }

    public void addWorksBeforeLongBreakChangeListener(OnWorksBeforeLongBreakChangeListener listener) {
        if (listener != null) {
            worksBeforeLongBreakListeners.add(listener);
            Log.d(TAG, "Added works before long break listener: " + listener.getClass().getSimpleName());
        } else {
            Log.w(TAG, "Attempted to add null works before long break listener");
        }
    }

    public void removeWorksBeforeLongBreakChangeListener(OnWorksBeforeLongBreakChangeListener listener) {
        worksBeforeLongBreakListeners.remove(listener);
    }

    public void addDailyGoalChangeListener(OnDailyGoalChangeListener listener) {
        if (listener != null) {
            dailyGoalListeners.add(listener);
            Log.d(TAG, "Added daily goal listener: " + listener.getClass().getSimpleName());
        } else {
            Log.w(TAG, "Attempted to add null daily goal listener");
        }
    }

    public void removeDailyGoalChangeListener(OnDailyGoalChangeListener listener) {
        dailyGoalListeners.remove(listener);
    }

    private SharedPreferences.Editor getEditor() {
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        return sharedPref.edit();
    }

    public long getWorkTime() {
        SharedPreferences getShareData = activity.getPreferences(Context.MODE_PRIVATE);
        Log.d(TAG, "getWorkTime: " + HandlerTime.getInstance().getTime(getShareData.getLong(WORK_ID, DEFAULT_TIME_WORK)));
        return HandlerTime.getInstance().getTime(getShareData.getLong(WORK_ID, DEFAULT_TIME_WORK));
    }

    public void setWorkTime(long time) {
        Log.d(TAG, "setWorkTime: " + time);
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(WORK_ID, time);
        editor.apply();

        // Notify all listeners for real-time updates
        for (OnWorkTimeChangeListener listener : workTimeListeners) {
            if (listener != null) {
                try {
                    listener.onWorkTimeChanged(time);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying work time listener", e);
                }
            } else {
                Log.w(TAG, "Found null listener in workTimeListeners");
            }
        }
    }

    public long getBreakTime() {
        SharedPreferences getShareData = activity.getPreferences(Context.MODE_PRIVATE);
        long breakTime = HandlerTime.getInstance().getTime(getShareData.getLong(BREAK_ID, DEFAULT_TIME_BREAK));
        Log.d(TAG, "getBreakTime: " + breakTime + " ms (" + (breakTime / 60000) + " min)");
        return breakTime;
    }

    public void setBreakTime(long time) {
        Log.d(TAG, "setBreakTime: " + time);
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(BREAK_ID, time);
        editor.apply();

        // Notify all listeners for real-time updates
        for (OnBreakTimeChangeListener listener : breakTimeListeners) {
            if (listener != null) {
                try {
                    listener.onBreakTimeChanged(HandlerTime.getInstance().getTime(time));
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying break time listener", e);
                }
            } else {
                Log.w(TAG, "Found null listener in breakTimeListeners");
            }
        }
    }

    public long getLongBreakTime() {
        SharedPreferences getShareData = activity.getPreferences(Context.MODE_PRIVATE);
        Log.d(TAG, "getLongBreakTime: " + HandlerTime.getInstance().getTime(getShareData.getLong(LONG_BREAK_ID, DEFAULT_TIME_LONG_BREAK)));
        return HandlerTime.getInstance().getTime(getShareData.getLong(LONG_BREAK_ID, DEFAULT_TIME_LONG_BREAK));
    }

    public void setLongBreakTime(long time) {
        Log.d(TAG, "setLongBreakTime: " + time);
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(LONG_BREAK_ID, time);
        editor.apply();

        // Notify all listeners for real-time updates
        for (OnLongBreakTimeChangeListener listener : longBreakTimeListeners) {
            if (listener != null) {
                try {
                    listener.onLongBreakTimeChanged(HandlerTime.getInstance().getTime(time));
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying long break time listener", e);
                }
            } else {
                Log.w(TAG, "Found null listener in longBreakTimeListeners");
            }
        }
    }

    public long getWorksBeforeLongBreakTime() {
        SharedPreferences getShareData = activity.getPreferences(Context.MODE_PRIVATE);
        Log.d(TAG, "getWorksBeforeLongBreakTime: " + HandlerTime.getInstance().getTime(getShareData.getLong(WORKS_BEFORE_LONG_BREAK_TIME_ID, DEFAULT_TIME_WORKS_BEFORE_LONG_BREAK)));
        return HandlerTime.getInstance().getTime(getShareData.getLong(WORKS_BEFORE_LONG_BREAK_TIME_ID, DEFAULT_TIME_WORKS_BEFORE_LONG_BREAK));
    }

    public void setWorksBeforeLongBreak(int count) {
        Log.d(TAG, "setWorksBeforeLongBreak: " + count);
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(WORKS_BEFORE_LONG_BREAK_TIME_ID, count);
        editor.apply(); //saving to disk

        // Notify all listeners for real-time updates
        for (OnWorksBeforeLongBreakChangeListener listener : worksBeforeLongBreakListeners) {
            if (listener != null) {
                try {
                    listener.onWorksBeforeLongBreakChanged(count);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying works before long break listener", e);
                }
            } else {
                Log.w(TAG, "Found null listener in worksBeforeLongBreakListeners");
            }
        }
    }

    public long getDailyGoal() {
        SharedPreferences getShareData = activity.getPreferences(Context.MODE_PRIVATE);
        long dailyGoal = getShareData.getLong(DAILY_GOAL_ID, DEFAULT_TIME_DAILY_GOAL);
        Log.d(TAG, "getDailyGoal: " + dailyGoal + " sessions");
        return dailyGoal;
    }

    public void setDailyGoal(int dailyGoal) {
        Log.d(TAG, "setDailyGoal: " + dailyGoal);
        SharedPreferences.Editor editor = getEditor();
        editor.putLong(DAILY_GOAL_ID, (long) dailyGoal);
        editor.apply();

        // Notify all listeners for real-time updates
        for (OnDailyGoalChangeListener listener : dailyGoalListeners) {
            if (listener != null) {
                try {
                    listener.onDailyGoalChanged(dailyGoal);
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying daily goal listener", e);
                    e.printStackTrace();
                }
            } else {
                Log.w(TAG, "Found null listener in dailyGoalListeners");
            }
        }
    }
}
