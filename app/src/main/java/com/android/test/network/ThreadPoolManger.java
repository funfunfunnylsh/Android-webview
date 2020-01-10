package com.android.test.network;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liusonghao
 * 2018/7/25
 *
 *
 */
public class ThreadPoolManger {

    /**
     * 实现一个单例模式
     */
    private volatile static ThreadPoolManger mInstance;

    /**
     * 核心线程池的数量，同时能够执行的线程数量
     */
    private int corePoolSize;
    /**
     * 最大线程池数量，表示当缓冲队列满的时候能继续容纳的等待任务的数量
     */
    private int maximumPoolSize;
    /**
     * 存活时间
     */
    private long keepAliveTime = 1;
    /**
     * 时间单位
     */
    private TimeUnit unit = TimeUnit.HOURS;

    private ThreadPoolExecutor executor;

    private List<Runnable> activeThread = new ArrayList<>();

    public static ThreadPoolManger getInstance() {
        if (mInstance == null) {
            synchronized (ThreadPoolManger.class) {
                if (mInstance == null) {
                    mInstance = new ThreadPoolManger();
                }
            }
        }
        return mInstance;
    }


    private ThreadPoolManger() {
        /**
         * 核心线程数corePoolSize
         */
        corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 1;
        //虽然maximumPoolSize
        maximumPoolSize = corePoolSize;
        executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                new LinkedBlockingQueue<Runnable>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    /**
     * 执行任务
     */
    public void execute(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        executor.execute(runnable);
    }

    /**
     * 执行任务
     */
    public void addRunnable(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        activeThread.add(runnable);
    }

    /**
     * 真正开始执行
     */
    public void run() {
        if (activeThread.size() <= 0) {
            return;
        }
        for (int i = 0; i < activeThread.size(); i++) {
            execute(activeThread.get(i));
        }
    }
}


