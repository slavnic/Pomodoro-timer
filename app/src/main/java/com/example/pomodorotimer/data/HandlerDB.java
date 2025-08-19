package com.example.pomodorotimer.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HandlerDB extends SQLiteOpenHelper {

    private static final String TAG = "HandlerDB";
    private static final String DATABASE_NAME = "work_timer.db";
    private static final int DATABASE_VERSION = 5; // Increment version to add TODO table

    // Work sessions table
    private static final String TABLE_WORK_SESSIONS = "work_sessions";
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_WORK_TIME_MINUTES = "work_time_minutes";
    private static final String KEY_SESSION_COUNT = "session_count";
    private static final String KEY_BREAK_TIME_MINUTES = "break_time_minutes";

    // Break sessions table
    private static final String TABLE_BREAK_SESSIONS = "break_sessions";
    private static final String KEY_BREAK_ID = "id";
    private static final String KEY_BREAK_DATE = "date";
    private static final String KEY_BREAK_TIME_MINUTES_SEPARATE = "break_time_minutes";

    // Long break sessions table
    private static final String TABLE_LONG_BREAK_SESSIONS = "long_break_sessions";
    private static final String KEY_LONG_BREAK_ID = "id";
    private static final String KEY_LONG_BREAK_DATE = "date";
    private static final String KEY_LONG_BREAK_TIME_MINUTES = "long_break_time_minutes";

    // TODO table
    private static final String TABLE_TODOS = "todos";
    private static final String KEY_TODO_ID = "id";
    private static final String KEY_TODO_TITLE = "title";
    private static final String KEY_TODO_DESCRIPTION = "description";
    private static final String KEY_TODO_IS_COMPLETED = "is_completed";
    private static final String KEY_TODO_DATE_CREATED = "date_created";


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
        // Create work sessions table
        String CREATE_WORK_SESSIONS_TABLE = "CREATE TABLE " + TABLE_WORK_SESSIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_DATE + " TEXT NOT NULL,"
                + KEY_WORK_TIME_MINUTES + " INTEGER NOT NULL,"
                + KEY_SESSION_COUNT + " INTEGER DEFAULT 1,"
                + KEY_BREAK_TIME_MINUTES + " INTEGER DEFAULT 0"
                + ")";

        // Create break sessions table
        String CREATE_BREAK_SESSIONS_TABLE = "CREATE TABLE " + TABLE_BREAK_SESSIONS + "("
                + KEY_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_BREAK_TIME_MINUTES_SEPARATE + " INTEGER NOT NULL,"
                + KEY_BREAK_DATE + " TEXT NOT NULL"
                + ")";

        // Create long break sessions table
        String CREATE_LONG_BREAK_SESSIONS_TABLE = "CREATE TABLE " + TABLE_LONG_BREAK_SESSIONS + "("
                + KEY_LONG_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_LONG_BREAK_TIME_MINUTES + " INTEGER NOT NULL,"
                + KEY_LONG_BREAK_DATE + " TEXT NOT NULL"
                + ")";

        // Create TODOs table
        String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS + "("
                + KEY_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TODO_TITLE + " TEXT NOT NULL,"
                + KEY_TODO_DESCRIPTION + " TEXT,"
                + KEY_TODO_IS_COMPLETED + " INTEGER DEFAULT 0,"
                + KEY_TODO_DATE_CREATED + " TEXT NOT NULL"
                + ")";

        db.execSQL(CREATE_WORK_SESSIONS_TABLE);
        db.execSQL(CREATE_BREAK_SESSIONS_TABLE);
        db.execSQL(CREATE_LONG_BREAK_SESSIONS_TABLE);
        db.execSQL(CREATE_TODOS_TABLE);
        Log.d(TAG, "All database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_WORK_SESSIONS + " ADD COLUMN " + KEY_SESSION_COUNT + " INTEGER DEFAULT 1");
                Log.d(TAG, "Added session_count column to existing table");
            } catch (Exception e) {
                Log.e(TAG, "Error adding session_count column: " + e.getMessage());
            }
        }
        if (oldVersion < 3) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_WORK_SESSIONS + " ADD COLUMN " + KEY_BREAK_TIME_MINUTES + " INTEGER DEFAULT 0");
                Log.d(TAG, "Added break_time_minutes column to existing table");
            } catch (Exception e) {
                Log.e(TAG, "Error adding break_time_minutes column: " + e.getMessage());
            }
        }
        if (oldVersion < 4) {
            try {
                String CREATE_BREAK_SESSIONS_TABLE = "CREATE TABLE " + TABLE_BREAK_SESSIONS + "("
                        + KEY_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + KEY_BREAK_TIME_MINUTES_SEPARATE + " INTEGER NOT NULL,"
                        + KEY_BREAK_DATE + " TEXT NOT NULL"
                        + ")";

                String CREATE_LONG_BREAK_SESSIONS_TABLE = "CREATE TABLE " + TABLE_LONG_BREAK_SESSIONS + "("
                        + KEY_LONG_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + KEY_LONG_BREAK_TIME_MINUTES + " INTEGER NOT NULL,"
                        + KEY_LONG_BREAK_DATE + " TEXT NOT NULL"
                        + ")";

                db.execSQL(CREATE_BREAK_SESSIONS_TABLE);
                db.execSQL(CREATE_LONG_BREAK_SESSIONS_TABLE);
                Log.d(TAG, "Created break and long break tables");
            } catch (Exception e) {
                Log.e(TAG, "Error creating break tables: " + e.getMessage());
            }
        }
        if (oldVersion < 5) {
            try {
                String CREATE_TODOS_TABLE = "CREATE TABLE " + TABLE_TODOS + "("
                        + KEY_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + KEY_TODO_TITLE + " TEXT NOT NULL,"
                        + KEY_TODO_DESCRIPTION + " TEXT,"
                        + KEY_TODO_IS_COMPLETED + " INTEGER DEFAULT 0,"
                        + KEY_TODO_DATE_CREATED + " TEXT NOT NULL"
                        + ")";

                db.execSQL(CREATE_TODOS_TABLE);
                Log.d(TAG, "Created TODOs table");
            } catch (Exception e) {
                Log.e(TAG, "Error creating TODOs table: " + e.getMessage());
            }
        }
    }

    public void saveWorkSession(long workMinutes) throws Exception {
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, getCurrentDate());
            values.put(KEY_WORK_TIME_MINUTES, workMinutes);
            values.put(KEY_SESSION_COUNT, 1); // Jedna work sesija
            values.put(KEY_BREAK_TIME_MINUTES, 0); // NIKAD ne čuvaj break time ovde

            long result = database.insert(TABLE_WORK_SESSIONS, null, values);
            if (result != -1) {
                Log.d(TAG, "Work session saved ONLY: " + workMinutes + " minutes");
            } else {
                Log.e(TAG, "Failed to save work session");
            }
            database.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving work session", e);
            throw e;
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

    public int getTotalBreakMinutesToday() {
        int totalBreakMinutes = 0;
        String currentDate = getCurrentDate();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT SUM(" + KEY_BREAK_TIME_MINUTES + ") FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{currentDate});

            if (cursor.moveToFirst()) {
                totalBreakMinutes = cursor.getInt(0);
            }
            cursor.close();
            db.close();

            Log.d(TAG, "Total break minutes today: " + totalBreakMinutes);
        } catch (Exception e) {
            Log.e(TAG, "Error getting total break minutes", e);
        }

        return totalBreakMinutes;
    }

    public int getTotalSessionsToday() {
        int totalSessions = 0;
        String currentDate = getCurrentDate();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // Count all rows for today (each row represents one completed session)
            String query = "SELECT COUNT(*) FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{currentDate});

            if (cursor.moveToFirst()) {
                totalSessions = cursor.getInt(0);
            }
            cursor.close();
            db.close();

            Log.d(TAG, "Today's completed sessions: " + totalSessions);
        } catch (Exception e) {
            Log.e(TAG, "Error getting total sessions", e);
        }

        return totalSessions;
    }

    public Map<String, Integer> getMonthlyWorkStatistics(int year, int month) {
        Map<String, Integer> stats = new HashMap<>();
        int totalWorkMinutes = 0;
        int totalSessions = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Format year-month for query
            String yearMonth = String.format("%04d-%02d", year, month + 1); // month+1 jer Calendar.MONTH počinje od 0

            String query = "SELECT SUM(" + KEY_WORK_TIME_MINUTES + "), SUM(" + KEY_SESSION_COUNT + ") FROM " +
                    TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " LIKE ?";

            Cursor cursor = db.rawQuery(query, new String[]{yearMonth + "%"});

            if (cursor.moveToFirst()) {
                totalWorkMinutes = cursor.getInt(0);
                totalSessions = cursor.getInt(1);
            }

            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly work statistics", e);
        }

        stats.put("workTime", totalWorkMinutes);
        stats.put("sessions", totalSessions);
        return stats;
    }

    public int getMonthlyBreakMinutes(int year, int month) {
        int totalBreakMinutes = 0;

        try {
            SQLiteDatabase db = this.getReadableDatabase();

            // Format year-month for query
            String yearMonth = String.format("%04d-%02d", year, month + 1);

            // Čitaj break minute iz glavne tabele
            String query = "SELECT SUM(" + KEY_BREAK_TIME_MINUTES + ") FROM " +
                    TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " LIKE ?";
            Cursor cursor = db.rawQuery(query, new String[]{yearMonth + "%"});

            if (cursor.moveToFirst()) {
                totalBreakMinutes = cursor.getInt(0);
            }
            cursor.close();
            db.close();

            Log.d(TAG, "Monthly break minutes for " + yearMonth + ": " + totalBreakMinutes);
        } catch (Exception e) {
            Log.e(TAG, "Error getting monthly break minutes", e);
        }

        return totalBreakMinutes;
    }

    public int getTotalWorkMinutesForDate(Date date) {
        int totalWorkMinutes = 0;
        String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(date);

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT SUM(" + KEY_WORK_TIME_MINUTES + ") FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{dateString});

            if (cursor.moveToFirst()) {
                totalWorkMinutes = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting work minutes for date", e);
        }

        return totalWorkMinutes;
    }

    public int getTotalBreakMinutesForDate(Date date) {
        int totalBreakMinutes = 0;
        String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(date);

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            // Čitaj break minute iz glavne tabele gde se čuvaju kompletne sesije
            String query = "SELECT SUM(" + KEY_BREAK_TIME_MINUTES + ") FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{dateString});

            if (cursor.moveToFirst()) {
                totalBreakMinutes = cursor.getInt(0);
            }
            cursor.close();
            db.close();

            Log.d(TAG, "Total break minutes for date " + dateString + ": " + totalBreakMinutes);
        } catch (Exception e) {
            Log.e(TAG, "Error getting break minutes for date", e);
        }

        return totalBreakMinutes;
    }

    public int getTotalWorkMinutesToday() {
        int totalWorkMinutes = 0;
        String currentDate = getCurrentDate();

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT SUM(" + KEY_WORK_TIME_MINUTES + ") FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{currentDate});

            if (cursor.moveToFirst()) {
                totalWorkMinutes = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting total work minutes", e);
        }

        return totalWorkMinutes;
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

    public int getTotalMinutesForDate(Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date);

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT SUM(" + KEY_WORK_TIME_MINUTES + ") FROM " + TABLE_WORK_SESSIONS +
                " WHERE " + KEY_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{dateString});

        int totalMinutes = 0;
        if (cursor.moveToFirst()) {
            totalMinutes = cursor.getInt(0);
        }

        cursor.close();
        return totalMinutes;
    }

    public int getTotalBreakTimeForDate(Date date) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date);

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT SUM(" + KEY_BREAK_TIME_MINUTES + ") FROM " + TABLE_WORK_SESSIONS +
                " WHERE " + KEY_DATE + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{dateString});
        int totalBreakTime = 0;
        if (cursor.moveToFirst()) {
            totalBreakTime = cursor.getInt(0);
        }
        cursor.close();
        return totalBreakTime;
    }

    public Map<String, Integer> getMonthlyStatistics(int year, int month) throws Exception {
        SQLiteDatabase db = getReadableDatabase();

        // Format month to match your date format (month is 0-based in Calendar)
        String monthStr = String.format("%04d-%02d", year, month + 1);

        String query = "SELECT SUM(" + KEY_WORK_TIME_MINUTES + "), SUM(" + KEY_BREAK_TIME_MINUTES + "), SUM(" + KEY_SESSION_COUNT + ") " +
                "FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " LIKE ?";

        Cursor cursor = db.rawQuery(query, new String[]{monthStr + "%"});

        Map<String, Integer> stats = new HashMap<>();
        if (cursor.moveToFirst()) {
            stats.put("workTime", cursor.getInt(0));
            stats.put("breakTime", cursor.getInt(1));
            stats.put("sessions", cursor.getInt(2));
        } else {
            stats.put("workTime", 0);
            stats.put("breakTime", 0);
            stats.put("sessions", 0);
        }

        cursor.close();
        return stats;
    }

    // Method to save break time (call this when break session ends)
    public void saveBreakTime(Date date, int breakMinutes) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = sdf.format(date);

        SQLiteDatabase db = getWritableDatabase();

        // Check if record exists for this date
        Cursor cursor = db.query(TABLE_WORK_SESSIONS, null, KEY_DATE + "=?",
                new String[]{dateString}, null, null, null);

        if (cursor.moveToFirst()) {
            // Update existing record - use getColumnIndexOrThrow instead
            int breakTimeColumnIndex = cursor.getColumnIndex(KEY_BREAK_TIME_MINUTES);
            int currentBreakTime = 0;
            if (breakTimeColumnIndex != -1) {
                currentBreakTime = cursor.getInt(breakTimeColumnIndex);
            }
            ContentValues values = new ContentValues();
            values.put(KEY_BREAK_TIME_MINUTES, currentBreakTime + breakMinutes);
            db.update(TABLE_WORK_SESSIONS, values, KEY_DATE + "=?", new String[]{dateString});
        } else {
            // Create new record
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, dateString);
            values.put(KEY_WORK_TIME_MINUTES, 0);
            values.put(KEY_BREAK_TIME_MINUTES, breakMinutes);
            values.put(KEY_SESSION_COUNT, 0);
            db.insert(TABLE_WORK_SESSIONS, null, values);
        }

        cursor.close();
    }

    public int getSessionCountForDate(Date date) {
        int sessionCount = 0;
        String dateString = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(date);

        try {
            SQLiteDatabase db = this.getReadableDatabase();
            String query = "SELECT SUM(" + KEY_SESSION_COUNT + ") FROM " + TABLE_WORK_SESSIONS + " WHERE " + KEY_DATE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{dateString});

            if (cursor.moveToFirst()) {
                sessionCount = cursor.getInt(0);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting session count for date", e);
        }

        return sessionCount;
    }

    public void saveCompleteSession(long workMinutes, long breakMinutes) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_DATE, getCurrentDate());
            values.put(KEY_WORK_TIME_MINUTES, workMinutes);
            values.put(KEY_BREAK_TIME_MINUTES, breakMinutes);
            values.put(KEY_SESSION_COUNT, 1); // Jedna kompletna sesija

            long result = db.insert(TABLE_WORK_SESSIONS, null, values);
            if (result != -1) {
                Log.d(TAG, "Complete session saved: Work=" + workMinutes + "min, Break=" + breakMinutes + "min");
            } else {
                Log.e(TAG, "Failed to save complete session");
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving complete session", e);
            e.printStackTrace();
        }
    }

    public void saveBreakSession(long breakTimeInMinutes) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_BREAK_TIME_MINUTES_SEPARATE, breakTimeInMinutes);
            values.put(KEY_BREAK_DATE, getCurrentDate());

            long result = db.insert(TABLE_BREAK_SESSIONS, null, values);
            if (result != -1) {
                Log.d(TAG, "Break session saved successfully: " + breakTimeInMinutes + " minutes");
            } else {
                Log.e(TAG, "Failed to save break session");
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving break session", e);
            e.printStackTrace();
        }
    }

    public void saveLongBreakSession(long longBreakTimeInMinutes) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_LONG_BREAK_TIME_MINUTES, longBreakTimeInMinutes);
            values.put(KEY_LONG_BREAK_DATE, getCurrentDate());

            long result = db.insert(TABLE_LONG_BREAK_SESSIONS, null, values);
            if (result != -1) {
                Log.d(TAG, "Long break session saved successfully: " + longBreakTimeInMinutes + " minutes");
            } else {
                Log.e(TAG, "Failed to save long break session");
            }
            db.close();
        } catch (Exception e) {
            Log.e(TAG, "Error saving long break session", e);
            e.printStackTrace();
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

    // TODO CRUD operations
    public long addTodo(com.example.pomodorotimer.model.Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TODO_TITLE, todo.getTitle());
        values.put(KEY_TODO_DESCRIPTION, todo.getDescription());
        values.put(KEY_TODO_IS_COMPLETED, todo.isCompleted() ? 1 : 0);
        values.put(KEY_TODO_DATE_CREATED, getCurrentDate());

        long result = db.insert(TABLE_TODOS, null, values);
        db.close();

        Log.d(TAG, "TODO added with ID: " + result);
        return result;
    }

    public java.util.List<com.example.pomodorotimer.model.Todo> getAllTodos() {
        java.util.List<com.example.pomodorotimer.model.Todo> todoList = new java.util.ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TODOS + " ORDER BY " + KEY_TODO_DATE_CREATED + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                com.example.pomodorotimer.model.Todo todo = new com.example.pomodorotimer.model.Todo();
                todo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TODO_ID)));
                todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TODO_TITLE)));
                todo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TODO_DESCRIPTION)));
                todo.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TODO_IS_COMPLETED)) == 1);
                todo.setDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TODO_DATE_CREATED)));
                todoList.add(todo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return todoList;
    }

    public int updateTodo(com.example.pomodorotimer.model.Todo todo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TODO_TITLE, todo.getTitle());
        values.put(KEY_TODO_DESCRIPTION, todo.getDescription());
        values.put(KEY_TODO_IS_COMPLETED, todo.isCompleted() ? 1 : 0);

        int result = db.update(TABLE_TODOS, values, KEY_TODO_ID + " = ?",
                new String[]{String.valueOf(todo.getId())});
        db.close();

        Log.d(TAG, "TODO updated: " + result + " rows affected");
        return result;
    }

    public void deleteTodo(long todoId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TODOS, KEY_TODO_ID + " = ?",
                new String[]{String.valueOf(todoId)});
        db.close();

        Log.d(TAG, "TODO deleted: " + result + " rows affected");
    }

    public com.example.pomodorotimer.model.Todo getTodo(long todoId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODOS, new String[]{KEY_TODO_ID, KEY_TODO_TITLE,
                        KEY_TODO_DESCRIPTION, KEY_TODO_IS_COMPLETED, KEY_TODO_DATE_CREATED}, KEY_TODO_ID + "=?",
                new String[]{String.valueOf(todoId)}, null, null, null, null);

        com.example.pomodorotimer.model.Todo todo = null;
        if (cursor != null && cursor.moveToFirst()) {
            todo = new com.example.pomodorotimer.model.Todo();
            todo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(KEY_TODO_ID)));
            todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TODO_TITLE)));
            todo.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TODO_DESCRIPTION)));
            todo.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_TODO_IS_COMPLETED)) == 1);
            todo.setDateCreated(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TODO_DATE_CREATED)));
            cursor.close();
        }
        db.close();
        return todo;
    }
}
