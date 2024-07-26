package com.mimeng.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DataBaseUtils {
    private final DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;

    public DataBaseUtils(Context context) {
        dataBaseHelper = new DataBaseHelper(context);

    }

    public void insertData(ContentValues values,String TABLE_NAME) {
        open();
        database.insert(TABLE_NAME, null, values);
        closeDatabase();
    }

    public void insertOverwriteData(ContentValues values,String TABLE_NAME){
        open();
        // 清空表数据
        database.delete(TABLE_NAME, null, null);
        // 存入表数据
        database.insert(TABLE_NAME, null, values);
        closeDatabase();
    }

    public Cursor select(String sql) {
        open();
        return database.rawQuery(sql, null);
    }

    public void open() {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        database.close();
    }

}
