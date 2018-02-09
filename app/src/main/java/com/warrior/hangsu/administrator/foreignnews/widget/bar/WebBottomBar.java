package com.warrior.hangsu.administrator.foreignnews.widget.bar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.foreignnews.R;

/**
 * Created by Administrator on 2016/10/5.
 */
public class WebBottomBar extends RelativeLayout implements View.OnClickListener {
    private Context context;
    private ImageView backwardIV, forwardIV, homeIV, optionsIV;
    private TextView webNumTV;
    private View bottomLayout;
    private OnWebBottomBarClickListener onWebBottomBarClickListener;
    private OnWebBottomBarOptionsClickListener onWebBottomBarOptionsClickListener;
    private OnWebBottomBarHomeClickListener onWebBottomBarHomeClickListener;
    private OnWebBottomBarLogoutClickListener onWebBottomBarLogoutClickListener;
    private OnWebBottomBarWebNumClickListener onWebBottomBarWebNumClickListener;

    private WebBottomOptionsBar optionsBar;

    public WebBottomBar(Context context) {
        this(context, null);
    }

    public WebBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void showOptionsDialog() {
        if (null == optionsBar) {
            optionsBar = new WebBottomOptionsBar(context);
            optionsBar.setOnWebBottomBarClickListener(onWebBottomBarClickListener);
            optionsBar.setOnWebBottomBarLogoutClickListener(onWebBottomBarLogoutClickListener);
            optionsBar.setOnWebBottomBarOptionsClickListener(onWebBottomBarOptionsClickListener);
            optionsBar.setCancelable(true);
        }
        optionsBar.show();
    }


    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_bottombar_web, this);
        bottomLayout = findViewById(R.id.bottom_layout);
        backwardIV = (ImageView) findViewById(R.id.backward_iv);
        forwardIV = (ImageView) findViewById(R.id.forward_iv);
        homeIV = (ImageView) findViewById(R.id.home_iv);
        optionsIV = (ImageView) findViewById(R.id.options_iv);
        webNumTV = (TextView) findViewById(R.id.web_num_iv);
        backwardIV.setOnClickListener(this);
        forwardIV.setOnClickListener(this);
        homeIV.setOnClickListener(this);
        optionsIV.setOnClickListener(this);
        webNumTV.setOnClickListener(this);
    }

    public void toggleBackwardState(boolean canGoBack) {
//        if (canGoBack) {
//            backwardIV.setImageResource(R.drawable.backward);
//        } else {
//            backwardIV.setImageResource(R.drawable.circle);
//        }
    }

    public void toggleForwardState(boolean canGoForward) {
//        if (canGoForward) {
//            forwardIV.setImageResource(R.drawable.forward);
//        } else {
//            forwardIV.setImageResource(R.drawable.circle);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backward_iv:
                if (null != onWebBottomBarClickListener) {
                    onWebBottomBarClickListener.onBackwardClick();
                }
                break;
            case R.id.forward_iv:
                if (null != onWebBottomBarClickListener) {
                    onWebBottomBarClickListener.onForwardClick();
                }
                break;
            case R.id.home_iv:
                if (null != onWebBottomBarHomeClickListener) {
                    onWebBottomBarHomeClickListener.onHomeClick();
                }
                break;
            case R.id.web_num_iv:
                if (null != onWebBottomBarWebNumClickListener) {
                    onWebBottomBarWebNumClickListener.onWebNumClick();
                }
                break;
            case R.id.options_iv:
                showOptionsDialog();
                break;
        }
    }

    public void setWebNum(int i) {
        webNumTV.setText("" + i);
    }

    public void setOnWebBottomBarHomeClickListener(OnWebBottomBarHomeClickListener onWebBottomBarHomeClickListener) {
        this.onWebBottomBarHomeClickListener = onWebBottomBarHomeClickListener;
    }

    public void setOnWebBottomBarClickListener(OnWebBottomBarClickListener onWebBottomBarClickListener) {
        this.onWebBottomBarClickListener = onWebBottomBarClickListener;
    }

    public void setOnWebBottomBarLogoutClickListener(OnWebBottomBarLogoutClickListener onWebBottomBarLogoutClickListener) {
        this.onWebBottomBarLogoutClickListener = onWebBottomBarLogoutClickListener;
    }

    public void setOnWebBottomBarWebNumClickListener(OnWebBottomBarWebNumClickListener onWebBottomBarWebNumClickListener) {
        this.onWebBottomBarWebNumClickListener = onWebBottomBarWebNumClickListener;
    }

    public void setOnWebBottomBarOptionsClickListener(OnWebBottomBarOptionsClickListener onWebBottomBarOptionsClickListener) {
        this.onWebBottomBarOptionsClickListener = onWebBottomBarOptionsClickListener;
    }

    public interface OnWebBottomBarClickListener {
        void onBackwardClick();

        void onForwardClick();

        void onRefreshClick();
    }

    public interface OnWebBottomBarOptionsClickListener {
        void onCollectClick();

        void onCollectedClick();

        void onShareClick();

    }

    public interface OnWebBottomBarLogoutClickListener {
        void onLogoutClick();
    }

    public interface OnWebBottomBarHomeClickListener {
        void onHomeClick();
    }

    public interface OnWebBottomBarWebNumClickListener {
        void onWebNumClick();
    }
}
