package com.warrior.hangsu.administrator.foreignnews.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.warrior.hangsu.administrator.foreignnews.bottombar.WebBottomBar;

public class BaseActivity extends FragmentActivity implements WebBottomBar.OnWebBottomBarLogoutClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


    @Override
    public void onLogoutClick() {
        this.finish();
    }
}
