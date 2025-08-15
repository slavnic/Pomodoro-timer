package com.example.pomodorotimer.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.pomodorotimer.R;
import com.example.pomodorotimer.data.HandlerDB;
import com.example.pomodorotimer.util.HandlerColor;
import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerProgressBar;
import com.example.pomodorotimer.util.HandlerSharedPreferences;

public class HomeFragment extends Fragment implements
        HandlerSharedPreferences.OnWorkTimeChangeListener,
        HandlerSharedPreferences.OnBreakTimeChangeListener,
        HandlerSharedPreferences.OnLongBreakTimeChangeListener,
        HandlerSharedPreferences.OnWorksBeforeLongBreakChangeListener,
        HandlerSharedPreferences.OnDailyGoalChangeListener,
        HandlerCountDownTime.OnWorkSessionCompletedListener {

    private static final String TAG = "HomeFragment";
    private View rootView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home, container, false);

        Log.d(TAG, "onCreateView: ");

        /* SET VIEW */
        HandlerColor.setView(rootView);

        try {
            if (getActivity() != null) {
                HandlerDB.setContext(getActivity().getApplicationContext());
            } else if (getContext() != null) {
                HandlerDB.setContext(getContext().getApplicationContext());
            } else {
                Log.e(TAG, "Both activity and context are null, cannot initialize HandlerDB");
                return rootView;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing HandlerDB context", e);
            return rootView;
        }

        try {
            manageProgressBar(rootView);
        } catch (Exception e) {
            Log.e(TAG, "Error managing progress bar", e);
            e.printStackTrace();
        }

        try {
            manageCountDownTime(rootView);
        } catch (Exception e) {
            Log.e(TAG, "Error managing countdown time", e);
            e.printStackTrace();
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Register for all timer changes
        try {
            HandlerSharedPreferences sharedPrefs = HandlerSharedPreferences.getInstance();
            if (sharedPrefs != null) {
                sharedPrefs.addWorkTimeChangeListener(this);
                sharedPrefs.addBreakTimeChangeListener(this);
                sharedPrefs.addLongBreakTimeChangeListener(this);
                sharedPrefs.addWorksBeforeLongBreakChangeListener(this);
                sharedPrefs.addDailyGoalChangeListener(this);
            } else {
                Log.e(TAG, "HandlerSharedPreferences instance is null");
            }

            // Register for work session completion
            HandlerCountDownTime countDownHandler = HandlerCountDownTime.getInstance();
            if (countDownHandler != null) {
                countDownHandler.setOnWorkSessionCompletedListener(this);
                Log.d(TAG, "Registered for work session completion");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error registering listeners", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onWorkSessionCompleted() {
        Log.d(TAG, "Work session completed, updating daily progress");
        try {
            HandlerProgressBar progressBar = HandlerProgressBar.getInstance();
            if (progressBar != null) {
                progressBar.incrementTodaysProgress();
                Log.d(TAG, "Daily progress incremented");
            } else {
                Log.e(TAG, "HandlerProgressBar instance is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating daily progress after work session completion", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onWorkTimeChanged(long newWorkTime) {
        Log.d(TAG, "Work time changed to: " + newWorkTime + " minutes");

        try {
            HandlerCountDownTime countDownHandler = HandlerCountDownTime.getInstance();
            if (countDownHandler == null) {
                Log.e(TAG, "HandlerCountDownTime instance is null");
                return;
            }

            // Update only if currently in work mode
            if (countDownHandler.isInWorkMode() && countDownHandler.isRunning()) {
                Log.d(TAG, "Timer is running in work mode, restarting with new work time");
                countDownHandler.restartWithNewWorkTime(newWorkTime);
            } else if (!countDownHandler.isRunning()) {
                Log.d(TAG, "Timer not running, updating display only");
                if (rootView != null) {
                    manageCountDownTime(rootView);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating work time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onBreakTimeChanged(long newBreakTime) {
        Log.d(TAG, "Break time changed to: " + newBreakTime + " minutes");

        try {
            HandlerCountDownTime countDownHandler = HandlerCountDownTime.getInstance();
            if (countDownHandler == null) {
                Log.e(TAG, "HandlerCountDownTime instance is null");
                return;
            }

            // Update only if currently in break mode
            if (countDownHandler.isInBreakMode() && countDownHandler.isRunning()) {
                Log.d(TAG, "Timer is running in break mode, restarting with new break time");
                countDownHandler.restartWithNewBreakTime(newBreakTime);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating break time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onLongBreakTimeChanged(long newLongBreakTime) {
        Log.d(TAG, "Long break time changed to: " + newLongBreakTime + " minutes");

        try {
            HandlerCountDownTime countDownHandler = HandlerCountDownTime.getInstance();
            if (countDownHandler == null) {
                Log.e(TAG, "HandlerCountDownTime instance is null");
                return;
            }

            // Update only if currently in long break mode
            if (countDownHandler.isInLongBreakMode() && countDownHandler.isRunning()) {
                Log.d(TAG, "Timer is running in long break mode, restarting with new long break time");
                countDownHandler.restartWithNewLongBreakTime(newLongBreakTime);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error updating long break time: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onWorksBeforeLongBreakChanged(int newWorksBeforeLongBreak) {
        Log.d(TAG, "Works before long break changed to: " + newWorksBeforeLongBreak);
    }

    @Override
    public void onDailyGoalChanged(int newDailyGoal) {
        Log.d(TAG, "Daily goal changed to: " + newDailyGoal);
        try {
            HandlerProgressBar progressBar = HandlerProgressBar.getInstance();
            if (progressBar != null) {
                progressBar.updateDailyGoal(newDailyGoal);
                Log.d(TAG, "Daily goal updated, progress recalculated");
            } else {
                Log.e(TAG, "HandlerProgressBar instance is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating daily goal in progress bar", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        // Unregister all listeners to prevent memory leaks
        try {
            HandlerSharedPreferences sharedPrefs = HandlerSharedPreferences.getInstance();
            if (sharedPrefs != null) {
                sharedPrefs.removeWorkTimeChangeListener(this);
                sharedPrefs.removeBreakTimeChangeListener(this);
                sharedPrefs.removeLongBreakTimeChangeListener(this);
                sharedPrefs.removeWorksBeforeLongBreakChangeListener(this);
                sharedPrefs.removeDailyGoalChangeListener(this);
            }

            HandlerCountDownTime countDownHandler = HandlerCountDownTime.getInstance();
            if (countDownHandler != null) {
                countDownHandler.setOnWorkSessionCompletedListener(null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering listeners", e);
            e.printStackTrace();
        }

        super.onDestroyView();
        rootView = null;
        Log.d(TAG, "onDestroyView: ");
    }

    private void manageCountDownTime(View root) {
        try {
            HandlerCountDownTime.setCountDown(root);
        } catch (Exception e) {
            Log.e(TAG, "Error setting countdown", e);
            e.printStackTrace();
        }
    }

    private void manageProgressBar(View root) throws Exception {
        try {
            HandlerProgressBar.setView(root);
            Log.d(TAG, "Progress bar initialized with current daily progress");
        } catch (Exception e) {
            Log.e(TAG, "Error setting progress bar view", e);
            throw e;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        try {
            HandlerCountDownTime countDownHandler = HandlerCountDownTime.getInstance();
            if (countDownHandler != null) {
                countDownHandler.goOnPause();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling pause", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }
}