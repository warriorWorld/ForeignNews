package com.warrior.hangsu.administrator.foreignnews.base;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.warrior.hangsu.administrator.foreignnews.R;


/**
 * 投资记录页
 */
public abstract class FragmentContainerActivity extends BaseFragmentActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    protected abstract BaseFragment getFragment();

    protected abstract String getTopBarTitle();

    @Override
    protected void initUI() {
        super.initUI();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, getFragment());
        transaction.commit();

        baseTopBar.setTitle(getTopBarTitle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_container;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

}
