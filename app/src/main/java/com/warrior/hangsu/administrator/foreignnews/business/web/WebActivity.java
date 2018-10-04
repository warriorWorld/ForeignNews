package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.os.Bundle;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseFragment;
import com.warrior.hangsu.administrator.foreignnews.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.TopBar;

import javax.microedition.khronos.opengles.GL;


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
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                if (Globle.IS_TEST) {
                    showBaseDialog(mWebFragment.getUrl(), "", "知道了", "", null);
                }
            }
        });
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
