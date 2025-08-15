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

    /*START STATE*/
    @Override
    public void start() {

        try {
            /*
             *  SET WORK COLOR AT COUNTDOWN OBJECT
             * */
            HandlerCountDownTime.getInstance().setWorkColor();

            /*
             * SET TIMER MODE TO WORK
             */
            HandlerCountDownTime.getInstance().setTimerMode(HandlerCountDownTime.TimerMode.WORK);

            HandlerAlert.getInstance().showToast("Start Work");
            Log.d(WORK_STATE, "start: " + HandlerSharedPreferences.getInstance().getWorkTime());
            HandlerCountDownTime.getInstance().getmCvCountdownView().start(HandlerSharedPreferences.getInstance().getWorkTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*STOP STATE*/
    @Override
    public void stop() {
        Log.d(WORK_STATE, "I AM IN STOP.");

        try {
            /*
             * SAVE COMPLETED WORK SESSION TO DATABASE
             * */
            long workTimeInMinutes = HandlerSharedPreferences.getInstance().getWorkTime() / (1000 * 60); // Convert milliseconds to minutes

            // Save work session to database
            HandlerDB.getInstance().saveWorkSession(workTimeInMinutes);
            Log.d(WORK_STATE, "Work session saved: " + workTimeInMinutes + " minutes");

            // change state
            State nextState = StateFlyweightFactory.getInstance().getState(BreakState.BREAK_STATE);
            Log.d(WORK_STATE, "Next State -> " + nextState.toString());
            ContextState.setState(nextState);
            ContextState.getInstance().start(); // PASS TO START STATE

            /*UPDATE PROGRESS BAR WITH COMPLETED SESSION*/
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
