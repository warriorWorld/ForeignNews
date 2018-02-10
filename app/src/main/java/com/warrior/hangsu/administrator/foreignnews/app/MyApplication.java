package com.warrior.hangsu.administrator.foreignnews.app;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;

/**
 * Created by Administrator on 2016/4/3.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        /**
         * 友盟
         */
        UMShareAPI.get(this);
        PlatformConfig.setWeixin("wx967daebe835fbeac", "5bb696d9ccd75a38c8a0bfe0675559b3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
    }


    public static Context getContext() {
        return context;
    }
}
