package com.mimeng.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtils {
    private final static String TAG = "IOUtils";

    public static void copy(@NonNull InputStream input, @NonNull OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = input.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

    }

    /**
     * 插入数据（清空整个表）
     *
     * @param path 文件路径
     */
    public static void sqlInsert(Context context, String path) {
        new Thread(() -> {
            try (DataBaseHelper dataBaseHelper = new DataBaseHelper(context)) {
                SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
                // 清空表数据
                database.delete(DataBaseHelper.TABLE_NAME, null, null);
                // 存入表数据
                ContentValues insertData = new ContentValues();
                Log.i(TAG, "getAllResultFile: 数据库：" + path);
                insertData.put(null, path);
                database.insert(DataBaseHelper.TABLE_NAME, null, insertData);
            } catch (Exception e) {
                Log.e(TAG, "getAllResultFile: " + e);
            }
        });
    }

    public String selectData(Context context, String name) {
        String value = null;
        try (DataBaseHelper dataBaseHelper = new DataBaseHelper(context)) {
            SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
            database.execSQL("SELECT * FROM " + DataBaseHelper.TABLE_NAME + " WHERE 'icon_path' like '+" + name + "'");
        }
        return value;
    }
}
