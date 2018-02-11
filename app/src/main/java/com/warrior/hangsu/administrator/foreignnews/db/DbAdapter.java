//package com.warrior.hangsu.administrator.foreignnews.db;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//
//import com.warrior.hangsu.administrator.foreignnews.bean.CollectBean;
//import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
//
//import java.util.ArrayList;
//
//
//public class DbAdapter {
//    public static final String DB_NAME = "browser.db";
//    private DbHelper dbHelper;
//    private SQLiteDatabase db;
//
//    public DbAdapter(Context context) {
//        dbHelper = new DbHelper(context, DB_NAME, null, Globle.DB_VERSION);
//        db = dbHelper.getWritableDatabase();
//    }
//
//    /**
//     * 插入一条书籍信息
//     */
//    public void insertCollectTableTb(String title, String url, String bpPath) {
//        if (queryadded(url)) {
//            return;
//        }
//        db.execSQL(
//                "insert into COLLECT(title,url,titleThumb) values (?,?,?)",
//                new Object[]{title, url, bpPath});
//    }
//
//
//    /**
//     * 查询所有书籍
//     *
//     * @return
//     */
//    public ArrayList<CollectBean> queryAllCollect() {
//        ArrayList<CollectBean> resBeans = new ArrayList<CollectBean>();
//        Cursor cursor = db
//                .query("COLLECT", null, null, null, null, null, null);
//
//        while (cursor.moveToNext()) {
//            String title = cursor.getString(cursor.getColumnIndex("title"));
//            String url = cursor
//                    .getString(cursor.getColumnIndex("url"));
//            String titleThumb = cursor
//                    .getString(cursor.getColumnIndex("titleThumb"));
//            CollectBean item = new CollectBean();
//            item.setTitle(title);
//            item.setUrl(url);
//            item.setTitleThumb(titleThumb);
//            resBeans.add(item);
//        }
//        cursor.close();
//        return resBeans;
//    }
//
//
//    /**
//     * 查询是否已经添加过
//     */
//    public boolean queryadded(String URL) {
//        Cursor cursor = db.rawQuery(
//                "select url from COLLECT where url=?",
//                new String[]{URL});
//        int count = cursor.getCount();
//        cursor.close();
//        if (count > 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//
//    /**
//     * 删除书籍
//     */
//    public void deleteCollect(String url) {
//        db.execSQL("delete from COLLECT where url=?",
//                new Object[]{url});
//
//    }
//
//
//    public void closeDb() {
//        if (null != db) {
//            db.close();
//        }
//    }
//}
