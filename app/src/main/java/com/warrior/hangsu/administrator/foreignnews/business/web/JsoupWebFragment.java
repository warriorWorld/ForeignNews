package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Administrator on 2018/10/4.
 */

public class JsoupWebFragment extends TranslateWebFragment {
    protected org.jsoup.nodes.Document doc;

    @Override
    protected void initFrgmentUI(ViewGroup view) {
        super.initFrgmentUI(view);
        myWebView.setOnPeanutWebViewListener(new MyWebView.OnPeanutWebViewListener() {
            @Override
            public void onReceivedTitle(final String url, String title) {
                removeAD(url);

                if (url.contains("9gag") && url.contains("comment")) {
                    Intent intent = new Intent(getActivity(), WebActivity.class);
                    intent.putExtra("url", url);
                    intent.putExtra("title","Comment");
                    startActivity(intent);
                    myWebView.goBack();
                }
            }
        });
    }

    public void removeAD(final String url) {
//        if (url.contains("boredpanda.com")) {
//            new Thread() {
//                @Override
//                public void run() {
//                    try {
//                        doc = Jsoup.connect(url)
//                                .timeout(10000).get();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    if (null != doc) {
//                        final Elements mangaListElements = doc.getElementsByClass("trending");
//                        doc.removeClass("trending");
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                baseToast.showToast(mangaListElements.size() + "");
//                                loadUrl(doc.html());
//                            }
//                        });
//                    }
//                }
//            }.start();
//        }
    }
}
