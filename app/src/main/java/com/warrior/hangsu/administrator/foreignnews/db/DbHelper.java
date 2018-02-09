package com.warrior.hangsu.administrator.foreignnews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private Context xcontext;

    public static final String COLLECT = "create table if not exists COLLECT ("
            + "id integer primary key autoincrement,"
            + "url text," + "title text," + "titleThumb text)";

    public DbHelper(Context context, String name, CursorFactory factory,
                    int version) {
        super(context, name, factory, version);
        xcontext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(COLLECT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
