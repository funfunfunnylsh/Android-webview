package com.android.test;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.test.network.DownLoadBitmapRunnable;
import com.android.test.network.ThreadPoolManger;
import com.android.test.utils.BitmapCacheUtil;
import com.android.test.utils.HtmlCacheUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class WebViewActivity extends AppCompatActivity {
    public static final String GET = "GET";
    private static final String TAG = "WebViewActivity";
    public static final String PNG = ".png";
    public static final String JPG = ".jpg";
    private String encoding = "utf-8";
    private WebView mWebView;
    private FrameLayout container;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        url = getIntent().getStringExtra(UrlListActivity.URL_);
        initView();
    }

    private void initView() {
        container = findViewById(R.id.container);
        mWebView = new WebView(MyApplication.mContext);
        //配置WebView
        webViewConfig();

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
        );
        container.addView(mWebView, layoutParams);
        if (url != null) {
            String htmlFromCache = HtmlCacheUtil.getInstance().getCache(url);
            if (htmlFromCache != null) {
                Log.e(TAG, "从缓存中加载Html");
                //html: mimeType = "text/html";
                mWebView.loadDataWithBaseURL(url, htmlFromCache, "text/html", encoding, null);
            } else {
                mWebView.loadUrl(url);
            }
        }
    }

    /**
     * webView
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void webViewConfig() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
    }


    /**
     * WebViewClient
     */
    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (!GET.equals(request.getMethod())) {
                return super.shouldInterceptRequest(view, request);
            }
            String url = request.getUrl().toString();
            if (url.endsWith(PNG) || url.endsWith(JPG)) {
                Log.e(TAG, "url====" + url);
                Bitmap bitmap = BitmapCacheUtil.getInstance().getCache(url);
                if (bitmap != null) {
                    //jpg/png:  mimeType = "image/png";
                    String mimeType = "image/png";
                    //bitmap 转换成 inputStream
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
                    Log.e(TAG, "加载缓存中的图片");
                    WebResourceResponse response = new WebResourceResponse(mimeType, encoding, inputStream);
                    try {
                        //关闭流
                        baos.close();
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return response;
                } else {
                    //需要下载
                    ThreadPoolManger.getInstance().execute(new DownLoadBitmapRunnable(url));
                }
                return super.shouldInterceptRequest(view, request);
            } else {
                return super.shouldInterceptRequest(view, request);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        container.removeView(mWebView);
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView = null;
        }
    }
}
