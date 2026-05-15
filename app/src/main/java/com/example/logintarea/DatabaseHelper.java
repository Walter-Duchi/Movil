package com.example.logintarea;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "user_app.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_DOB = "date_of_birth";
    public static final String COLUMN_GENDER = "gender";

    private static final String TABLE_USERS_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_DOB + " TEXT NOT NULL, " +
                    COLUMN_GENDER + " TEXT NOT NULL" +
                    ");";

    public static final String TABLE_ROUTINES = "routines";
    public static final String COLUMN_ROUTINE_ID = "_id";
    public static final String COLUMN_ROUTINE_NAME = "name";

    private static final String TABLE_ROUTINES_CREATE =
            "CREATE TABLE " + TABLE_ROUTINES + " (" +
                    COLUMN_ROUTINE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_ROUTINE_NAME + " TEXT NOT NULL UNIQUE" +
                    ");";

    public static final String TABLE_USER_SELECTED_ROUTINES = "user_selected_routines";
    public static final String COLUMN_USR_ID = "_id";
    public static final String COLUMN_USR_USER_ID_FK = "user_id";
    public static final String COLUMN_USR_ROUTINE_ID_FK = "routine_id";
    public static final String COLUMN_USR_IS_COMPLETED = "is_completed";

    private static final String TABLE_USER_SELECTED_ROUTINES_CREATE =
            "CREATE TABLE " + TABLE_USER_SELECTED_ROUTINES + " (" +
                    COLUMN_USR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USR_USER_ID_FK + " INTEGER NOT NULL, " +
                    COLUMN_USR_ROUTINE_ID_FK + " INTEGER NOT NULL, " +
                    COLUMN_USR_IS_COMPLETED + " INTEGER NOT NULL DEFAULT 0, " +
                    "FOREIGN KEY(" + COLUMN_USR_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + ") ON DELETE CASCADE, " +
                    "FOREIGN KEY(" + COLUMN_USR_ROUTINE_ID_FK + ") REFERENCES " + TABLE_ROUTINES + "(" + COLUMN_ROUTINE_ID + ") ON DELETE CASCADE, " +
                    "UNIQUE (" + COLUMN_USR_USER_ID_FK + ", " + COLUMN_USR_ROUTINE_ID_FK + ")" +
                    ");";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_USERS_CREATE);
        Log.i(TAG, "Table created: " + TABLE_USERS);
        db.execSQL(TABLE_ROUTINES_CREATE);
        Log.i(TAG, "Table created: " + TABLE_ROUTINES);
        db.execSQL(TABLE_USER_SELECTED_ROUTINES_CREATE);
        Log.i(TAG, "Table created: " + TABLE_USER_SELECTED_ROUTINES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SELECTED_ROUTINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
        ensureTestUser(db);
    }

    private void ensureTestUser(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{"admin"},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        if (!exists) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USERNAME, "admin");
            values.put(COLUMN_PASSWORD, "pass123");
            values.put(COLUMN_DOB, "01/01/2000");
            values.put(COLUMN_GENDER, "Otro");
            long newId = db.insert(TABLE_USERS, null, values);
            if (newId != -1) {
                Log.i(TAG, "Usuario de prueba 'admin' creado con contraseña 'pass123'.");
            } else {
                Log.e(TAG, "No se pudo crear el usuario de prueba 'admin'.");
            }
        } else {
            Log.d(TAG, "Usuario de prueba 'admin' ya existe.");
        }
    }

    public long addUser(String username, String password, String dob, String gender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_DOB, dob);
        values.put(COLUMN_GENDER, gender);
        long newRowId = db.insert(TABLE_USERS, null, values);
        db.close();
        return newRowId;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_USER_ID};
        String selection = COLUMN_USERNAME + " = ?" + " AND " + COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        boolean userExists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return userExists;
    }

    public long getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        long userId = -1;
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COLUMN_USER_ID},
                COLUMN_USERNAME + " = ?",
                new String[]{username},
                null, null, null);
        if (cursor.moveToFirst()) {
            userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }
        cursor.close();
        db.close();
        return userId;
    }

    public long addRoutine(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROUTINE_NAME, name);
        long id = db.insertWithOnConflict(TABLE_ROUTINES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return id;
    }

    public List<Routine> getAllRoutines() {
        List<Routine> routines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ROUTINES, null, null, null, null, null, COLUMN_ROUTINE_NAME + " ASC");

        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_NAME));
                routines.add(new Routine(id, name));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return routines;
    }

    public int updateRoutine(long routineId, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ROUTINE_NAME, newName);
        int rowsAffected = db.update(TABLE_ROUTINES, values, COLUMN_ROUTINE_ID + " = ?", new String[]{String.valueOf(routineId)});
        db.close();
        return rowsAffected;
    }

    public void deleteRoutine(long routineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROUTINES, COLUMN_ROUTINE_ID + " = ?", new String[]{String.valueOf(routineId)});
        db.close();
    }

    public boolean addUserSelectedRoutine(long userId, long routineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USR_USER_ID_FK, userId);
        values.put(COLUMN_USR_ROUTINE_ID_FK, routineId);
        values.put(COLUMN_USR_IS_COMPLETED, 0);
        long result = db.insertWithOnConflict(TABLE_USER_SELECTED_ROUTINES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
        return result != -1;
    }

    public List<UserSelectedRoutine> getUserSelectedRoutines(long userId) {
        List<UserSelectedRoutine> selectedRoutines = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT usr." + COLUMN_USR_ID + ", usr." + COLUMN_USR_USER_ID_FK + ", usr." + COLUMN_USR_ROUTINE_ID_FK +
                ", r." + COLUMN_ROUTINE_NAME + ", usr." + COLUMN_USR_IS_COMPLETED +
                " FROM " + TABLE_USER_SELECTED_ROUTINES + " usr" +
                " JOIN " + TABLE_ROUTINES + " r ON usr." + COLUMN_USR_ROUTINE_ID_FK + " = r." + COLUMN_ROUTINE_ID +
                " WHERE usr." + COLUMN_USR_USER_ID_FK + " = ?" +
                " ORDER BY r." + COLUMN_ROUTINE_NAME + " ASC";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor.moveToFirst()) {
            do {
                long usrId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USR_ID));
                long usrUserId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USR_USER_ID_FK));
                long usrRoutineId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_USR_ROUTINE_ID_FK));
                String routineName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROUTINE_NAME));
                boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USR_IS_COMPLETED)) == 1;
                selectedRoutines.add(new UserSelectedRoutine(usrId, usrUserId, usrRoutineId, routineName, isCompleted));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return selectedRoutines;
    }

    public void removeUserSelectedRoutine(long userId, long routineId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USER_SELECTED_ROUTINES,
                COLUMN_USR_USER_ID_FK + " = ? AND " + COLUMN_USR_ROUTINE_ID_FK + " = ?",
                new String[]{String.valueOf(userId), String.valueOf(routineId)});
        db.close();
    }

    public int updateUserRoutineCompletion(long userSelectedRoutineId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USR_IS_COMPLETED, isCompleted ? 1 : 0);
        int rowsAffected = db.update(TABLE_USER_SELECTED_ROUTINES, values,
                COLUMN_USR_ID + " = ?",
                new String[]{String.valueOf(userSelectedRoutineId)});
        db.close();
        return rowsAffected;
    }

    public void resetUserRoutineProgress(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USR_IS_COMPLETED, 0);
        db.update(TABLE_USER_SELECTED_ROUTINES, values, COLUMN_USR_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)});
        db.close();
    }
}
