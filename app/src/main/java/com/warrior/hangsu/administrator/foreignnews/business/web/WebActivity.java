package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.os.Bundle;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseFragment;
import com.warrior.hangsu.administrator.foreignnews.base.FragmentContainerActivity;


/**
 * 个人信息页
 */
public class WebActivity extends FragmentContainerActivity {
    private TranslateWebFragment mWebFragment;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWebFragment = new TranslateWebFragment();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        super.initUI();
        title = getIntent().getStringExtra("title");
        baseTopBar.setTitle(title);
    }

    @Override
    protected BaseFragment getFragment() {
        return mWebFragment;
    }

    @Override
    protected String getTopBarTitle() {
        return getResources().getString(R.string.app_name);
    }
}
