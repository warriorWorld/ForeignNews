package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.text.TextUtils;
import android.view.ViewGroup;

import com.warrior.hangsu.administrator.foreignnews.listener.OnUrlChangeListener;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

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
                if (url.contains("9gag.com")) {
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                doc = Jsoup.connect(url)
                                        .timeout(10000).get();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if (null != doc) {
                                Elements mangaListElements = doc.getElementsByClass("salt-container");
                                doc.removeClass("salt-container");
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        myWebView.loadUrl(doc.html());
                                    }
                                });
                            }
                        }
                    }.start();
                }
            }
        });
    }
}
