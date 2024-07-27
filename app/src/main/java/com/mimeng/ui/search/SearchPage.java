package com.mimeng.ui.search;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.R;
import com.mimeng.utils.DataBaseHelper;
import com.mimeng.utils.DataBaseUtils;

public class SearchPage extends BaseActivity {

    @SuppressLint("Range")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        String TAG = "SearchPage";
        StringBuilder values = new StringBuilder();

        DataBaseUtils dataBaseUtils = new DataBaseUtils(SearchPage.this);
        try (Cursor cursor = dataBaseUtils.select("SELECT * FROM " + DataBaseHelper.TABLE_NAME + " LIMIT 0,10")) {
            while (cursor.moveToNext()) {
                values.append(cursor.getString(cursor.getColumnIndex("icon_name"))).append("\n");
                Log.d(TAG, "run: " + cursor.getString(cursor.getColumnIndex("icon_path")));
            }
            cursor.close();
            dataBaseUtils.closeDatabase();
        } catch (Exception e) {
            Log.e(TAG, "run: " + e);
        }

    }
}
