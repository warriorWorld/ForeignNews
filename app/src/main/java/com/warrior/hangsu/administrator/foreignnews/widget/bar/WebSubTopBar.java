package com.warrior.hangsu.administrator.foreignnews.widget.bar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.listener.OnCopyClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.DisplayUtil;

/**
 * Created by Administrator on 2016/10/5.
 */
public class WebSubTopBar extends Dialog implements View.OnClickListener {
    private Context context;
    private RelativeLayout copyUrlRl;

    private OnCopyClickListener onCopyClickListener;

    public WebSubTopBar(Context context) {
//        super(context);
        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_sub_top_bar);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(lp);
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay(); // 闁兼儳鍢茶ぐ鍥╀沪韫囨挾顔庨悗鐟邦潟閿熸垝绶氶悵顕�鎮介敓锟�
        lp.width = (int) (d.getWidth() * 1); // 閻庣妫勭�瑰磭鎷嬮崜褏鏋�
        window.setGravity(Gravity.TOP);
        window.setWindowAnimations(R.style.AnimationDialog);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        lp.y = DisplayUtil.dip2px(context, 50); // 闁哄倿顣︾紞鍛磾閻㈡洟宕搁幇顓犲灱
        window.setAttributes(lp);
    }

    private void init() {
        copyUrlRl = (RelativeLayout) findViewById(R.id.copy_url_rl);
        copyUrlRl.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.copy_url_rl:
                if (null != onCopyClickListener) {
                    onCopyClickListener.onCopy();
                }
                break;
        }
        dismiss();
    }

    public void setOnCopyClickListener(OnCopyClickListener onCopyClickListener) {
        this.onCopyClickListener = onCopyClickListener;
    }
}
