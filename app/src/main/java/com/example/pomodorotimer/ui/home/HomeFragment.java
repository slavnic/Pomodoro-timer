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
                Log.d(TAG, "HandlerCountDownTime instance obtained");
            } else {
                Log.e(TAG, "HandlerCountDownTime instance is null");
            }

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
        try {
            if (handlerCountDownTime != null && handlerCountDownTime.isInBreakMode()) {
                Log.d(TAG, "Currently in break mode, updating timer with new break time");
                handlerCountDownTime.restartWithNewBreakTime(breakTimeMs);
            } else {
                Log.d(TAG, "Not in break mode, changes will apply when break starts");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling break time change", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onLongBreakTimeChanged(long longBreakTimeMs) {
        Log.d(TAG, "Long break time changed to: " + longBreakTimeMs + " ms");
        try {
            if (handlerCountDownTime != null) {
                Log.d(TAG, "HandlerCountDownTime is not null");
                Log.d(TAG, "Current timer mode: " + handlerCountDownTime.getCurrentMode());
                Log.d(TAG, "Is in long break mode: " + handlerCountDownTime.isInLongBreakMode());
                Log.d(TAG, "Is timer running: " + handlerCountDownTime.isRunning());

                if (handlerCountDownTime.isInLongBreakMode()) {
                    long oldLongBreakTime = longBreakTimeMs;
                    try {
                        long remaining = handlerCountDownTime.getRemainingTime();
                        oldLongBreakTime = remaining;
                    } catch (Exception e) {
                        Log.e(TAG, "Error getting remaining time for long break", e);
                    }
                    handlerCountDownTime.restartWithNewLongBreakTime(longBreakTimeMs, oldLongBreakTime);
                } else {
                    Log.d(TAG, "Not in long break mode, will not update timer");
                }
            } else {
                Log.e(TAG, "HandlerCountDownTime is null!");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error handling long break time change", e);
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        try {
            // Unregister listeners

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