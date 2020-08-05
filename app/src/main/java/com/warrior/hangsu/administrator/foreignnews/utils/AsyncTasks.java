package com.warrior.hangsu.administrator.foreignnews.utils;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by acorn on 2020/8/5.
 */
public class AsyncTasks {
    private static Executor sExecutor;

    public AsyncTasks() {
    }

    @TargetApi(11)
    private static void init() {
        if (Build.VERSION.SDK_INT >= 11) {
            sExecutor = AsyncTask.THREAD_POOL_EXECUTOR;
        } else {
            sExecutor = Executors.newSingleThreadExecutor();
        }

    }

    public static void setExecutor(Executor var0) {
        sExecutor = var0;
    }

    @TargetApi(11)
    public static <P> void safeExecuteOnExecutor(AsyncTask<P, ?, ?> var0, P... var1) {
        if (Build.VERSION.SDK_INT >= 11) {
            var0.executeOnExecutor(sExecutor, var1);
        } else {
            var0.execute(var1);
        }

    }

    static {
        init();
    }
}
