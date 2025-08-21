package com.example.pomodorotimer.model.State;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pomodorotimer.util.HandlerAlert;
import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerSharedPreferences;
import com.example.pomodorotimer.util.HandlerTime;

import org.jetbrains.annotations.NotNull;

public class BreakState extends State {

    public static final String BREAK_STATE = "BreakState";

    public BreakState() {
    }
    @SuppressLint("ResourceAsColor")
    @Override
    public void start() {
        Log.d(BREAK_STATE, "I AM IN START.");

        try {
            long realSessionsBeforeLongBreak = HandlerTime.getInstance().getRealTime(HandlerSharedPreferences.getInstance().getSessionsBeforeLongBreak());

            if (ContextState.getInstance().getCurrentSession() >= realSessionsBeforeLongBreak) {
                Log.d(BREAK_STATE, "start: " + "I AM IN THE LONG BREAK TIME!");

                HandlerCountDownTime.getInstance().setLongBreakColor();
                HandlerCountDownTime.getInstance().setTimerMode(HandlerCountDownTime.TimerMode.LONG_BREAK);

                ContextState.getInstance().setCurrentSession(0);
                HandlerAlert.getInstance().showToast("Take a Long break");
                HandlerCountDownTime.getInstance().getmCvCountdownView().start(HandlerSharedPreferences.getInstance().getLongBreakTime());
            } else {
                HandlerCountDownTime.getInstance().setBreakColor();
                HandlerCountDownTime.getInstance().setTimerMode(HandlerCountDownTime.TimerMode.BREAK);

                HandlerAlert.getInstance().showToast("Take a break");
                HandlerCountDownTime.getInstance().getmCvCountdownView().start(HandlerSharedPreferences.getInstance().getBreakTime());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Log.d(BREAK_STATE, "I AM IN STOP.");

        try {
            State nextState = StateFlyweightFactory.getInstance().getState(WorkState.WORK_STATE);
            Log.d(BREAK_STATE, "Next State -> " + nextState.toString());
            ContextState.setState(nextState);
            ContextState.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "BREAK STATE";
    }
}