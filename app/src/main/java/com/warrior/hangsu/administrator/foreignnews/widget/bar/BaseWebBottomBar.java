package com.warrior.hangsu.administrator.foreignnews.widget.bar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarHomeClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarWebNumClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarRefreshClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarSkipToURLListener;

/**
 * Created by Administrator on 2018/2/10.
 */

public abstract class BaseWebBottomBar extends RelativeLayout {
    public BaseWebBottomBar(Context context) {
        super(context);
    }

    public BaseWebBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWebBottomBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseWebBottomBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public abstract void setOnWebBottomBarClickListener(OnWebBottomBarClickListener listener);

    public abstract void setOnWebBottomBarLogoutClickListener(OnWebBottomBarLogoutClickListener listener);

    public abstract void setOnWebBottomBarHomeClickListener(OnWebBottomBarHomeClickListener listener);

    public abstract void setOnWebBottomBarOptionsClickListener(OnWebBottomBarOptionsClickListener listener);

    public abstract void setOnWebBottomBarWebNumClickListener(OnWebBottomBarWebNumClickListener listener);
}
