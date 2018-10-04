/*
 * Copyright (C) 2012 Brandon Tate
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.warrior.hangsu.administrator.foreignnews.listener.OnAllVersionScrollChangeListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnUrlChangeListener;


/**
 * Webview subclass that hijacks web content selection.
 *
 * @author Brandon Tate
 */
public class MyWebView extends WebView implements View.OnLongClickListener {
    /**
     * Context.
     */
    protected Context mContext;
    protected OnPeanutWebViewListener onPeanutWebViewListener;
    private OnWebViewLongClickListener onWebViewLongClickListener;
    private OnUrlChangeListener onUrlChangeListener;
    private OnAllVersionScrollChangeListener onAllVersionScrollChangeListener;

    public MyWebView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public MyWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(context);

    }

    public MyWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }


    /**
     * Setups up the web view.
     *
     * @param context
     */
    protected void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().flush();
        }
        setOnLongClickListener(this);
        // Webview init
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //插件状态
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);// 允许通过网页上传文件
        webSettings.setBuiltInZoomControls(true);// 可缩放
        //隐藏缩放按钮
        webSettings.setDisplayZoomControls(false);
        webSettings.setSupportZoom(true);
        webSettings.setTextZoom(100);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);// 打开本地缓存提供JS调用,至关重要
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);// 实现8倍缓存

        buildDrawingCache(true);
        setDrawingCacheEnabled(true);


        // Webview client.
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
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                try {
                    // Otherwise allow the OS to handle things like tel, mailto, etc.
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                } catch (ActivityNotFoundException e) {

                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (null != onUrlChangeListener) {
                    onUrlChangeListener.onUrlChange(url);
                }
                // 一般实现以下这个方法,这个方法是这个网页结束后调用的
                super.onPageFinished(view, url);
            }
        });
        setWebChromeClient(new MyWebChromeClient());


//        setOnKeyListener(new OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
//                    goBack(); // goBack()表示返回WebView的上一页面
//                    return true;
//                } else {
//                    return false;
//                }
//            }
//        });
    }

    public void setOnPeanutWebViewListener(OnPeanutWebViewListener onPeanutWebViewListener) {
        this.onPeanutWebViewListener = onPeanutWebViewListener;
    }

    @Override
    public boolean onLongClick(View v) {
        HitTestResult result = ((WebView) v).getHitTestResult();
        int type = result.getType();
        if (type == HitTestResult.IMAGE_TYPE) {
            onWebViewLongClickListener.onImgLongClick(result.getExtra());
            return true;
        }
        return false;
    }

    public void setOnWebViewLongClickListener(OnWebViewLongClickListener onWebViewLongClickListener) {
        this.onWebViewLongClickListener = onWebViewLongClickListener;
    }

    public void setOnUrlChangeListener(OnUrlChangeListener onUrlChangeListener) {
        this.onUrlChangeListener = onUrlChangeListener;
    }

    public void setOnAllVersionScrollChangeListener(OnAllVersionScrollChangeListener onAllVersionScrollChangeListener) {
        this.onAllVersionScrollChangeListener = onAllVersionScrollChangeListener;
    }

    /***
     * webChromeClient是一个比较神奇的东西，其里面提供了一系列的方法，
     * 分别作用于我们的javascript代码调用特定方法时执行，我们一般在其内部
     * 将javascript形式的展示切换为android的形式。
     * 例如：我们重写了onJsAlert方法，那么当页面中需要弹出alert窗口时，便
     * 会执行我们的代码，按照我们的Toast的形式提示用户。
     */
    class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 JsResult result) {
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // 得到读取进度
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (null != onPeanutWebViewListener) {
                onPeanutWebViewListener.onReceivedTitle(getUrl(),title);
            }
        }
    }

    public interface OnPeanutWebViewListener {
        void onReceivedTitle(String url,String title);
    }

    public interface OnWebViewLongClickListener {
        void onImgLongClick(String imgUrl);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (null != onAllVersionScrollChangeListener) {
            onAllVersionScrollChangeListener.onScrollChange(this, l, t, oldl, oldt);
        }
    }
}
