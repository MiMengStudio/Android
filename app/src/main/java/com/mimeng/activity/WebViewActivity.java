package com.mimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebUIControllerImplBase;
import com.just.agentweb.WebViewClient;
import com.mimeng.App;
import com.mimeng.ApplicationConfig;
import com.mimeng.R;
import com.mimeng.base.BaseActivity;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.mimeng.utils.AndroidUtils;
import com.mimeng.utils.ClipboardUtils;

public class WebViewActivity extends BaseActivity {
    private AgentWeb mAgentWeb;
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
        }
    };
    private String url;

    @NonNull
    public static Intent createLoginInIntent(@NonNull Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", ApplicationConfig.LOGIN_IN_URL);
        intent.putExtra("showMenu", false);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
        setContentView(R.layout.activity_web_view);

        this.url = getIntent().getStringExtra("url"); // 获取传递的URL参数

        String extraToast;
        if ((extraToast = getIntent().getStringExtra("toast")) != null) {
            Toast.makeText(this, extraToast, Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = findViewById(R.id.web_activity_toolbar);
        resetLayoutTopMargin(toolbar, 3);
        setSupportActionBar(toolbar);

        View webView = findViewById(R.id.web_view);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent((LinearLayout) webView, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebViewClient(mWebViewClient)
                .setAgentWebUIController(new AgentWebUIControllerImplBase())
                .addJavascriptInterface("android", new AndroidInterface())
                .createAgentWeb()
                .ready()
                .go(this.url); // 加载传递的URL

        findViewById(R.id.back).setOnClickListener(view -> onBackPressed());

        ImageView close = findViewById(R.id.close);
        if (!getIntent().getBooleanExtra("showCloseBut", true)) close.setVisibility(View.GONE);
        close.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        if (mAgentWeb != null && mAgentWeb.getWebCreator().getWebView().canGoBack()) {
            mAgentWeb.getWebCreator().getWebView().goBack(); // 后退
        } else {
            finish(); // 关闭当前页面
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 获取是否显示菜单参数
        if (getIntent().getBooleanExtra("showMenu", true)) {
            getMenuInflater().inflate(R.menu.webview_menu, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.copy_link) {
            ClipboardUtils.copyToClipboard(this, this.url);
            Toast.makeText(this, getString(R.string.msg_copied), Toast.LENGTH_SHORT).show();
            return true;
        } else if (item.getItemId() == R.id.open_in_browser) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            // 在返回应用后关闭Activity
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.destroy();
        }
    }

    @SuppressWarnings("unused")
    public class AndroidInterface {
        @JavascriptInterface
        public void updateUserInfo(String accountInfo) {
            Log.d("WebViewActivity", "updateUserInfo called from JavaScript");
            Log.d("WebViewActivity", "AccountInfo: " + accountInfo);
            Account account = App.GSON.fromJson(accountInfo, Account.class);

            // 保存 Account 对象到 SharedPreferences
            AccountManager.save(WebViewActivity.this, account);
            setResult(Activity.RESULT_OK, null);
            finish();
        }

        @JavascriptInterface
        public void share(String text) {
            AndroidUtils.shareText(WebViewActivity.this, text);
        }
    }
}