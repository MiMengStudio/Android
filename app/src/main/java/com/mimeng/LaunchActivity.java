package com.mimeng;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import com.mimeng.databinding.ActivityMainBinding;

public class LaunchActivity extends AppCompatActivity {
  private ActivityMainBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_launch);

    // 实现全屏显示
    View decorView = getWindow().getDecorView();
    int uiOptions =
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN;

    decorView.setSystemUiVisibility(uiOptions);
    // 状态栏颜色设置为透明
    getWindow().setStatusBarColor(Color.TRANSPARENT);

    // 适配刘海屏
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
      WindowManager.LayoutParams lp = getWindow().getAttributes();
      lp.layoutInDisplayCutoutMode =
          WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
      getWindow().setAttributes(lp);
    }

    // 监听系统UI可见性变化，确保状态栏和导航栏隐藏
    decorView.setOnSystemUiVisibilityChangeListener(
        new View.OnSystemUiVisibilityChangeListener() {
          @Override
          public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
              decorView.setSystemUiVisibility(uiOptions);
            }
          }
        });
    new Handler()
        .postDelayed(
            new Runnable() {
              @Override
              public void run() {
                // 2秒后执行的操作
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish(); // 关闭当前页面)
              }
            },
            500);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // 避免内存泄漏，确保在Activity销毁时释放binding
    this.binding = null;
  }
}
