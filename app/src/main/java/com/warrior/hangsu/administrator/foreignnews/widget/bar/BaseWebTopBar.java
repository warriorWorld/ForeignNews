package com.warrior.hangsu.administrator.foreignnews.widget.bar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarRefreshClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarSkipToURLListener;

/**
 * Created by Administrator on 2018/2/10.
 */

public abstract class BaseWebTopBar extends RelativeLayout {
    public BaseWebTopBar(Context context) {
        super(context);
    }

    public BaseWebTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseWebTopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BaseWebTopBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void setTitleTextColor(int color);

    public abstract void setOnWebTopBarRefreshClickListener(OnWebTopBarRefreshClickListener listener);

    public abstract void setOnWebTopBarSkipToURLListener(OnWebTopBarSkipToURLListener listener);

    public abstract void toggleEditAndShow(boolean isEdit);

    public abstract void setProgress(int progress);

    public abstract void setTitle(String title);

    public abstract void setPath(String url);
}
