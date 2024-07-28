package com.mimeng.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseUtils {
    private final String TAG = "DataBaseUtils";
    private final DataBaseHelper dataBaseHelper;
    private SQLiteDatabase database;

    public DataBaseUtils(Context context) {
        dataBaseHelper = new DataBaseHelper(context);

    }

    /**
     * 写入数据
     *
     * @param values     内容
     * @param TABLE_NAME 表名
     */
    public void insertData(ContentValues values, String TABLE_NAME) {
        Log.d(TAG, "insertData(写入数据): " + values.toString());
        database.insert(TABLE_NAME, null, values);
    }

    /**
     * 查询数据
     *
     * @param sql 数据库语句
     * @return 返回查询的内容
     */
    public Cursor select(String sql) {
        open();
        return database.rawQuery(sql, null);
    }

    /**
     * 清空表数据
     *
     * @param TABLE_NAME 表名
     */
    public void clearTableData(String TABLE_NAME) {
        database.delete(TABLE_NAME, null, null);
    }

    public void open() {
        database = dataBaseHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        database.close();
    }

}
