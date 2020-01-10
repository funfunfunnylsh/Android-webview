package com.android.test.utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by liusonghao
 * 2020.1.10
 *
 *
 */
public class BitmapCacheUtil {
    public static BitmapCacheUtil instance;
    /**
     * 图片的缓存
     */
    private LruCache<String, Bitmap> mBitmapLruCache;

    private BitmapCacheUtil() {
        init();
    }

    /**
     * 获取缓存
     */
    private void init() {
        if (mBitmapLruCache != null) {
            return;
        }
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 16);
        mBitmapLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static BitmapCacheUtil getInstance() {
        if (instance == null) {
            synchronized (BitmapCacheUtil.class) {
                if (instance == null) {
                    instance = new BitmapCacheUtil();
                }
            }
        }
        return instance;
    }


    /**
     * 存入bitmap
     *
     * @param url
     * @param bitmap
     */
    public void putCache(String url, Bitmap bitmap) {
        if (mBitmapLruCache == null) {
            init();
        }
        if (getCache(url) == null) {
            mBitmapLruCache.put(url, bitmap);
            Log.e("putCache", "putBitmap================= " + "success");
        }
    }

    /**
     * 获取bitmap
     *
     * @param url
     * @return
     */
    public Bitmap getCache(String url) {
        if (mBitmapLruCache == null) {
            init();
        }
        return mBitmapLruCache.get(url);
    }
}
