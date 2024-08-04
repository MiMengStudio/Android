package com.mimeng.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

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
    public ArrayList<String> select(String sql,String columnName) {
        open();
        Cursor cursor = database.rawQuery(sql,null);
        ArrayList<String> values = new ArrayList<>();
        while (cursor.moveToNext()){
            values.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
        }
        return values;
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
