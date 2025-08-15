package com.example.pomodorotimer.util;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;

import com.example.pomodorotimer.R;
import com.example.pomodorotimer.data.HandlerDB;
import com.example.pomodorotimer.model.State.ContextState;

import org.jetbrains.annotations.NotNull;

import cn.iwgang.countdownview.CountdownView;
import cn.iwgang.countdownview.DynamicConfig;

public class HandlerCountDownTime {

    private static final String TAG = "HandlerCountDownTime";
    @SuppressLint("StaticFieldLeak")
    private static HandlerCountDownTime instance;
    private static CountdownView mCvCountdownView;
    private static boolean itWillBeStartAtNextState;
    private static boolean isCountdownRunning = false;
    private OnWorkSessionCompletedListener workSessionCompletedListener;


    private static TimerMode currentMode = TimerMode.WORK;

    public enum TimerMode {
        WORK, BREAK, LONG_BREAK
    }

    private HandlerCountDownTime() {
    }

    public boolean isInWorkMode() {
        return currentMode == TimerMode.WORK;
    }

    public boolean isInBreakMode() {
        return currentMode == TimerMode.BREAK;
    }

    public boolean isInLongBreakMode() {
        return currentMode == TimerMode.LONG_BREAK;
    }

    public interface OnWorkSessionCompletedListener {
        void onWorkSessionCompleted();
    }

