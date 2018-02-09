package com.warrior.hangsu.administrator.foreignnews.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * Created by Administrator on 2016/10/7.
 */

public class DownLoadUtil {
    private static final File parentPath = Environment
            .getExternalStorageDirectory();
    private static String storagePath = "";
    private static final String DST_FOLDER_NAME = "garbage";
    private static String DST_FOLDER_NAME2 = "img";

    /**
     * 初始化保存路径
     *
     * @return
     */
    private static String initPath() {
        storagePath = parentPath.getAbsolutePath() + "/" + DST_FOLDER_NAME
                + "/" + DST_FOLDER_NAME2;
        Log.d("路径", storagePath);
        File f = new File(storagePath);
        if (!f.exists()) {
            // 如果不存在 就创建
            f.mkdirs();
        }
        return storagePath;
    }

    public static String getPath() {
        return storagePath;
    }

    public static void downloadImg(final Context context, final String imgUrl) {
        new Thread() {
            public void run() {
                Bitmap bp = null;
                //从网络上获取到图片
                bp = loadImageFromNetwork(imgUrl);
                if (null != bp) {
                    //把图片保存到本地
                    saveBitmap(bp, System.currentTimeMillis() + ".jpg");
                } else {

                }
            }
        }.start();
    }

    public static String saveBitmap(Bitmap b, String bmpName) {
        b = ImageUtil.imageZoom(b, 480);

        String path = initPath();
        String jpegName = path + "/" + bmpName;
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jpegName;
    }


    /**
     * 递归删除文件和文件夹 因为file.delete();只能删除空文件夹或文件 所以需要这么递归循环删除
     *
     * @param file 要删除的根目�?
     */
    public static void deleteFile(File file) {
        if (file.isFile() && file.exists()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    /**
     * 网络获取图片
     *
     * @param imageUrl
     * @return
     */
    private static Bitmap loadImageFromNetwork(String imageUrl) {
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(imageUrl).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {

        }
        return bitmap;
    }
}
