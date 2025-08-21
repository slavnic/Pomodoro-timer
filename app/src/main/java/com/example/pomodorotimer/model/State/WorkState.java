package com.example.pomodorotimer.model.State;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.pomodorotimer.data.HandlerDB;
import com.example.pomodorotimer.util.HandlerAlert;
import com.example.pomodorotimer.util.HandlerCountDownTime;
import com.example.pomodorotimer.util.HandlerProgressBar;
import com.example.pomodorotimer.util.HandlerSharedPreferences;

import org.jetbrains.annotations.NotNull;

public class WorkState extends State {

    public static final String WORK_STATE = "WorkState";

    public WorkState() {
    }
    @Override
    public void start() {
        Log.d(WORK_STATE, "I AM IN START.");

        try {
            HandlerCountDownTime.getInstance().setWorkColor();
            HandlerCountDownTime.getInstance().setTimerMode(HandlerCountDownTime.TimerMode.WORK);

            ContextState.getInstance().increaseSession();

            HandlerAlert.getInstance().showToast("Focus time!");
            HandlerCountDownTime.getInstance().getmCvCountdownView().start(HandlerSharedPreferences.getInstance().getWorkTime());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        Log.d(WORK_STATE, "I AM IN STOP.");

        try {
            long workTimeInMinutes = HandlerSharedPreferences.getInstance().getWorkTime() / (1000 * 60); // Convert milliseconds to minutes

            HandlerDB.getInstance().saveWorkSession(workTimeInMinutes);
            Log.d(WORK_STATE, "Work session saved: " + workTimeInMinutes + " minutes");

            State nextState = StateFlyweightFactory.getInstance().getState(BreakState.BREAK_STATE);
            Log.d(WORK_STATE, "Next State -> " + nextState.toString());
            ContextState.setState(nextState);
            ContextState.getInstance().start();

            HandlerProgressBar.getInstance().onSessionCompleted();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @NotNull
    @Override
    public String toString() {
        return "WORK STATE";
    }
}
