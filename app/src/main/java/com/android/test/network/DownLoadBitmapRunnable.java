package com.android.test.network;

import android.graphics.BitmapFactory;
import android.util.Log;

import com.android.test.utils.BitmapCacheUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class DownLoadBitmapRunnable implements Runnable {
    private static final String TAG = "DownLoadBitmapRunnable";
    private String path;

    public DownLoadBitmapRunnable(String path) {
        this.path = path;

    }

    @Override
    public void run() {
        try {
            URL url = new URL(path);
            //https
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.connect();
            if (connection.getResponseCode() == 200) {
                InputStream inputStream = connection.getInputStream();
                Log.e(TAG, "下载图片成功  url==" + path);
                BitmapCacheUtil.getInstance().putCache(path, BitmapFactory.decodeStream(inputStream));
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "下载图片失败  url==" + path);
        }
    }
}
