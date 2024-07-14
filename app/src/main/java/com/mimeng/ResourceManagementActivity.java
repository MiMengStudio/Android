package com.mimeng;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


import com.mimeng.R;
import com.mimeng.BaseClass.BaseActivity;

public class ResourceManagementActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
        setContentView(R.layout.activity_resource_management);

        ImageButton backButton = findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 当按钮被点击时，关闭当前Activity
                finish();
            }
        });

    }
}
