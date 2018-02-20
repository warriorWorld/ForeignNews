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

package com.warrior.hangsu.administrator.foreignnews.widget.webview;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.business.read.ReadTextOnlyActivity;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarRefreshClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarSkipToURLListener;
import com.warrior.hangsu.administrator.foreignnews.utils.BlackListUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.BaseWebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.BaseWebTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;

/**
 * Webview subclass that hijacks web content selection.
 *
 * @author Brandon Tate
 */
public class TranslateWebView extends WebView implements OnLongClickListener, TextSelectionJavascriptInterfaceListener, View.OnTouchListener {
    /**
     * Context.
     */
    protected Context mContext;
    private String TAG = "TranslateWebView";
    /**
     * Javascript interface for catching text selection.
     */
    protected TextSelectionJavascriptInterface mTextSelectionJSInterface = null;
    private BaseWebTopBar webTopBar;
    private BaseWebBottomBar webBottomBar;
    private String lastURL = "";//用于判断是否已经注入
    private String lastURL1 = "";//用于只设置一遍颜色
    private OnWebViewLongClickListener onWebViewLongClickListener;

    public TranslateWebView(Context context) {
        super(context);
        mContext = context;
        init(context);
    }

    public TranslateWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init(context);

    }

    public TranslateWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }


    @Override
    public boolean onLongClick(View v) {
        WebView.HitTestResult result = ((WebView) v).getHitTestResult();
        int type = result.getType();
        if (type == WebView.HitTestResult.IMAGE_TYPE) {
            onWebViewLongClickListener.onImgLongClick(result.getExtra());
            return true;
        }
        Handler handler = new Handler();
        Runnable updateThread = new Runnable() {
            public void run() {
                //其实直接调用就好了
                loadUrl("javascript:longTouchSelected();");
            }

        };
        handler.postDelayed(updateThread, 0);
        // Tell the javascript to handle this if not in selection mode
//        loadUrl("javascript:android.selection.longTouch();");

        // Don't let the webview handle it
//        return true;
        //let the webview handle it
        return false;
    }


    /**
     * Setups up the web view.
     *
     * @param context
     */
    protected void init(Context context) {

        // On Touch Listener
        setOnLongClickListener(this);
        setOnTouchListener(this);

        // Webview init
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //插件状态
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setAllowFileAccess(true);// 允许通过网页上传文件
        webSettings.setBuiltInZoomControls(true);// 可缩放
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存


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
        setWebChromeClient(new MyWebChromeClient());

        //JavaScript回调接口
        mTextSelectionJSInterface = new TextSelectionJavascriptInterface(mContext, this);
        addJavascriptInterface(mTextSelectionJSInterface,
                mTextSelectionJSInterface.getInterfaceName());

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
                    goBack(); // goBack()表示返回WebView的上一页面
                    return true;
                } else {
                    return false;
                }
            }
        });

        //隐藏缩放按钮
        getSettings().setDisplayZoomControls(false);
    }

    private void JSinject() {
        //以下是JavaScript注入的代码 目前是以直接注入text的方式注入的 也就是说是直接把方法以字符串的方式注入进去的,所以assets里的文件没用
        String js = "var newscript = document.createElement(\"script\");";
//                js += "newscript.src=\"file:///android_asset/android.selection.js\";";
//                js += "newscript.onload=function(){android.selection.longTouch();};";  //xxx()代表js中某方法
        js += "newscript.text =  function longTouchSelected(){           if(window.getSelection) {\n" +
                "        \t\t window.TextSelection.seletedWord(window.getSelection().toString());\n" +
                "            } else if(document.selection && document.selection.createRange) {\n" +
                "           \t\t  window.TextSelection.seletedWord(document.selection.createRange().text());\n" +
                "            }};";
        js += "document.body.appendChild(newscript);";
        loadUrl("javascript:" + js);
        //TODO 提示用户注入完成
//        ToastUtil.tipShort(mContext, "注入完成");
        webTopBar.setTitleTextColor(getResources().getColor(R.color.top_bar));
    }

    @Override
    public void tsjiJSError(String error) {
        Log.e(TAG, "JSError: " + error);
    }

    private void refresh() {
        lastURL = "";
        lastURL1 = "";
        webTopBar.setTitleTextColor(getResources().getColor(R.color.text_color));
        reload();
    }

    public void setWebTopBar(BaseWebTopBar webTopBar) {
        this.webTopBar = webTopBar;
        this.webTopBar.setOnWebTopBarRefreshClickListener(new OnWebTopBarRefreshClickListener() {
            @Override
            public void onRefreshClick() {
                refresh();
            }
        });
        this.webTopBar.setOnWebTopBarSkipToURLListener(new OnWebTopBarSkipToURLListener() {
            @Override
            public void skipToURL(String url) {
                if (!url.contains("http://") && !url.contains("https://")) {
                    loadUrl("http://" + url + "/");
                } else {

                    loadUrl(url);
                }
            }
        });
    }

    public void setWebBottomBar(final WebBottomBar webBottomBar) {
        this.webBottomBar = webBottomBar;
        this.webBottomBar.setOnWebBottomBarClickListener(new OnWebBottomBarClickListener() {
            @Override
            public void onBackwardClick() {
                goBack(); // goBack()表示返回WebView的上一页面
                if (canGoBack()) {
                    webBottomBar.toggleBackwardState(true);
                } else {
                    webBottomBar.toggleBackwardState(false);
                }
            }

            @Override
            public void onForwardClick() {
                goForward();
                if (canGoForward()) {
                    webBottomBar.toggleForwardState(true);
                } else {
                    webBottomBar.toggleForwardState(false);
                }
            }

            @Override
            public void onRefreshClick() {
                refresh();
            }

            @Override
            public void onTextOnlyClick() {
                Intent intent = new Intent(mContext, ReadTextOnlyActivity.class);
                intent.putExtra("url", getUrl());
                mContext.startActivity(intent);
            }
        });
    }

    public void setTextSelectionListener(TextSelectionListener textSelectionListener) {
        mTextSelectionJSInterface.setTextSelectionListener(textSelectionListener);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null != webTopBar) {
            webTopBar.toggleEditAndShow(false);
        }
        return false;
    }

    public void setOnWebViewLongClickListener(OnWebViewLongClickListener onWebViewLongClickListener) {
        this.onWebViewLongClickListener = onWebViewLongClickListener;
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
//            Toast.makeText(mContext, message,
//                    Toast.LENGTH_LONG).show();
//            loadUrl("javascript: android.selection.clearSelection();");
            return true;
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            // 得到读取进度
            super.onProgressChanged(view, newProgress);
            Log.i("ts", "读取进度" + newProgress);
            if (null != webTopBar) {
                webTopBar.setProgress(newProgress);
            }

            if (!TextUtils.isEmpty(getUrl()) && !getUrl().equals(lastURL) && newProgress > 40) {
                //不用等全部读取完就可以注入了
                lastURL = getUrl();
                if (!BlackListUtil.isBlackList(getUrl())) {
                    JSinject();
                }
            }
            if (!TextUtils.isEmpty(getUrl()) && !getUrl().equals(lastURL1) && newProgress <= 40) {
                //告诉用户已经注入完成
                lastURL1 = getUrl();
                webTopBar.setTitleTextColor(getResources().getColor(R.color.text_color));
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (null != webTopBar) {
                webTopBar.setTitle(title);
                webTopBar.setPath(getUrl());
            }
        }
    }

    public interface OnWebViewLongClickListener {
        void onImgLongClick(String imgUrl);
    }
}
