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
import android.widget.TextView;

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
    private LinearLayout addCollectLl;
    private LinearLayout addCollectedLl;
    private LinearLayout refreshLl;
    private LinearLayout optionsLl;
    private LinearLayout mangaLl;
    private LinearLayout exitLl;
    private LinearLayout headLl;
    private ImageView userHeadIv;
    private TextView userNameTv;
    private RelativeLayout closeDwonArrowRl;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2018-02-10 17:12:02 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {

    }


    private OnWebBottomBarClickListener onWebBottomBarClickListener;
    private OnWebBottomBarOptionsClickListener onWebBottomBarOptionsClickListener;
    private OnWebBottomBarLogoutClickListener onWebBottomBarLogoutClickListener;

    public WebBottomOptionsBar(Context context) {
        super(context);
//        super(context, R.style.CustomDialog);
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
//        lp.y = DisplayUtil.dip2px(context, 50); // 闁哄倿顣︾紞鍛磾閻㈡洟宕搁幇顓犲灱
        window.setAttributes(lp);
    }

    private void init() {
        addCollectLl = (LinearLayout) findViewById(R.id.add_collect_ll);
        addCollectedLl = (LinearLayout) findViewById(R.id.add_collected_ll);
        refreshLl = (LinearLayout) findViewById(R.id.refresh_ll);
        optionsLl = (LinearLayout) findViewById(R.id.options_ll);
        mangaLl = (LinearLayout) findViewById(R.id.manga_ll);
        exitLl = (LinearLayout) findViewById(R.id.exit_ll);
        headLl = (LinearLayout) findViewById(R.id.head_ll);
        userHeadIv = (ImageView) findViewById(R.id.user_head_iv);
        userNameTv = (TextView) findViewById(R.id.user_name_tv);
        closeDwonArrowRl = (RelativeLayout) findViewById(R.id.close_dialog_rl);

        closeDwonArrowRl.setOnClickListener(this);
        addCollectLl.setOnClickListener(this);
        addCollectedLl.setOnClickListener(this);
        refreshLl.setOnClickListener(this);
        optionsLl.setOnClickListener(this);
        mangaLl.setOnClickListener(this);
        exitLl.setOnClickListener(this);
        headLl.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_collect_ll:
                if (null != onWebBottomBarOptionsClickListener) {
                    onWebBottomBarOptionsClickListener.onCollectClick();
                }
                break;
            case R.id.add_collected_ll:
                if (null != onWebBottomBarOptionsClickListener) {
                    onWebBottomBarOptionsClickListener.onCollectedClick();
                }
                break;
            case R.id.refresh_ll:
                if (null != onWebBottomBarClickListener) {
                    onWebBottomBarClickListener.onRefreshClick();
                }
                break;
            case R.id.exit_ll:
                if (null != onWebBottomBarLogoutClickListener) {
                    onWebBottomBarLogoutClickListener.onLogoutClick();
                }
                break;
            case R.id.close_dialog_rl:
                dismiss();
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
