package com.mimeng.ui.search;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.R;
import com.mimeng.utils.DataBaseHelper;
import com.mimeng.utils.DataBaseUtils;

public class SearchPage extends BaseActivity {
    private final String TAG = "SearchPage";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        DataBaseUtils dataBaseUtils = new DataBaseUtils(SearchPage.this);
        try (Cursor cursor = dataBaseUtils.select("SELECT * FROM " + DataBaseHelper.TABLE_NAME)) {
            while (cursor.moveToNext()) {
                Log.d(TAG, "run: " + cursor.getColumnIndexOrThrow("icon_path"));
            }
            cursor.close();
            dataBaseUtils.closeDatabase();
        } catch (Exception e) {
            Log.e(TAG, "run: " + e);
        }

    }
}
