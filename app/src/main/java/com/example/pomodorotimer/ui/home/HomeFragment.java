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
        HandlerCountDownTime.OnWorkSessionCompletedListener,
        HandlerSharedPreferences.OnTimeChangeListener {

    private static final String TAG = "HomeFragment";
    private View rootView;
    private HandlerCountDownTime handlerCountDownTime;
    private HandlerSharedPreferences handlerSharedPreferences;

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

        try {
            // Get HandlerCountDownTime instance
            handlerCountDownTime = HandlerCountDownTime.getInstance();
            if (handlerCountDownTime != null) {
                handlerCountDownTime.setOnWorkSessionCompletedListener(this);
                Log.d(TAG, "Registered for work session completion");
            } else {
                Log.e(TAG, "HandlerCountDownTime instance is null");
            }

            // Register for time change notifications
            handlerSharedPreferences = HandlerSharedPreferences.getInstance();
            if (handlerSharedPreferences != null) {
                handlerSharedPreferences.addOnTimeChangeListener(this);
                Log.d(TAG, "Registered for time change notifications");
            } else {
                Log.e(TAG, "HandlerSharedPreferences instance is null");
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

    // Implementation of OnTimeChangeListener
    @Override
    public void onWorkTimeChanged(long workTimeMs) {
        Log.d(TAG, "Work time changed to: " + workTimeMs + " ms");
        try {
            if (handlerCountDownTime != null && !handlerCountDownTime.isRunning()) {
                Log.d(TAG, "Timer not running, recreating countdown with new time");
                // Reinitialize the countdown with new time
                if (rootView != null) {
                    HandlerCountDownTime.setCountDown(rootView);
                }
            } else {
                Log.d(TAG, "Timer is running, changes will apply after current session");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling work time change", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onBreakTimeChanged(long breakTimeMs) {
        Log.d(TAG, "Break time changed to: " + breakTimeMs + " ms");
        // Handle break time change if needed
    }

    @Override
    public void onLongBreakTimeChanged(long longBreakTimeMs) {
        Log.d(TAG, "Long break time changed to: " + longBreakTimeMs + " ms");
        // Handle long break time change if needed
    }

    @Override
    public void onDestroyView() {
        try {
            // Unregister listeners
            if (handlerCountDownTime != null) {
                handlerCountDownTime.setOnWorkSessionCompletedListener(null);
            }

            if (handlerSharedPreferences != null) {
                handlerSharedPreferences.removeOnTimeChangeListener(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering listeners", e);
            e.printStackTrace();
        }

        super.onDestroyView();
        rootView = null;
        handlerCountDownTime = null;
        handlerSharedPreferences = null;
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
            if (handlerCountDownTime != null) {
                handlerCountDownTime.goOnPause();
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