package com.android.test.utils;

import android.util.Log;
import android.util.LruCache;

/**
 * Created by liusonghao
 * 2020.1.10
 *
 *
 */
public class HtmlCacheUtil {
    public static HtmlCacheUtil instance;
    /**
     * LruCache
     */
    private LruCache<String, String> mLruCache;


    private HtmlCacheUtil() {
        init();
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static HtmlCacheUtil getInstance() {
        if (instance == null) {
            synchronized (HtmlCacheUtil.class) {
                if (instance == null) {
                    instance = new HtmlCacheUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化LruCache
     */
    private void init() {
        if (mLruCache != null) {
            return;
        }
        int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 16);
        mLruCache = new LruCache<String, String>(cacheSize) {
            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }
        };
    }


    /**
     * 存入
     *
     * @param url
     * @param html
     */
    public void putCache(String url, String html) {
        if (mLruCache == null) {
            init();
        }
        if (getCache(url) == null) {
            mLruCache.put(url, html);
            Log.e("putCache", "putHtml================= " + "success");
        }
    }

    /**
     * 获取缓存
     *
     * @param url
     * @return
     */
    public String getCache(String url) {
        if (mLruCache == null) {
            init();
        }
        return mLruCache.get(url);
    }

}
