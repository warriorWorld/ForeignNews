package com.warrior.hangsu.administrator.foreignnews.business.read;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 个人信息页
 */
public class ReadTextOnlyActivity extends BaseActivity implements View.OnClickListener {
    private TextView readTv;
    private String url;
    private static org.jsoup.nodes.Document doc;
    private String urlContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            return;
        }
        initUI();
        doGetData();
    }

    private void initUI() {
        readTv = (TextView) findViewById(R.id.read_tv);
        baseTopBar.setTitle("文本阅读");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_text;
    }

    private void doGetData() {
        new Thread() {
            @Override
            public void run() {
                try {
                    doc = Jsoup.connect(url)
                            .timeout(10000).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (null != doc) {
                    Elements test = doc.select("p");
                    for (int i = 0; i < test.size(); i++) {
                        urlContent += test.get(i).text();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            readTv.setText(urlContent);
                        }
                    });
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_btn:
                break;
        }
    }
}
