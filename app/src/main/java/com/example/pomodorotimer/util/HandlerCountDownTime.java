package com.example.pomodorotimer.util;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private static Context context; // Add context for HandlerSound

    private static long currentWorkMinutes = 0;
    private static boolean workCompleted = false;
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

    public void restartWithNewLongBreakTime(long newLongBreakTime, long oldLongBreakTime) {
        if (mCvCountdownView != null && currentMode == TimerMode.LONG_BREAK) {
            Log.d(TAG, "Restarting long break timer with new time: " + newLongBreakTime + " ms (old: " + oldLongBreakTime + " ms)");

            boolean wasRunning = isCountdownRunning;
            long oldRemaining = mCvCountdownView.getRemainTime();

            long elapsed = oldLongBreakTime - oldRemaining;
            long newRemaining = Math.max(newLongBreakTime - elapsed, 0);

            mCvCountdownView.stop();
            isCountdownRunning = false;

            mCvCountdownView.updateShow(newRemaining);

            if (wasRunning && newRemaining > 0) {
                mCvCountdownView.start(newRemaining);
                isCountdownRunning = true;
                itWillBeStartAtNextState = false;
                Log.d(TAG, "Long break timer restarted with running state");
            } else {
                itWillBeStartAtNextState = true;
                Log.d(TAG, "Long break timer updated in paused state");
            }

            try {
                setLongBreakColor();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.d(TAG, "Long break timer updated successfully");
        } else {
            Log.d(TAG, "Not in LONG_BREAK mode, timer not updated");
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

        // Store context for HandlerSound
        context = root.getContext();

        mCvCountdownView = root.findViewById(R.id.countDown);

        try {
            long timeInMilliseconds;
            // Use the correct time based on the current mode
            if (currentMode == TimerMode.WORK) {
                timeInMilliseconds = HandlerSharedPreferences.getInstance().getWorkTime();
                Log.d(TAG, "Initial work time set to: " + (timeInMilliseconds / 60000) + " minutes (" + timeInMilliseconds + " ms)");
            } else if (currentMode == TimerMode.BREAK) {
                timeInMilliseconds = HandlerSharedPreferences.getInstance().getBreakTime();
                Log.d(TAG, "Initial break time set to: " + (timeInMilliseconds / 60000) + " minutes (" + timeInMilliseconds + " ms)");
            } else if (currentMode == TimerMode.LONG_BREAK) {
                timeInMilliseconds = HandlerSharedPreferences.getInstance().getLongBreakTime();
                Log.d(TAG, "Initial long break time set to: " + (timeInMilliseconds / 60000) + " minutes (" + timeInMilliseconds + " ms)");
            } else {
                // fallback to work time
                timeInMilliseconds = HandlerSharedPreferences.getInstance().getWorkTime();
                Log.d(TAG, "Fallback: Initial work time set to: " + (timeInMilliseconds / 60000) + " minutes (" + timeInMilliseconds + " ms)");
            }
            mCvCountdownView.updateShow(timeInMilliseconds);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        mCvCountdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                Log.d(TAG, "onEnd: Timer finished for mode: " + currentMode);
                isCountdownRunning = false;
                itWillBeStartAtNextState = true;

                try {
                    if (currentMode == TimerMode.WORK) {
                        // Play work time finished sound
                        if (context != null) {
                            HandlerSound.getInstance(context).playWorkTimeFinishedSound();
                            Log.d(TAG, "Playing work time finished sound");
                        }

                        // Convert work time to minutes (for very short testing times, use minimum of 0.1 minutes)
                        long workTimeMs = HandlerSharedPreferences.getInstance().getWorkTime();
                        currentWorkMinutes = Math.max(workTimeMs / 60000, 1); // Always save at least 1 minute equivalent
                        if (workTimeMs < 60000) {
                            // If less than 1 minute (testing mode), still count as 1 minute for statistics
                            currentWorkMinutes = 1;
                            Log.d(TAG, "Testing mode detected - work time " + workTimeMs + "ms counted as 1 minute for statistics");
                        } else {
                            currentWorkMinutes = workTimeMs / 60000;
                        }
                        workCompleted = true;
                        Log.d(TAG, "Work completed: " + currentWorkMinutes + " minutes (original: " + workTimeMs + "ms)");

                        // Check if it's time for long break
                        int sessionsBeforeLongBreak = HandlerSharedPreferences.getInstance().getSessionsBeforeLongBreak();
                        int todayCompletedSessions = HandlerDB.getInstance().getTotalSessionsToday();

                        Log.d(TAG, "Today's completed sessions: " + todayCompletedSessions + ", Sessions before long break: " + sessionsBeforeLongBreak);

                        // Check if next session should be long break (current sessions + 1 would be divisible by sessions before long break)
                        boolean shouldBeLongBreak = ((todayCompletedSessions + 1) % sessionsBeforeLongBreak == 0);

                        if (shouldBeLongBreak) {
                            currentMode = TimerMode.LONG_BREAK;
                            long longBreakTime = HandlerSharedPreferences.getInstance().getLongBreakTime();
                            mCvCountdownView.updateShow(longBreakTime);

                            try {
                                getInstance().setLongBreakColor();
                            } catch (Exception e) {
                                Log.e(TAG, "Error setting long break color", e);
                            }

                            Log.d(TAG, "Switched to LONG_BREAK mode with time: " + longBreakTime + " ms");
                        } else {
                            currentMode = TimerMode.BREAK;
                            long breakTime = HandlerSharedPreferences.getInstance().getBreakTime();
                            mCvCountdownView.updateShow(breakTime);

                            try {
                                getInstance().setBreakColor();
                            } catch (Exception e) {
                                Log.e(TAG, "Error setting break color", e);
                            }

                            Log.d(TAG, "Switched to BREAK mode with time: " + breakTime + " ms");
                        }

                    } else if (currentMode == TimerMode.BREAK) {
                        // Play short break time finished sound
                        if (context != null) {
                            HandlerSound.getInstance(context).playShortBreakTimeFinishedSound();
                            Log.d(TAG, "Playing short break time finished sound");
                        }

                        long breakTimeMs = HandlerSharedPreferences.getInstance().getBreakTime();
                        long breakMinutes;
                        if (breakTimeMs < 60000) {
                            // If less than 1 minute (testing mode), still count as 1 minute for statistics
                            breakMinutes = 1;
                            Log.d(TAG, "Testing mode detected - break time " + breakTimeMs + "ms counted as 1 minute for statistics");
                        } else {
                            breakMinutes = breakTimeMs / 60000;
                        }

                        if (workCompleted && currentWorkMinutes > 0) {
                            HandlerDB.getInstance().saveCompleteSession(currentWorkMinutes, breakMinutes);
                            Log.d(TAG, "Complete session saved: Work=" + currentWorkMinutes + "min, Break=" + breakMinutes + "min (original: " + breakTimeMs + "ms)");

                            try {
                                HandlerProgressBar.getInstance().onSessionCompleted();
                                Log.d(TAG, "Full cycle completed - daily progress updated");
                            } catch (Exception e) {
                                Log.e(TAG, "Error updating progress bar", e);
                            }

                            currentWorkMinutes = 0;
                            workCompleted = false;
                        }

                        currentMode = TimerMode.WORK;
                        long workTime = HandlerSharedPreferences.getInstance().getWorkTime();
                        mCvCountdownView.updateShow(workTime);

                        try {
                            getInstance().setWorkColor();
                        } catch (Exception e) {
                            Log.e(TAG, "Error setting work color", e);
                        }

                        Log.d(TAG, "Switched to WORK mode with time: " + workTime + " ms");

                    } else if (currentMode == TimerMode.LONG_BREAK) {
                        // Play long break time finished sound
                        if (context != null) {
                            HandlerSound.getInstance(context).playLongBreakTimeFinishedSound();
                            Log.d(TAG, "Playing long break time finished sound");
                        }

                        long longBreakTimeMs = HandlerSharedPreferences.getInstance().getLongBreakTime();
                        long longBreakMinutes;
                        if (longBreakTimeMs < 60000) {
                            // If less than 1 minute (testing mode), still count as 1 minute for statistics
                            longBreakMinutes = 1;
                            Log.d(TAG, "Testing mode detected - long break time " + longBreakTimeMs + "ms counted as 1 minute for statistics");
                        } else {
                            longBreakMinutes = longBreakTimeMs / 60000;
                        }

                        if (workCompleted && currentWorkMinutes > 0) {
                            HandlerDB.getInstance().saveCompleteSession(currentWorkMinutes, longBreakMinutes);
                            Log.d(TAG, "Complete session with long break saved: Work=" + currentWorkMinutes + "min, LongBreak=" + longBreakMinutes + "min (original: " + longBreakTimeMs + "ms)");

                            try {
                                HandlerProgressBar.getInstance().onSessionCompleted();
                                Log.d(TAG, "Full cycle with long break completed - daily progress updated");
                            } catch (Exception e) {
                                Log.e(TAG, "Error updating progress bar", e);
                            }

                            currentWorkMinutes = 0;
                            workCompleted = false;
                        }

                        currentMode = TimerMode.WORK;
                        long workTime = HandlerSharedPreferences.getInstance().getWorkTime();
                        mCvCountdownView.updateShow(workTime);

                        try {
                            getInstance().setWorkColor();
                        } catch (Exception e) {
                            Log.e(TAG, "Error setting work color", e);
                        }

                        Log.d(TAG, "Switched to WORK mode with time: " + workTime + " ms");
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
        Log.d(TAG, "Setting work color (green)");
        setColor(HandlerColor.getInstance().getColorFromColorString(R.color.firstColor),
                HandlerColor.getInstance().getColorFromColorString(R.color.thirdColor));
    }

    public void setBreakColor() throws Exception {
        Log.d(TAG, "Setting break color (orange)");
        setColor(HandlerColor.getInstance().getColorFromColorString(R.color.breakColor),
                HandlerColor.getInstance().getColorFromColorString(R.color.breakColorDark));
    }

    public void setLongBreakColor() throws Exception {
        Log.d(TAG, "Setting long break color (purple)");
        setColor(HandlerColor.getInstance().getColorFromColorString(R.color.longBreakColor),
                HandlerColor.getInstance().getColorFromColorString(R.color.longBreakColorDark));
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
