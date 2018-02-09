package com.warrior.hangsu.administrator.foreignnews;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.warrior.hangsu.administrator.foreignnews.bottombar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.collect.CollectedActivity;
import com.warrior.hangsu.administrator.foreignnews.db.DbAdapter;
import com.warrior.hangsu.administrator.foreignnews.read.YoudaoResponse;
import com.warrior.hangsu.administrator.foreignnews.topbar.WebTopBar;
import com.warrior.hangsu.administrator.foreignnews.utils.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.utils.DownLoadUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.Globle;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.webview.FindWebView;
import com.warrior.hangsu.administrator.foreignnews.webview.TextSelectionListener;
import com.warrior.hangsu.administrator.foreignnews.webview.TranslateWebView;

import java.util.HashMap;
import java.util.Map;

public class WebActivity2 extends BaseActivity
        implements View.OnClickListener {
    private FindWebView translateWebView;
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
        image = new UMImage(WebActivity2.this, R.drawable.icon_garbage);//资源文件
    }

    private void initUmeng() {
        UMShareAPI mShareAPI = UMShareAPI.get(WebActivity2.this);
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

        mShareAPI.doOauthVerify(WebActivity2.this, SHARE_MEDIA.QQ, umAuthListener);
        mShareAPI.doOauthVerify(WebActivity2.this, SHARE_MEDIA.WEIXIN, umAuthListener);
    }

    private void initUI() {
        translateWebView = (FindWebView) findViewById(R.id.translate_webview);
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
                ToastUtil.tipShort(WebActivity2.this, "添加到收藏成功");
            }

            @Override
            public void onCollectedClick() {
                Intent intent = new Intent(WebActivity2.this, CollectedActivity.class);
                startActivityForResult(intent, 33);
            }

            @Override
            public void onShareClick() {
                new ShareAction(WebActivity2.this).withTitle("来自垃圾浏览器的分享")
                        .withText(translateWebView.getTitle())
                        .withMedia(image)
                        .withTargetUrl(translateWebView.getUrl())
                        .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.WEIXIN)
                        .setCallback(new UMShareListener() {
                            @Override
                            public void onResult(SHARE_MEDIA share_media) {
                                ToastUtil.tipShort(WebActivity2.this, "分享成功");
                            }

                            @Override
                            public void onError(SHARE_MEDIA share_media, Throwable throwable) {
                                ToastUtil.tipShort(WebActivity2.this, "分享失败");
                            }

                            @Override
                            public void onCancel(SHARE_MEDIA share_media) {
                                ToastUtil.tipShort(WebActivity2.this, "分享取消");
                            }
                        }).open();
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
                        ToastUtil.tipShort(WebActivity2.this, "没查到该词");
                    }
                } else {
                    ToastUtil.tipShort(WebActivity2.this, "网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(WebActivity2.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                WebActivity2.this, url, params,
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
                DownLoadUtil.downloadImg(WebActivity2.this, imgUrl);
                ToastUtil.tipShort(WebActivity2.this, "如果成功保存了,那就会保存在\n" + "garbage/img文件夹中");
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
