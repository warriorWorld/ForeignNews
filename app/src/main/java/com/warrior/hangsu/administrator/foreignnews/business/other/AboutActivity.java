package com.warrior.hangsu.administrator.foreignnews.business.other;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.business.login.RegisterActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;


public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI() {
        baseTopBar.setTitle("设置");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
        }
        if (null != intent) {
            startActivity(intent);
        }
    }
}
