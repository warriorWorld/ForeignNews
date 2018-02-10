package com.warrior.hangsu.administrator.foreignnews.widget.bar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.DisplayUtil;

/**
 * Created by Administrator on 2016/10/5.
 */
public class WebBottomOptionsBar extends Dialog implements View.OnClickListener {
    private Context context;
    private RelativeLayout bottomOptionsRL;
    private ImageView collectIV, collectedIV, shareIV, refreshIV, logoutIV;
    private OnWebBottomBarClickListener onWebBottomBarClickListener;
    private OnWebBottomBarOptionsClickListener onWebBottomBarOptionsClickListener;
    private OnWebBottomBarLogoutClickListener onWebBottomBarLogoutClickListener;

    public WebBottomOptionsBar(Context context) {
//        super(context);
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bottombar_opitons_web);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(lp);
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay(); // 闁兼儳鍢茶ぐ鍥╀沪韫囨挾顔庨悗鐟邦潟閿熸垝绶氶悵顕�鎮介敓锟�
        lp.width = (int) (d.getWidth() * 1); // 閻庣妫勭�瑰磭鎷嬮崜褏鏋�
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimationDialog);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        lp.y = DisplayUtil.dip2px(context, 50); // 闁哄倿顣︾紞鍛磾閻㈡洟宕搁幇顓犲灱
        window.setAttributes(lp);
    }

    private void init() {
        bottomOptionsRL = (RelativeLayout) findViewById(R.id.bottom_up_options_rl);
        collectIV = (ImageView) findViewById(R.id.collect_iv);
        collectedIV = (ImageView) findViewById(R.id.collected_iv);
        shareIV = (ImageView) findViewById(R.id.share_iv);
        refreshIV = (ImageView) findViewById(R.id.refresh_iv);
        logoutIV = (ImageView) findViewById(R.id.logout_iv);
        collectIV.setOnClickListener(this);
        collectedIV.setOnClickListener(this);
        shareIV.setOnClickListener(this);
        refreshIV.setOnClickListener(this);
        logoutIV.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.collect_iv:
                if (null != onWebBottomBarOptionsClickListener) {
                    onWebBottomBarOptionsClickListener.onCollectClick();
                }
                break;
            case R.id.collected_iv:
                if (null != onWebBottomBarOptionsClickListener) {
                    onWebBottomBarOptionsClickListener.onCollectedClick();
                }
                break;
            case R.id.share_iv:
                if (null != onWebBottomBarOptionsClickListener) {
                    onWebBottomBarOptionsClickListener.onShareClick();
                }
                break;
            case R.id.refresh_iv:
                if (null != onWebBottomBarClickListener) {
                    onWebBottomBarClickListener.onRefreshClick();
                }
                break;
            case R.id.logout_iv:
                if (null != onWebBottomBarLogoutClickListener) {
                    onWebBottomBarLogoutClickListener.onLogoutClick();
                }
                break;
        }
        dismiss();
    }

    public void setOnWebBottomBarOptionsClickListener(OnWebBottomBarOptionsClickListener onWebBottomBarOptionsClickListener) {
        this.onWebBottomBarOptionsClickListener = onWebBottomBarOptionsClickListener;
    }

    public void setOnWebBottomBarClickListener(OnWebBottomBarClickListener onWebBottomBarClickListener) {
        this.onWebBottomBarClickListener = onWebBottomBarClickListener;
    }

    public void setOnWebBottomBarLogoutClickListener(OnWebBottomBarLogoutClickListener onWebBottomBarLogoutClickListener) {
        this.onWebBottomBarLogoutClickListener = onWebBottomBarLogoutClickListener;
    }
}
