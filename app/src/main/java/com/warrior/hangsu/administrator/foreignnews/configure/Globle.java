package com.warrior.hangsu.administrator.foreignnews.configure;

import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class Globle {
    /**
     * API key：1447394905
     * keyfrom：foreignnews
     * 创建时间：2016-10-06
     * 网站名称：foreignnews
     * 网站地址：http://warrior.hangsu.administrator.foreignnews.com
     */
    public static final String YOUDAO = "http://fanyi.youdao.com/openapi.do?keyfrom=foreignnews&key=14473" +
            "94905&type=data&doctype=json&version=1.1&q=";
    //数据库版本号
    public static final int DB_VERSION = 1;

    final public static DisplayImageOptions normalImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
//            .showImageOnLoading(R.drawable.empty_list)
//            .showImageOnFail(R.drawable.empty_list)
            .build();
    final public static DisplayImageOptions smallImageOptions = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.ARGB_8888)
//            .showImageOnLoading(R.drawable.spider_hat_color512)
//            .showImageOnFail(R.drawable.spider_hat_gray512)
            .build();
    public static final String DEFAULT_MAIN_URL = "http://www.bbc.com/";
}
