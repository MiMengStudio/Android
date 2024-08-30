package com.mimeng.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.AgentWebUIControllerImplBase;
import com.just.agentweb.WebViewClient;
import com.mimeng.App;
import com.mimeng.ApplicationConfig;
import com.mimeng.R;
import com.mimeng.base.BaseActivity;
import com.mimeng.databinding.ActivityWebViewBinding;
import com.mimeng.user.Account;
import com.mimeng.user.AccountManager;
import com.mimeng.utils.AndroidUtils;
import com.mimeng.utils.ClipboardUtils;

public class WebViewActivity extends BaseActivity {

    /**
     * String value. Mandatory.
     **/
    public static final String OPTION_URL = "url";
    /**
     * Boolean Value. Default to true.
     */
    public static final String OPTION_SHOW_MENU = "showMenu";
    /**
     * String value. Default to null. Optional.
     */
    public static final String OPTION_EXTRA_TOAST = "toast";
    /**
     * Boolean Value. Default to true.
     */
    public static final String OPTION_SHOW_CLOSE = "showCloseBut";

    protected ActivityWebViewBinding binding;
    private String url;
    private AgentWeb mAgentWeb;

    @NonNull
    public static Intent createLoginInIntent(@NonNull Context context) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(OPTION_URL, ApplicationConfig.LOGIN_IN_URL);
        intent.putExtra(OPTION_SHOW_MENU, false);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        blackParentBar();
        setFullScreen(false);
        this.binding = ActivityWebViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        this.url = getIntent().getStringExtra(OPTION_URL); // 获取传递的URL参数

        String extraToast;
        if (!TextUtils.isEmpty(extraToast = getIntent().getStringExtra(OPTION_EXTRA_TOAST))) {
            Toast.makeText(this, extraToast, Toast.LENGTH_SHORT).show();
        }

        resetLayoutTopMargin(binding.webActivityToolbar, 3);
        setSupportActionBar(binding.webActivityToolbar);

        mAgentWeb = onCreateAgentWeb(binding.webView, url);

        binding.back.setOnClickListener(view -> onBackPressed());

        if (!getIntent().getBooleanExtra(OPTION_SHOW_CLOSE, true))
            binding.close.setVisibility(View.GONE);
        binding.close.setOnClickListener(view -> finish());
    }

    @Override
    public void onBackPressed() {
        if (mAgentWeb != null && !mAgentWeb.back()) {
            finish(); // 关闭当前页面
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 获取是否显示菜单参数
        if (getIntent().getBooleanExtra(OPTION_SHOW_MENU, true)) {
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

    @Override
    protected void onResume() {
        super.onResume();
        mAgentWeb.getWebLifeCycle().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAgentWeb.getWebLifeCycle().onPause();
    }

    protected WebViewClient onCreateWebViewClient() {
        return new WebActivityWebViewClient();
    }

    protected AgentWeb onCreateAgentWeb(ViewGroup parent, String url) {
        AgentWeb result = AgentWeb.with(this)
                .setAgentWebParent(parent, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .setWebViewClient(onCreateWebViewClient())
                .setAgentWebUIController(new AgentWebUIControllerImplBase())
                .addJavascriptInterface("android", new AndroidInterface())
                .createAgentWeb()
                .ready()
                .go(url); // 加载传递的URL
        result.getAgentWebSettings().getWebSettings().setUserAgentString("MiMengAndroidAPP");
        return result;
    }

    @SuppressWarnings("unused")
    protected class AndroidInterface {
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

    protected class WebActivityWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 获取网页标题
            String title = view.getTitle();
            if (title != null) {
                binding.title.setText(title);
                Log.d("WebViewActivity", "网页标题: " + title);
            }
        }
    }
}