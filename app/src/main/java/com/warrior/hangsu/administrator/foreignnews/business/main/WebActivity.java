package com.warrior.hangsu.administrator.foreignnews.business.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.business.collect.CollectedActivity;
import com.warrior.hangsu.administrator.foreignnews.db.DbAdapter;
import com.warrior.hangsu.administrator.foreignnews.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebTopBar;
import com.warrior.hangsu.administrator.foreignnews.utils.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.utils.DownLoadUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.Globle;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.widget.webview.TextSelectionListener;
import com.warrior.hangsu.administrator.foreignnews.widget.webview.TranslateWebView;

import java.util.HashMap;
import java.util.Map;

public class WebActivity extends BaseActivity
        implements View.OnClickListener {
    private TranslateWebView translateWebView;
    private WebTopBar webTopBar;
    private WebBottomBar webBottomBar;
    private AlertDialog dialog;
    private ClipboardManager clip;//复制文本用
    private DbAdapter db;
    private UMImage image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        db = new DbAdapter(this);
        initUI();
        initDialog();
//        initUmeng();
        image = new UMImage(WebActivity.this, R.drawable.icon_garbage);//资源文件
    }

    private void initUmeng() {
        UMShareAPI mShareAPI = UMShareAPI.get(WebActivity.this);
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText(getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText(getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
            }
        };

        mShareAPI.doOauthVerify(WebActivity.this, SHARE_MEDIA.QQ, umAuthListener);
        mShareAPI.doOauthVerify(WebActivity.this, SHARE_MEDIA.WEIXIN, umAuthListener);
    }

    private void initUI() {
        translateWebView = (TranslateWebView) findViewById(R.id.translate_webview);
        webTopBar = (WebTopBar) findViewById(R.id.top_bar);
        webBottomBar = (WebBottomBar) findViewById(R.id.bottom_bar);
        webBottomBar.setOnWebBottomBarLogoutClickListener(this);
        webBottomBar.setOnWebBottomBarHomeClickListener(new WebBottomBar.OnWebBottomBarHomeClickListener() {
            @Override
            public void onHomeClick() {
                translateWebView.loadUrl(Globle.firstPageURL);
            }
        });
        webBottomBar.setOnWebBottomBarOptionsClickListener(new WebBottomBar.OnWebBottomBarOptionsClickListener() {
            @Override
            public void onCollectClick() {
                db.insertCollectTableTb(translateWebView.getTitle(), translateWebView.getUrl(), "");
                ToastUtil.tipShort(WebActivity.this, "添加到收藏成功");
            }

            @Override
            public void onCollectedClick() {
                Intent intent = new Intent(WebActivity.this, CollectedActivity.class);
                startActivityForResult(intent, 33);
            }

            @Override
            public void onShareClick() {
                new ShareAction(WebActivity.this).withTitle("来自垃圾浏览器的分享")
                        .withText(translateWebView.getTitle())
                        .withMedia(image)
                        .withTargetUrl(translateWebView.getUrl())
                        .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                        .setCallback(new UMShareListener() {
                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                ToastUtil.tipShort(WebActivity.this, "分享成功");
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                ToastUtil.tipShort(WebActivity.this, "分享失败");
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                ToastUtil.tipShort(WebActivity.this, "分享取消");
                            }
                        }).open();
            }
        });
        translateWebView.setWebTopBar(webTopBar);
        translateWebView.setWebBottomBar(webBottomBar);
        translateWebView.setTextSelectionListener(new TextSelectionListener() {
            @Override
            public void seletedWord(String word) {
                translation(word);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        translateWebView.clearFocus();
                    }
                }, 200);//n秒后执行Runnable中的run方法

            }
        });
        translateWebView.setOnWebViewLongClickListener(new TranslateWebView.OnWebViewLongClickListener() {
            @Override
            public void onImgLongClick(String imgUrl) {
                showSaveImgDialog(imgUrl);

            }
        });

        translateWebView.loadUrl(Globle.firstPageURL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33 && null != data) {
            String url = data.getStringExtra("url");
            translateWebView.loadUrl(url);
        }
    }

    private void initDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialog = builder.create();
        dialog.setCancelable(true);
    }

    private void translation(final String word) {
        clip.setText(word);
        //记录查过的单词
        if (Globle.closeQueryWord) {
            return;
        }
        String url = Globle.YOUDAO + word;
        HashMap<String, String> params = new HashMap<String, String>();
        VolleyCallBack<YoudaoResponse> callback = new VolleyCallBack<YoudaoResponse>() {

            @Override
            public void loadSucceed(YoudaoResponse result) {
                if (null != result && result.getErrorCode() == 0) {
                    YoudaoResponse.BasicBean item = result.getBasic();
                    String t = "";
                    if (null != item) {
                        for (int i = 0; i < item.getExplains().size(); i++) {
                            t = t + item.getExplains().get(i) + ";";
                        }
                        showOnlyOkDialog(word, result.getQuery() + " [" + item.getPhonetic() +
                                "]: " + "\n" + t);
                    } else {
                        ToastUtil.tipShort(WebActivity.this, "没查到该词");
                    }
                } else {
                    ToastUtil.tipShort(WebActivity.this, "网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(WebActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                WebActivity.this, url, params,
                YoudaoResponse.class, callback);
    }

    private void showOnlyOkDialog(String title, String msg) {
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.show();
    }

    private void showSaveImgDialog(final String imgUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否保存图片");
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DownLoadUtil.downloadImg(WebActivity.this, imgUrl);
                ToastUtil.tipShort(WebActivity.this, "如果成功保存了,那就会保存在\n" + "garbage/img文件夹中");
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.closeDb();
        translateWebView.clearCache(true);
    }
}
