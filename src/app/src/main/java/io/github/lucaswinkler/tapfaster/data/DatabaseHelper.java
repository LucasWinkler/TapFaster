package io.github.lucaswinkler.tapfaster.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.github.lucaswinkler.tapfaster.data.models.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TapFaster.db";

    private static final String TABLE_USER = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_BEST_TIME = "best_time";
    private static final String COLUMN_USER_PASSWORD = "password";

    private SQLiteDatabase db;
    private void openReadableDb() {
        db = this.getReadableDatabase();
    }
    private void openWriteableDb() {
        db = this.getWritableDatabase();
    }
    private void closeDb() {
        if (db != null) {
            db.close();
        }
    }
    private void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USER + "("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT UNIQUE,"
                + COLUMN_USER_BEST_TIME + " TEXT DEFAULT '-1'," + COLUMN_USER_PASSWORD + " TEXT" + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        Log.d("Task list", "Upgrading db from version " + oldVersion + " to " + newVersion);
        onCreate(db);
    }

    public void addUser(String username, String password) {
        openWriteableDb();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, username);
        values.put(COLUMN_USER_PASSWORD, password);

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public User getUser(String username) {
        User user = null;

        openReadableDb();
        Cursor cursor = db.rawQuery("SELECT id, name, best_time FROM users WHERE name = ?", new String[]{username});
        if (cursor.moveToFirst()) {
            user = new User(
                    Long.toString(cursor.getLong(cursor.getColumnIndex(COLUMN_USER_ID))),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_USER_BEST_TIME)));
        }

        closeCursor(cursor);
        closeDb();

        return user;
    }

    public List<User> getUsers() {
        String[] columns = {
                COLUMN_USER_ID,
                COLUMN_USER_NAME,
                COLUMN_USER_BEST_TIME
        };

        String sortOrder = COLUMN_USER_BEST_TIME + " ASC";
        List<User> userList = new ArrayList<User>();

        openReadableDb();

        Cursor cursor = db.query(TABLE_USER, columns, null, null, null, null, sortOrder);
        if (cursor.moveToFirst()) {
            do {
                userList.add(new User(
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_ID)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_NAME)),
                        cursor.getString(cursor.getColumnIndex(COLUMN_USER_BEST_TIME))));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return userList;
    }

    public void updateUser(User user) {
        openWriteableDb();

        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, user.getUsername());
        values.put(COLUMN_USER_BEST_TIME, user.getBestTime());

        db.update(TABLE_USER, values, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
        closeDb();
    }

    public void deleteUser(User user) {
        openWriteableDb();

        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?", new String[]{String.valueOf(user.getId())});
        closeDb();
    }

    public boolean checkUserExists(String username) {
        String[] columns = {
                COLUMN_USER_ID
        };
        openReadableDb();

        String selection = COLUMN_USER_NAME + " = ?";
        String[] selectionArgs = {username};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();

        closeCursor(cursor);
        closeDb();

        return cursorCount > 0;
    }

    public boolean isValidLogin(String username, String password) {
        String[] columns = {
                COLUMN_USER_ID
        };
        openReadableDb();

        String selection = COLUMN_USER_NAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();

        closeCursor(cursor);
        closeDb();

        return cursorCount > 0;
    }
}
