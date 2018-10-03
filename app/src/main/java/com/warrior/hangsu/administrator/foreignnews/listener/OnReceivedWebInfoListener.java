package com.warrior.hangsu.administrator.foreignnews.listener;

/**
 * Created by Administrator on 2018/10/3.
 */

public interface OnReceivedWebInfoListener {
    void onReceivedTitle(String title);

    void onProgress(int progress);
}
