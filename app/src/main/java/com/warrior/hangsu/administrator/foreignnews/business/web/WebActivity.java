package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.os.Bundle;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseFragment;
import com.warrior.hangsu.administrator.foreignnews.base.FragmentContainerActivity;


/**
 * 个人信息页
 */
public class WebActivity extends FragmentContainerActivity {
    private WebFragment mWebFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWebFragment = new WebFragment();
        super.onCreate(savedInstanceState);
        hideBaseTopBar();
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
