package com.mimeng.activity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import com.mimeng.base.BaseActivity;

import java.io.IOException;

public class ArticlePreviewActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webview = new WebView(this);
        webview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setFullScreen(true);

        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("UTF-8");
        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.i("WebView", "WebView start page " + url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("WebView", "Recv Error: " + error.getDescription());
            }

            @Nullable
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                Log.i("WebView", "Intercept Request " + request.getUrl());

                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme())) {
                    if ("dist".equals(uri.getPathSegments().get(0))) {
                        String mimeType = MimeTypeMap.getSingleton()
                                .getMimeTypeFromExtension(MimeTypeMap
                                        .getFileExtensionFromUrl(uri.toString()));
                        try {
                            Log.i("WebView", "Redirecting resource " + uri.getPath());
                            return new WebResourceResponse(mimeType, "UTF-8", 200,
                                    "OK", null,
                                    getAssets().open("article" + uri.getPath()));
                        } catch (IOException e) {
                            Log.e("WebView", "Error when opening file " + uri, e);
                        }
                    }
                }

                return null;
            }
        });
        webview.loadUrl("file:///android_asset/article/index.html?id=1");

        setContentView(webview);
    }
}
