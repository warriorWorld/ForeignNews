package com.warrior.hangsu.administrator.foreignnews.widget.dialog;

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
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.foreignnews.mannger.SettingManager;
import com.warrior.hangsu.administrator.foreignnews.mannger.ThemeManager;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;


/**
 * Created by Administrator on 2016/10/5.
 */
public class ReadDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private OnReadDialogClickListener onReadDialogClickListener;
    private LinearLayout saveLl;
    private LinearLayout toggleTranslateWayLl;
    private TextView translateWayTv;
    private LinearLayout closeTranslateLl;
    private TextView closeTranslateTv;
    private LinearLayout textSizeLl;
    private TextView textSizeTv;
    private LinearLayout backgroundStyleLl;
    private TextView backgroundStyleTv;
    private LinearLayout sunMoonModeLl;
    private ImageView sunMoonModeIv;
    private TextView sunMoonModeTv;
    private ImageView userHeadIv;
    private TextView userNameTv;
    private RelativeLayout closeDialogRl;
    private LinearLayout headLl;
    private boolean isProgressChange = false;

    public ReadDialog(Context context) {
        super(context);
//        super(context, R.style.CustomDialog);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_read);
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

        refreshUI();
    }

    private void init() {
        toggleTranslateWayLl = (LinearLayout) findViewById(R.id.toggle_translate_way_ll);
        saveLl = (LinearLayout) findViewById(R.id.save_txt_ll);
        translateWayTv = (TextView) findViewById(R.id.translate_way_tv);
        closeTranslateLl = (LinearLayout) findViewById(R.id.close_translate_ll);
        closeTranslateTv = (TextView) findViewById(R.id.close_translate_tv);
        textSizeLl = (LinearLayout) findViewById(R.id.text_size_ll);
        textSizeTv = (TextView) findViewById(R.id.text_size_tv);
        backgroundStyleLl = (LinearLayout) findViewById(R.id.background_style_ll);
        backgroundStyleTv = (TextView) findViewById(R.id.background_style_tv);
        sunMoonModeLl = (LinearLayout) findViewById(R.id.sun_moon_mode_ll);
        sunMoonModeIv = (ImageView) findViewById(R.id.sun_moon_mode_iv);
        sunMoonModeTv = (TextView) findViewById(R.id.sun_moon_mode_tv);
        userHeadIv = (ImageView) findViewById(R.id.user_head_iv);
        userNameTv = (TextView) findViewById(R.id.user_name_tv);
        closeDialogRl = (RelativeLayout) findViewById(R.id.close_dialog_rl);
        headLl = (LinearLayout) findViewById(R.id.head_ll);

        saveLl.setOnClickListener(this);
        toggleTranslateWayLl.setOnClickListener(this);
        closeTranslateLl.setOnClickListener(this);
        textSizeLl.setOnClickListener(this);
        backgroundStyleLl.setOnClickListener(this);
        sunMoonModeLl.setOnClickListener(this);
        closeDialogRl.setOnClickListener(this);
        headLl.setOnClickListener(this);
    }


    public void refreshUI() {
        if (!TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            setUserName(LoginBean.getInstance().getUserName());
        } else {
            setUserName("立即登录");
        }
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(context,
                ShareKeys.CLOSE_TRANSLATE, false)) {
            closeTranslateTv.setText("开启查词");
        } else {
            closeTranslateTv.setText("关闭查词");
        }
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(context,
                ShareKeys.DOUBLE_CLICK_TRANSLATE, false)) {
            translateWayTv.setText("切换到单击查词");
        } else {
            translateWayTv.setText("切换到双击查词");
        }
        backgroundStyleTv.setText("背景样式(" + ThemeManager.THEME_LIST[SettingManager.getInstance().getReadTheme()] + ")");
        textSizeTv.setText("字体大小(" + SettingManager.getInstance().getFontSizeExplain() + ")");
        toggleSunMoonMode();
    }

    private void toggleSunMoonMode() {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(context,
                ShareKeys.ISNIGHT, false)) {
            sunMoonModeIv.setBackgroundResource(R.drawable.sun_icon);
            sunMoonModeTv.setText("切换到日间模式");
        } else {
            sunMoonModeIv.setBackgroundResource(R.drawable.moon_icon);
            sunMoonModeTv.setText("切换到夜间模式");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save_txt_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onSaveClick();
                }
                break;
            case R.id.toggle_translate_way_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onToggleTranslateWayClick();
                }
                break;
            case R.id.close_translate_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onCloseTranslateClick();
                }
                break;
            case R.id.text_size_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onTextSizeClick();
                }
                break;
            case R.id.sun_moon_mode_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onSunMoonToggleClick();
                }
                break;
            case R.id.background_style_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onBackgroundStyleClick();
                }
                break;
            case R.id.head_ll:
                if (null != onReadDialogClickListener) {
                    onReadDialogClickListener.onUserHeadClick();
                }
                break;
            case R.id.close_dialog_rl:
                break;
        }
        dismiss();
    }

    public void setUserName(String name) {
        userNameTv.setText(name);
    }

    public void setOnReadDialogClickListener(OnReadDialogClickListener onReadDialogClickListener) {
        this.onReadDialogClickListener = onReadDialogClickListener;
    }
}
