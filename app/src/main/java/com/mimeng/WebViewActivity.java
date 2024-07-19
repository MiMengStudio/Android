package com.mimeng;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebViewClient;
import com.mimeng.BaseClass.BaseActivity;
import com.mimeng.user.Account;

public class WebViewActivity extends BaseActivity {
    private AgentWeb mAgentWeb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
        setContentView(R.layout.activity_web_view);

        String url = getIntent().getStringExtra("url"); // 获取传递的URL参数

        Log.d("WebViewActivity", "URL: " + url);

        View webView = findViewById(R.id.web_view);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) webView, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebViewClient(mWebViewClient)
                .createAgentWeb()
                .ready()
                .go(url); // 加载传递的URL

        // 添加 JavaScript 接口
        mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(this));

        ImageButton back = findViewById(R.id.back);
        back.setOnClickListener(view -> {
            if (mAgentWeb != null && mAgentWeb.getWebCreator().getWebView().canGoBack()) {
                mAgentWeb.getWebCreator().getWebView().goBack(); // 后退
            } else {
                finish(); // 关闭当前页面
            }
        });

        ImageButton close = findViewById(R.id.close);
        close.setOnClickListener(view -> {
            finish();
        });
    }

    private final WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 获取网页标题
            String title = view.getTitle();
            TextView titleTextView = findViewById(R.id.title);
            if (title != null) {
                titleTextView.setText(title);
                Log.d("WebViewActivity", "网页标题: " + title);
            }

            // 注册 AndroidInterface
            mAgentWeb.getJsInterfaceHolder().addJavaObject("android", new AndroidInterface(WebViewActivity.this));
        }
    };

    public class AndroidInterface {
        private final Context mContext; // 添加一个 Context 对象的引用

        public AndroidInterface(Context context) {
            mContext = context; // 在构造函数中初始化 Context 对象
        }

        @JavascriptInterface
        public void updateUserInfo(String accountInfo) {
            Log.d("WebViewActivity", "updateUserInfo called from JavaScript");
            Log.d("WebViewActivity", "AccountInfo: " + accountInfo);
            Gson gson = new Gson();
            Account account = gson.fromJson(accountInfo, Account.class);

            // 保存 Account 对象到 SharedPreferences
            Account.save(mContext, account);
            Intent intent = new Intent();
            intent.putExtra("accountInfo", "登录成功信息");
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
        }
    }
}