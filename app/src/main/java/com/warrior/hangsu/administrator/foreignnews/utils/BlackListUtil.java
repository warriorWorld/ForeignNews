package com.warrior.hangsu.administrator.foreignnews.utils;

import android.content.Context;

import com.warrior.hangsu.administrator.foreignnews.configure.Globle;

/**
 * Created by Administrator on 2016/10/7.
 */

public class BlackListUtil {
    public static boolean isBlackList(String url) {
        for (int i = 0; i < Globle.BLACK_LIST.length; i++) {
            if (url.contains(Globle.BLACK_LIST[i])) {
                return true;
            }
        }
        return false;
    }
}
