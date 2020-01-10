package com.android.test.callback;

/**
 * Created by liusonghao
 * 2018/7/25
 */
public interface HtmlCallBack {
    void success(String html, String url);

    void onFail(Exception e);
}
