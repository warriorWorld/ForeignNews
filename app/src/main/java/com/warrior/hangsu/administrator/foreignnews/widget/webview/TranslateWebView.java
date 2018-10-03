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

import android.content.ComponentName;
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
import com.warrior.hangsu.administrator.foreignnews.widget.toast.EasyToast;

import java.util.HashMap;

/**
 * Webview subclass that hijacks web content selection.
 *
 * @author Brandon Tate
 */
public class TranslateWebView extends MyWebView implements OnLongClickListener, View.OnTouchListener, View.OnClickListener {
    private String TAG = "TranslateWebView";
    /**
     * Javascript interface for catching text selection.
     */
    protected TextSelectionJavascriptInterface mTextSelectionJSInterface = null;
    private BaseWebTopBar webTopBar;
    private BaseWebBottomBar webBottomBar;
    private OnWebViewLongClickListener onWebViewLongClickListener;
    private String urlTitle;
    private EasyToast baseToast;
    private TextSelectionListener mSelectionListener;
    private String currentInjectedUrl = "";

    public TranslateWebView(Context context) {
        super(context);
    }

    public TranslateWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TranslateWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onLongClick(View v) {
        WebView.HitTestResult result = ((WebView) v).getHitTestResult();
        int type = result.getType();
        if (type == WebView.HitTestResult.IMAGE_TYPE) {
            onWebViewLongClickListener.onImgLongClick(result.getExtra());
            return true;
        }
        if (!currentInjectedUrl.equals(getUrl())) {
            JSinject();
        }
        Handler handler = new Handler();
        Runnable updateThread = new Runnable() {
            public void run() {
                loadUrl("javascript:longTouchSelected();");
            }
        };
        handler.postDelayed(updateThread, 0);
        return false;
    }

    @Override
    public void onClick(View v) {

    }


    /**
     * Setups up the web view.
     *
     * @param context
     */
    @Override
    protected void init(Context context) {
        super.init(context);
        baseToast = new EasyToast(context);

        // On Touch Listener
        setOnLongClickListener(this);
        setOnClickListener(this);
        setOnTouchListener(this);

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
                JSinject();
                super.onPageFinished(view, url);
            }
        });

        //JavaScript回调接口
        mTextSelectionJSInterface = new TextSelectionJavascriptInterface(mContext, new TextSelectionListener() {
            @Override
            public void seletedWord(String word) {
                if (null != mSelectionListener) {
                    mSelectionListener.seletedWord(word);
                }
            }

            @Override
            public void clickWord(String word) {
                if (null != mSelectionListener) {
                    mSelectionListener.seletedWord(word);
                }
            }
        });
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
        setWebChromeClient(new MyWebChromeClient());
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
//        ToastUtil.tipShort(mContext, "注入完成");
        currentInjectedUrl = getUrl();
    }

    private void clickJsInject() {
        //以下是JavaScript注入的代码 目前是以直接注入text的方式注入的 也就是说是直接把方法以字符串的方式注入进去的,所以assets里的文件没用
        String js = "var newscript = document.createElement(\"script\");";
//                js += "newscript.src=\"file:///android_asset/android.selection.js\";";
//                js += "newscript.onload=function(){android.selection.longTouch();};";  //xxx()代表js中某方法
        js += "newscript.text =  function clickSelected(){  "+
//                "var str=window.getSelection().toString();     \nif(document.selection){\n" +
//                "                        str=document.selection.createRange().text;// IE\n" +
//                "                    }"+
                "\twindow.TextSelection.clickWord(\"test\");};";
        js += "document.body.appendChild(newscript);";
        loadUrl("javascript:" + js);
//        ToastUtil.tipShort(mContext, "注入完成");
    }

    private void refresh() {
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
                intent.putExtra("title", urlTitle);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (null != webTopBar) {
            webTopBar.toggleEditAndShow(false);
        }
//        clickJsInject();
//        Handler handler = new Handler();
//        Runnable updateThread = new Runnable() {
//            public void run() {
//                loadUrl("javascript:clickSelected();");
//            }
//        };
//        handler.postDelayed(updateThread, 0);
        return false;
    }

    public void setOnWebViewLongClickListener(OnWebViewLongClickListener onWebViewLongClickListener) {
        this.onWebViewLongClickListener = onWebViewLongClickListener;
    }

    public void setSelectionListener(TextSelectionListener selectionListener) {
        mSelectionListener = selectionListener;
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
            baseToast.showToast(message);
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
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            urlTitle = title;
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
