package com.pingmo.chengyan.activity.persion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.gyf.immersionbar.ImmersionBar;
import com.pingmo.chengyan.R;
import com.pingmo.chengyan.activity.manager.BaseActivity;

import androidx.annotation.Nullable;

public class CommonWebviewActivity extends BaseActivity implements View.OnClickListener {
    public static final String PAGE_TITLE = "page_title";
    public static final String PAGE_URL = "page_url";

    private String mTitle;
    private String mUrl;

    private WebView mWebView;
    private ProgressBar mPbWebView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_layout);
        mTitle = getIntent().getStringExtra(PAGE_TITLE);
        mUrl = getIntent().getStringExtra(PAGE_URL);
        initView();
        addActivity(this);
    }

    private void initView() {
        getTitleBar().setTitle(mTitle);
        //getTitleBar().enableBack();
        ImmersionBar.setTitleBar(this, getTitleBar().getTitle());
        mWebView = findViewById(R.id.webview);
        mPbWebView = findViewById(R.id.pb_webview);
      ImageView image_back = findViewById(R.id.back);
        image_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //按返回键操作并且能回退网页
                if (mWebView.canGoBack()) {
                    //后退
                    mWebView.goBack();
                } else {
                 finish();
                }
            }
        });


        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setBuiltInZoomControls(false);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setDomStorageEnabled(true);//开启本地DOM存储
        //自适应屏幕
        mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        mWebView.getSettings().setLoadWithOverviewMode(true);


        MyWebViewClient mMyWebViewClient = new MyWebViewClient();
        mMyWebViewClient.onPageFinished(mWebView, mUrl);
        mMyWebViewClient.shouldOverrideUrlLoading(mWebView, mUrl);
        mMyWebViewClient.onPageFinished(mWebView, mUrl);
        mWebView.setWebViewClient(mMyWebViewClient);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mPbWebView.setVisibility(View.GONE);
                } else {
                    mPbWebView.setVisibility(View.VISIBLE);
                    mPbWebView.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
//                if (!TextUtils.isEmpty(title) && !"cx".equals(title)) {
//                    getTitleBar().setTitle(title);
//                } else {
//                    getTitleBar().setTitle("帮助");
//                }
            }
        });


        mWebView.loadUrl(mUrl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    class MyWebViewClient extends WebViewClient {
        ProgressDialog progressDialog;

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {//网页页面开始加载的时候

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {//网页加载结束的时候
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) { //网页加载时的连接的网址
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(url));
                startActivity(intent);
                return true;
            } else {
                view.loadUrl(url);
            }
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeActivity(this);
    }
}