    public void restartWithNewBreakTime(long newBreakTime) {
        Log.d(TAG, "restartWithNewBreakTime called with: " + newBreakTime + " ms (" + (newBreakTime / 60000) + " min)");

        if (mCvCountdownView != null) {
            Log.d(TAG, "Current mode before restart: " + currentMode);
            Log.d(TAG, "Current countdown time before restart: " + mCvCountdownView.getRemainTime() + " ms");

            boolean wasRunning = isCountdownRunning;
            Log.d(TAG, "Timer was running: " + wasRunning);

            mCvCountdownView.stop();
            isCountdownRunning = false;

            currentMode = TimerMode.BREAK;
            Log.d(TAG, "Mode changed to: " + currentMode);

            mCvCountdownView.updateShow(0);
            mCvCountdownView.updateShow(newBreakTime);
            Log.d(TAG, "Timer updated to: " + newBreakTime + " ms");

            if (wasRunning) {
                mCvCountdownView.start(newBreakTime);
                isCountdownRunning = true;
                itWillBeStartAtNextState = false;
                Log.d(TAG, "Break timer restarted with running state");
            } else {
                itWillBeStartAtNextState = true;
                Log.d(TAG, "Break timer updated in paused state");
            }

            try {
                setBreakColor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Break timer updated successfully to: " + (newBreakTime / 60000) + " minutes");
        } else {
            Log.e(TAG, "mCvCountdownView is null in restartWithNewBreakTime!");
        }
    }

    public void restartWithNewLongBreakTime(long newLongBreakTime) {
        if (mCvCountdownView != null) {
            Log.d(TAG, "Restarting long break timer with new time: " + newLongBreakTime + " ms");

            boolean wasRunning = isCountdownRunning;
            currentMode = TimerMode.LONG_BREAK;

            mCvCountdownView.stop();
            isCountdownRunning = false;

            mCvCountdownView.updateShow(newLongBreakTime);

            if (wasRunning) {
                mCvCountdownView.start(newLongBreakTime);
                isCountdownRunning = true;
                Log.d(TAG, "Long break timer restarted with running state");
            } else {
                itWillBeStartAtNextState = true;
                Log.d(TAG, "Long break timer updated in paused state");
            }

            try {
                setBreakColor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Long break timer updated successfully");
        }
    }

    public void restartWithNewWorkTime(long newWorkTime) {
        if (mCvCountdownView != null) {
            Log.d(TAG, "Restarting work timer with new time: " + newWorkTime + " ms");

            boolean wasRunning = isCountdownRunning;
            currentMode = TimerMode.WORK;

            mCvCountdownView.stop();
            isCountdownRunning = false;

            mCvCountdownView.updateShow(newWorkTime);

            if (wasRunning) {
                mCvCountdownView.start(newWorkTime);
                isCountdownRunning = true;
                Log.d(TAG, "Work timer restarted with running state");
            } else {
                itWillBeStartAtNextState = true;
                Log.d(TAG, "Work timer updated in paused state");
            }

            try {
                setWorkColor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Work timer updated successfully");
        }
    }

    public static HandlerCountDownTime getInstance() throws Exception {
        if (mCvCountdownView == null)
            throw new Exception("CountDownView == null");

        if (instance == null)
            instance = new HandlerCountDownTime();
        return instance;
    }

    public static void setCountDown(@NotNull View root) {
        itWillBeStartAtNextState = true;

        mCvCountdownView = root.findViewById(R.id.countDown);

        try {
            long timeInMilliseconds = HandlerSharedPreferences.getInstance().getWorkTime();
            mCvCountdownView.updateShow(timeInMilliseconds);
            currentMode = TimerMode.WORK; // Set initial mode to work
            Log.d(TAG, "Initial work time set to: " + (timeInMilliseconds / 60000) + " minutes (" + timeInMilliseconds + " ms)");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // set click listener
        mCvCountdownView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: CLICK ON COUNTDOWN -> itWillBeStartAtNextState: " + itWillBeStartAtNextState + ", currentMode: " + currentMode);

                try {
                    if (itWillBeStartAtNextState) {
                        long remainingTime = mCvCountdownView.getRemainTime();
                        long timeToStart = 0;

                        if (remainingTime > 0) {
                            timeToStart = remainingTime;
                            Log.d(TAG, "Resuming with remaining time: " + timeToStart + " ms (" + (timeToStart / 1000) + " sec)");
                        } else {
                            if (currentMode == TimerMode.WORK) {
                                timeToStart = HandlerSharedPreferences.getInstance().getWorkTime();
                                Log.d(TAG, "WORK MODE - starting fresh: " + timeToStart + " ms (" + (timeToStart / 60000) + " min)");
                            } else if (currentMode == TimerMode.BREAK) {
                                timeToStart = HandlerSharedPreferences.getInstance().getBreakTime();
                                Log.d(TAG, "BREAK MODE - starting fresh: " + timeToStart + " ms (" + (timeToStart / 60000) + " min)");
                            } else if (currentMode == TimerMode.LONG_BREAK) {
                                timeToStart = HandlerSharedPreferences.getInstance().getLongBreakTime();
                                Log.d(TAG, "LONG_BREAK MODE - starting fresh: " + timeToStart + " ms (" + (timeToStart / 60000) + " min)");
                            }

                            mCvCountdownView.updateShow(timeToStart);
                        }

                        mCvCountdownView.start(timeToStart);
                        Log.d(TAG, "Starting timer with: " + timeToStart + " ms for mode: " + currentMode);

                        isCountdownRunning = true;
                    } else {
                        mCvCountdownView.stop();
                        isCountdownRunning = false;
                        Log.d(TAG, "Timer paused with remaining time: " + mCvCountdownView.getRemainTime() + " ms");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                itWillBeStartAtNextState = !itWillBeStartAtNextState;
            }
        });

        // set stop listener
        mCvCountdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                Log.d(TAG, "onEnd: Timer finished for mode: " + currentMode);
                isCountdownRunning = false;
                itWillBeStartAtNextState = true;

                try {
                    if (currentMode == TimerMode.WORK) {
                        long workTimeInMinutes = HandlerSharedPreferences.getInstance().getWorkTime() / 60000;
                        HandlerDB.getInstance().saveWorkSession(workTimeInMinutes);
                        Log.d(TAG, "Work session saved: " + workTimeInMinutes + " minutes");

                        HandlerProgressBar.getInstance().onSessionCompleted();

                        try {
                            HandlerCountDownTime.getInstance().notifyWorkSessionCompleted();
                        } catch (Exception e) {
                            Log.e(TAG, "Error notifying work session completed", e);
                        }

                        currentMode = TimerMode.BREAK;
                        long breakTime = HandlerSharedPreferences.getInstance().getBreakTime();
                        Log.d(TAG, "DEBUG: getBreakTime() returned: " + breakTime + " ms (" + (breakTime / 60000) + " min)");
                        mCvCountdownView.updateShow(breakTime);
                        Log.d(TAG, "Switched to BREAK mode with time: " + breakTime + " ms");

                        HandlerCountDownTime.getInstance().setBreakColor();
                    } else if (currentMode == TimerMode.BREAK) {
                        currentMode = TimerMode.WORK;
                        long workTime = HandlerSharedPreferences.getInstance().getWorkTime();
                        Log.d(TAG, "DEBUG: getWorkTime() returned: " + workTime + " ms (" + (workTime / 60000) + " min)");
                        mCvCountdownView.updateShow(workTime);
                        Log.d(TAG, "Switched to WORK mode with time: " + workTime + " ms");

                        HandlerCountDownTime.getInstance().setWorkColor();

                    } else if (currentMode == TimerMode.LONG_BREAK) {
                        // Switch back to WORK mode after long break
                        currentMode = TimerMode.WORK;
                        long workTime = HandlerSharedPreferences.getInstance().getWorkTime();
                        Log.d(TAG, "DEBUG: getWorkTime() after long break returned: " + workTime + " ms (" + (workTime / 60000) + " min)");
                        mCvCountdownView.updateShow(workTime);
                        Log.d(TAG, "Switched to WORK mode with time: " + workTime + " ms");

                        HandlerCountDownTime.getInstance().setWorkColor();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void setTimerMode(TimerMode mode) {
        currentMode = mode;
        Log.d(TAG, "Timer mode changed to: " + mode);
    }

    public TimerMode getCurrentMode() {
        return currentMode;
    }

    public long getRemainingTime() throws Exception {
        return mCvCountdownView.getRemainTime();
    }

    public void setTime(float time) {
        mCvCountdownView.start((long) time);
        isCountdownRunning = true;
        itWillBeStartAtNextState = false;
    }

    private void setColor(int colorTime, int colorSuffix) throws Exception {
        mCvCountdownView.dynamicShow(
                new DynamicConfig.Builder()
                        .setTimeTextColor(colorTime)
                        .setSuffixTextColor(colorSuffix)
                        .build());
    }

    public void setWorkColor() throws Exception {
        setColor(HandlerColor.getInstance().getColorFromColorString(R.color.firstColor),
                HandlerColor.getInstance().getColorFromColorString(R.color.thirdColor));
    }

    public void setBreakColor() throws Exception {
        setColor(HandlerColor.getInstance().getColorFromColorString(R.color.thirdColor),
                HandlerColor.getInstance().getColorFromColorString(R.color.thirdColor));
    }

    public void goOnPause() throws Exception {
        Log.d(TAG, "goOnPause: ");
        itWillBeStartAtNextState = true;
        isCountdownRunning = false;
        ContextState.getInstance().pause();
    }

    public void startingOrResume() throws Exception {
        Log.d(TAG, "startingOrResume: Current mode: " + currentMode);

        if (mCvCountdownView != null) {
            long remainingTime = mCvCountdownView.getRemainTime();

            if (remainingTime > 0) {
                mCvCountdownView.start(remainingTime);
                Log.d(TAG, "Resuming with remaining time: " + remainingTime + " ms");
            } else {
                long timeToStart = 0;
                if (currentMode == TimerMode.WORK) {
                    timeToStart = HandlerSharedPreferences.getInstance().getWorkTime();
                } else if (currentMode == TimerMode.BREAK) {
                    timeToStart = HandlerSharedPreferences.getInstance().getBreakTime();
                } else if (currentMode == TimerMode.LONG_BREAK) {
                    timeToStart = HandlerSharedPreferences.getInstance().getLongBreakTime();
                }

                mCvCountdownView.updateShow(timeToStart);
                mCvCountdownView.start(timeToStart);
                Log.d(TAG, "Starting new timer with: " + timeToStart + " ms for mode: " + currentMode);
            }

            itWillBeStartAtNextState = false;
            isCountdownRunning = true;
            ContextState.getInstance().resume();
        }
    }

    public boolean isRunning() {
        return isCountdownRunning;
    }

    public void stop() {
        if (mCvCountdownView != null) {
            mCvCountdownView.stop();
            isCountdownRunning = false;
            itWillBeStartAtNextState = true;
        }
    }

    public void setOnWorkSessionCompletedListener(OnWorkSessionCompletedListener listener) {
        this.workSessionCompletedListener = listener;
        Log.d(TAG, "Work session completed listener set");
    }

    private void notifyWorkSessionCompleted() {
        Log.d(TAG, "Work session completed - notifying listener");
        if (workSessionCompletedListener != null) {
            workSessionCompletedListener.onWorkSessionCompleted();
        } else {
            Log.w(TAG, "No work session completed listener registered");
        }
    }

    public CountdownView getmCvCountdownView() {
        return mCvCountdownView;
    }
}
