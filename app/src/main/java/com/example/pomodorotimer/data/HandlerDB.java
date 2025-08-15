package com.example.pomodorotimer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HandlerDB extends SQLiteOpenHelper {

    private static final String TAG = "HandlerDB";
    private static final String DATABASE_NAME = "work_timer.db";
    private static final int DATABASE_VERSION = 2; // Increment version to trigger migration

    // Table name and columns
    private static final String TABLE_WORK_SESSIONS = "work_sessions";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_WORK_TIME_MINUTES = "work_time_minutes";
    private static final String KEY_SESSION_COUNT = "session_count"; // Add session count column

    private static HandlerDB instance;
    private static Context context;

    public HandlerDB(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static HandlerDB getInstance(Context context) {
        if (instance == null) {
            instance = new HandlerDB(context);
        }
        return instance;
    }

    public static HandlerDB getInstance() throws Exception {
        if (context == null) {
            throw new Exception("Context is null.");
        }
        if (instance == null) {
            instance = new HandlerDB(context);
        }
        return instance;
    }

    public static void setContext(Context context) {
        HandlerDB.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_WORK_SESSIONS_TABLE = "CREATE TABLE " + TABLE_WORK_SESSIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT NOT NULL,"
                + KEY_WORK_TIME_MINUTES + " INTEGER NOT NULL,"
                + KEY_SESSION_COUNT + " INTEGER DEFAULT 1" // Default session count to 1
                + ")";
        db.execSQL(CREATE_WORK_SESSIONS_TABLE);
        Log.d(TAG, "Database table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add session_count column to existing table
            try {
                db.execSQL("ALTER TABLE " + TABLE_WORK_SESSIONS + " ADD COLUMN " + KEY_SESSION_COUNT + " INTEGER DEFAULT 1");
                Log.d(TAG, "Added session_count column to existing table");
            } catch (Exception e) {
                Log.e(TAG, "Error adding session_count column: " + e.getMessage());
                // If ALTER fails, recreate table (this will lose data but ensures functionality)
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORK_SESSIONS);
                onCreate(db);
            }
        }
    }

    public void saveWorkSession(long workTimeInMinutes) {
        String currentDate = getCurrentDate();
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, currentDate);
            values.put(KEY_WORK_TIME_MINUTES, workTimeInMinutes);

            // Check if record exists for today
            Cursor cursor = db.query(TABLE_WORK_SESSIONS,
                    new String[]{KEY_WORK_TIME_MINUTES, KEY_SESSION_COUNT},
                    KEY_DATE + " = ?",
                    new String[]{currentDate},
                    null, null, null);

            if (cursor.moveToFirst()) {
                // Update existing record - add to today's total
                long existingTime = cursor.getLong(cursor.getColumnIndexOrThrow(KEY_WORK_TIME_MINUTES));
                int sessionCount = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SESSION_COUNT));
                values.put(KEY_WORK_TIME_MINUTES, existingTime + workTimeInMinutes);
                values.put(KEY_SESSION_COUNT, sessionCount + 1); // Increment session count
                int rowsUpdated = db.update(TABLE_WORK_SESSIONS, values, KEY_DATE + " = ?", new String[]{currentDate});
                Log.d(TAG, "Updated work session: " + workTimeInMinutes + " minutes added to " + currentDate + " (rows updated: " + rowsUpdated + ")");
            } else {
                // Insert new record for today
                values.put(KEY_SESSION_COUNT, 1); // New session, count is 1
                long result = db.insert(TABLE_WORK_SESSIONS, null, values);
                Log.d(TAG, "Saved new work session: " + workTimeInMinutes + " minutes for " + currentDate + " (row id: " + result + ")");
            }

            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving work session: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public Map<String, Float> getDailyWorkTime() {
        Map<String, Float> dailyData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " + KEY_DATE + ", SUM(" + KEY_WORK_TIME_MINUTES + ") as total_minutes " +
                "FROM " + TABLE_WORK_SESSIONS + " " +
                "WHERE " + KEY_DATE + " >= date('now', '-30 days') " +
                "GROUP BY " + KEY_DATE + " " +
                "ORDER BY " + KEY_DATE;

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    String date = cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE));
                    float totalMinutes = cursor.getFloat(cursor.getColumnIndexOrThrow("total_minutes"));
                    float totalHours = totalMinutes / 60f; // Convert to hours
                    dailyData.put(date, totalHours);
                    Log.d(TAG, "Daily data - Date: " + date + ", Hours: " + totalHours);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting daily work time: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return dailyData;
    }

    public Map<Integer, Float> getMonthlyWorkTime() {
        Map<Integer, Float> monthlyData = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT strftime('%m', " + KEY_DATE + ") as month, " +
                "SUM(" + KEY_WORK_TIME_MINUTES + ") as total_minutes " +
                "FROM " + TABLE_WORK_SESSIONS + " " +
                "WHERE strftime('%Y', " + KEY_DATE + ") = strftime('%Y', 'now') " +
                "GROUP BY strftime('%m', " + KEY_DATE + ") " +
                "ORDER BY month";

        Cursor cursor = db.rawQuery(query, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    int month = cursor.getInt(cursor.getColumnIndexOrThrow("month"));
                    float totalMinutes = cursor.getFloat(cursor.getColumnIndexOrThrow("total_minutes"));
                    float totalHours = totalMinutes / 60f; // Convert to hours
                    monthlyData.put(month - 1, totalHours); // Month index 0-11 for chart
                    Log.d(TAG, "Monthly data - Month: " + month + ", Hours: " + totalHours);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly work time: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return monthlyData;
    }

    public float getTodayWorkTime() {
        String currentDate = getCurrentDate();
        SQLiteDatabase db = this.getReadableDatabase();
        float todayHours = 0f;

        String query = "SELECT SUM(" + KEY_WORK_TIME_MINUTES + ") as total_minutes " +
                "FROM " + TABLE_WORK_SESSIONS + " " +
                "WHERE " + KEY_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{currentDate});

        try {
            if (cursor.moveToFirst()) {
                float totalMinutes = cursor.getFloat(cursor.getColumnIndexOrThrow("total_minutes"));
                todayHours = totalMinutes / 60f;
                Log.d(TAG, "Today's work time: " + todayHours + " hours");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting today's work time: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return todayHours;
    }

    public int getTodaySessionCount() {
        String currentDate = getCurrentDate();
        SQLiteDatabase db = this.getReadableDatabase();
        int sessionCount = 0;

        String query = "SELECT " + KEY_SESSION_COUNT + " " +
                "FROM " + TABLE_WORK_SESSIONS + " " +
                "WHERE " + KEY_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{currentDate});

        try {
            if (cursor.moveToFirst()) {
                sessionCount = cursor.getInt(cursor.getColumnIndexOrThrow(KEY_SESSION_COUNT));
                Log.d(TAG, "Today's completed sessions: " + sessionCount);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting today's session count: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cursor.close();
            db.close();
        }

        return sessionCount;
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_WORK_SESSIONS, null, null);
            Log.d(TAG, "All work session data cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }

    public void clearTodayData() {
        String currentDate = getCurrentDate();
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            int deletedRows = db.delete(TABLE_WORK_SESSIONS, KEY_DATE + " = ?", new String[]{currentDate});
            Log.d(TAG, "Today's work session data cleared: " + deletedRows + " rows deleted for date " + currentDate);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing today's data: " + e.getMessage());
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
