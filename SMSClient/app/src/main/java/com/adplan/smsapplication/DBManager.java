package com.adplan.smsapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 21..
 */
public class DBManager extends SQLiteOpenHelper {

    private static final String TAG = "DBManger";

    private static final String TABLE_NAME = "phones";

    private static final String COL_PHONE = "phone";

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_PHONE + " TEXT NOT NULL PRIMARY KEY );";
        db.execSQL(query);
    }

    public void insertPhone(String phone) {
        Log.d(TAG, "phone = " + phone);
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO " + TABLE_NAME + " VALUES ('" +
                phone + "');";
        db.execSQL(query);
    }

    public void removePhone(String phone) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COL_PHONE + " = '" + phone + "';";
        db.execSQL(query);
    }

    public List<String> getPhone() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(COL_PHONE)));
        }
        return list;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
