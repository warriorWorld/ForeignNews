package com.warrior.hangsu.administrator.foreignnews.webview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;


/**
 * 在webview的界面里面填加 长按界面时出现搜索按钮 点击搜索按钮跳转到搜索页面
 *
 * @author 王训龙 360404113
 *         <p/>
 *         2014⑻⑻ 上午11:39:16
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FindWebView extends WebView {
    public Callback callback;
    private OnScrollChangedListener mOnScrollChangedListener;
    private boolean isTest = true;


    @SuppressWarnings("deprecation")
    public FindWebView(Context context, AttributeSet attrs, int defStyle,
                       boolean privateBrowsing) {
        super(context, attrs, defStyle, privateBrowsing);
        init();
    }


    public FindWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    public FindWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public FindWebView(Context context) {
        super(context);
        init();
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetJavaScriptEnabled")
    private void init() {
        WebSettings setting = getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        addJavascriptInterface(new SelectedText(), "search");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && isTest) {
            //支持webview调试
            WebView.setWebContentsDebuggingEnabled(true);
        }
        setWebViewClient(new WebViewClient() {
            // This is how it is supposed to work, so I'll leave it in, but this
            // doesn't get called on pinch
            // So for now I have to use deprecated getScale method.
            @Override
            public void onScaleChanged(WebView view, float oldScale,
                                       float newScale) {
                //这样会阻止缩放
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 这样写是为了可以在webview中点击链接还继续在webview中显示,而不是打开浏览器
                loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // 一般实现以下这个方法,这个方法是这个网页结束后调用的
                super.onPageFinished(view, url);
                // 全部完成后注入效率太低
//                JSinject();
            }
        });

    }


    @Override
    public ActionMode startActionMode(Callback callback) {
        CustomizedSelectActionModeCallback customizedSelectActionModeCallback = new CustomizedSelectActionModeCallback(
                callback);
        return super.startActionMode(customizedSelectActionModeCallback);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    public class CustomizedSelectActionModeCallback implements ActionMode.Callback {
        private Callback callback;


        public CustomizedSelectActionModeCallback(Callback callback) {
            this.callback = callback;
        }


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return callback.onCreateActionMode(mode, menu);
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return callback.onPrepareActionMode(mode, menu);
        }


        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item == null || TextUtils.isEmpty(item.getTitle())) {
                return callback.onActionItemClicked(mode, item);
            }
            if (!item.getTitle().toString().contains("搜索")
                    && !item.getTitle().toString().contains("search")) {
                return callback.onActionItemClicked(mode, item);
            }
            loadUrl("javascript:window.search.show(window.getSelection().toString());");
            clearFocus();
            return true;
        }


        @Override
        public void onDestroyActionMode(ActionMode mode) {
            callback.onDestroyActionMode(mode);
        }
    }


    public class SelectedText {
        @JavascriptInterface
        public void show(String data) {
            // TODO　这里获得选中的文字
            Toast.makeText(getContext(), data, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getContext(), SearchActivity.class);
//            intent.putExtra(SearchActivity.TAG_SEARCH, data);
//            getContext().startActivity(intent);
        }
    }


    private int dY;


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);


        int dy = t - oldt;
        dY += dy;
        if (mOnScrollChangedListener != null && Math.abs(dY) > 10) {
            dY = 0;
            mOnScrollChangedListener.onScroll(l, t, oldl, oldt);
        }
    }


    public interface OnScrollChangedListener {
        public void onScroll(int l, int t, int oldl, int oldt);
    }


    public void setOnScrollChangedListener(OnScrollChangedListener mOnScrollChangedListener) {
        this.mOnScrollChangedListener = mOnScrollChangedListener;
    }
}
