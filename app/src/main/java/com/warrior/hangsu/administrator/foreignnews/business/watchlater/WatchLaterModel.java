package com.warrior.hangsu.administrator.foreignnews.business.watchlater;

import android.content.Context;
import android.graphics.Bitmap;

import com.warrior.hangsu.administrator.foreignnews.configure.Globle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class WatchLaterModel implements IWatchLaterModel {

    @Override
    public List<String> getWatchLater(Context context) {
        try {
            File f = new File(Globle.CACHE_PATH);//第一级目录 cache
            if (!f.exists()) {
                f.mkdirs();
            }
            File[] files = f.listFiles();//第二级目录 具体网页们
            if (null != files && files.length > 0) {
                ArrayList<String> htmlList = new ArrayList<String>();
                for (File file : files) {
                    htmlList.add(file.getName());
                }
                return htmlList;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
