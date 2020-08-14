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
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.warrior.hangsu.administrator.foreignnews.business.read.ReadTextOnlyActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReceivedWebInfoListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarRefreshClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarSkipToURLListener;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.StringUtils;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.BaseWebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.BaseWebTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.toast.EasyToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Webview subclass that hijacks web content selection.
 *
 * @author Brandon Tate
 */
public class TranslateWebView extends MyWebView implements OnLongClickListener, View.OnClickListener {
    private String TAG = "TranslateWebView";
    /**
     * Javascript interface for catching text selection.
     */
    protected TextSelectionJavascriptInterface mTextSelectionJSInterface = null;
    private OnWebViewLongClickListener onWebViewLongClickListener;
    private String urlTitle;
    private EasyToast baseToast;
    private TextSelectionListener mSelectionListener;
    private String currentInjectedUrl = "";
    private OnReceivedWebInfoListener mOnReceivedWebInfoListener;

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
        if (type == WebView.HitTestResult.IMAGE_TYPE || type == HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
            onWebViewLongClickListener.onImgLongClick(result.getExtra());
            return true;
        }
        if (type == HitTestResult.SRC_ANCHOR_TYPE) {
            WebView webView = new WebView(mContext);
            webView.loadUrl(result.getExtra());
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);
                    if (null != mSelectionListener) {
                        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                                (mContext, ShareKeys.CLOSE_TRANSLATE, false)) {
                            mSelectionListener.seletedWord(title);
                        } else {
                            try {
                                String res = StringUtils.replaceAllSpecialCharacterTo(title, "分隔符");
                                String[] reses = res.split("分隔符");
                                ArrayList<String> list = new ArrayList<>();
                                for (String item : reses) {
                                    if (!TextUtils.isEmpty(item.replaceAll(" ", ""))&&!item.equals("9GAG")) {
                                        list.add(item.replaceAll(" ", ""));
                                    }
                                }
                                String[] finalRes = new String[list.size()];
                                for (int i = 0; i < list.size(); i++) {
                                    finalRes[i] = list.get(i);
                                }
                                mSelectionListener.selectedWord(finalRes);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            return true;
        }
        if (type == HitTestResult.UNKNOWN_TYPE) {
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
        }
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
//                if (getUrl() != null && url != null && url.equals(getUrl())) {
//                    goBack();
//                    return true;
//                }
//                loadUrl(url);
//                return false;
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
            public void selectedWord(String[] words) {
                if (null != mSelectionListener) {
                    mSelectionListener.selectedWord(words);
                }
            }
        });
        addJavascriptInterface(mTextSelectionJSInterface,
                mTextSelectionJSInterface.getInterfaceName());

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //因为DOWN和UP都算回车 所以这样写 避免调用两次
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if ((keyCode == KeyEvent.KEYCODE_BACK) && canGoBack()) {
                                goBack(); // goBack()表示返回WebView的上一页面
                                return true;
                            } else {
                                return false;
                            }
                    }
                }
                return false;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:" + js, null);
            Log.i(TAG, "evaluateJavascript-javascript");
        } else {
            loadUrl("javascript:" + js);
            Log.i(TAG, "loadUrl-javascript");
        }

//        ToastUtil.tipShort(mContext, "注入完成");
        currentInjectedUrl = getUrl();
    }

//    private void clickJsInject() {
//        //以下是JavaScript注入的代码 目前是以直接注入text的方式注入的 也就是说是直接把方法以字符串的方式注入进去的,所以assets里的文件没用
//        String js = "var newscript = document.createElement(\"script\");";
////                js += "newscript.src=\"file:///android_asset/android.selection.js\";";
////                js += "newscript.onload=function(){android.selection.longTouch();};";  //xxx()代表js中某方法
//        js += "newscript.text =  function clickSelected(){  " +
////                "var str=window.getSelection().toString();     \nif(document.selection){\n" +
////                "                        str=document.selection.createRange().text;// IE\n" +
////                "                    }"+
//                "\twindow.TextSelection.clickWord(document.selection.createTextRange().text);};";
//        js += "document.body.appendChild(newscript);";
//        loadUrl("javascript:" + js);
////        ToastUtil.tipShort(mContext, "注入完成");
//    }

    private void refresh() {
        reload();
    }


    public void setOnWebViewLongClickListener(OnWebViewLongClickListener onWebViewLongClickListener) {
        this.onWebViewLongClickListener = onWebViewLongClickListener;
    }

    public void setSelectionListener(TextSelectionListener selectionListener) {
        mSelectionListener = selectionListener;
    }

    public void setOnReceivedWebInfoListener(OnReceivedWebInfoListener onReceivedWebInfoListener) {
        mOnReceivedWebInfoListener = onReceivedWebInfoListener;
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
            if (null != mOnReceivedWebInfoListener) {
                mOnReceivedWebInfoListener.onProgress(newProgress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            urlTitle = title;
            if (null != mOnReceivedWebInfoListener) {
                mOnReceivedWebInfoListener.onReceivedTitle(title);
            }
            if (null != onPeanutWebViewListener) {
                onPeanutWebViewListener.onReceivedTitle(getUrl(), title);
            }
        }
    }

    public interface OnWebViewLongClickListener {
        void onImgLongClick(String imgUrl);
    }
}
