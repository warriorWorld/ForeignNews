package com.warrior.hangsu.administrator.foreignnews.okhttp;

import com.tencent.mmkv.MMKV;

public class CacheCaretaker {

    public static void saveContent(String key, String content) {
        boolean b = MMKV.defaultMMKV().encode(key, content);
    }

    public static String getContent(String key) {
        return MMKV.defaultMMKV().decodeString(key);
    }

    public static void clean() {
        MMKV.defaultMMKV().clearAll();
    }
}
