package com.android.test;

import android.app.Application;
import android.content.Context;

/**
 * Created by liusonghao
 * 2018/7/25
 *
 *
 */
public class MyApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    /**
     * 获取全局上下文
     * @return
     */
    public static Context getApplication() {
        return mContext;
    }
}
