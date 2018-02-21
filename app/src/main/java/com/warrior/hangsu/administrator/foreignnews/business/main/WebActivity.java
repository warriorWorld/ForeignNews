package com.warrior.hangsu.administrator.foreignnews.business.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.foreignnews.business.ad.AdvertisingActivity;
import com.warrior.hangsu.administrator.foreignnews.business.collect.CollectedActivity;
import com.warrior.hangsu.administrator.foreignnews.business.login.LoginActivity;
import com.warrior.hangsu.administrator.foreignnews.business.other.AboutActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnCopyClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarHomeClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarWebNumClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.utils.BaseParameterUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.DownLoadUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.FileUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebSubTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.DownloadDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.QrDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.foreignnews.widget.webview.TextSelectionListener;
import com.warrior.hangsu.administrator.foreignnews.widget.webview.TranslateWebView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class WebActivity extends BaseActivity
        implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks {
    private TranslateWebView translateWebView;
    private WebTopBar webTopBar;
    private WebBottomBar webBottomBar;
    private MangaDialog dialog;
    private ClipboardManager clip;//复制文本用
    private UMImage image;
    //版本更新
    private String versionName, msg;
    private int versionCode;
    private boolean forceUpdate;
    private AVFile downloadFile,qrCodeFile;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;
    private String qrFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
//        initUmeng();
        image = new UMImage(WebActivity.this, R.drawable.icon_garbage);//资源文件
//        openYoudao();
        doGetAnnouncement();
        doGetVersionInfo();
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            baseToast.showToast("登录后就可以收藏网站了");
        }
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, false)) {
            MangaDialog dialog = new MangaDialog(this);
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,当顶部网页标题颜色变为蓝色后,可通过长按单词翻译+\n" +
                    "2,可在设置中关闭教程");
        }
    }

    private void initUI() {
        hideBaseTopBar();
        translateWebView = (TranslateWebView) findViewById(R.id.translate_webview);
        webTopBar = (WebTopBar) findViewById(R.id.top_bar);
        webTopBar.setOnWebTopClickListener(new OnWebTopClickListener() {
            @Override
            public void onTitleClick() {
                showWebSubTopBar();
            }
        });
        webBottomBar = (WebBottomBar) findViewById(R.id.bottom_bar);
        webBottomBar.setOnWebBottomBarLogoutClickListener(new OnWebBottomBarLogoutClickListener() {
            @Override
            public void onLogoutClick() {
                ActivityPoor.finishAllActivity();
            }
        });
        webBottomBar.setOnWebBottomBarHomeClickListener(new OnWebBottomBarHomeClickListener() {
            @Override
            public void onHomeClick() {
                translateWebView.loadUrl
                        (SharedPreferencesUtils.getSharedPreferencesData
                                (WebActivity.this, ShareKeys.MAIN_URL, Globle.DEFAULT_MAIN_URL));
            }
        });
        webBottomBar.setOnWebBottomBarWebNumClickListener(new OnWebBottomBarWebNumClickListener() {
            @Override
            public void onWebNumClick() {
                baseToast.showToast("待开发");
            }
        });
        webBottomBar.setOnWebBottomBarOptionsClickListener(new OnWebBottomBarOptionsClickListener() {
            @Override
            public void onCollectClick() {
                doCollect();
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

            @Override
            public void onLoginClick() {
                if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
                    Intent intent = new Intent(WebActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    baseToast.showToast("你好!" + LoginBean.getInstance().getUserName() + ".");
                }
            }

            @Override
            public void onOptionsClick() {
                Intent intent = new Intent(WebActivity.this, AboutActivity.class);
                startActivity(intent);
            }

            @Override
            public void onMangaClick() {
                Intent intent = new Intent(WebActivity.this, AdvertisingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onShareAppClick() {
                showQrDialog();
            }
        });
        translateWebView.setWebTopBar(webTopBar);
        translateWebView.setWebBottomBar(webBottomBar);
        translateWebView.setTextSelectionListener(new TextSelectionListener() {
            @Override
            public void seletedWord(String word) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        translateWebView.clearFocus();
                    }
                }, 150);//n秒后执行Runnable中的run方法
                translation(word);
            }
        });
        translateWebView.setOnWebViewLongClickListener(new TranslateWebView.OnWebViewLongClickListener() {
            @Override
            public void onImgLongClick(String imgUrl) {
                showSaveImgDialog(imgUrl);

            }
        });

        translateWebView.loadUrl
                (SharedPreferencesUtils.getSharedPreferencesData
                        (this, ShareKeys.MAIN_URL, Globle.DEFAULT_MAIN_URL));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
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

    private void doCollect() {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(WebActivity.this);
        AVObject object = new AVObject("Collect");
        object.put("owner", userName);
        object.put("collect_title", translateWebView.getTitle());
        object.put("collect_url", translateWebView.getUrl());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(WebActivity.this, e)) {
                    baseToast.showToast("收藏成功");
                }
            }
        });
    }

    private void doGetAnnouncement() {
        AVQuery<AVObject> query = new AVQuery<>("Announcement");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(WebActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        String title = list.get(0).getString("title");
                        String message = list.get(0).getString("message");
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String date = df.format(new Date());
                        if (!date.equals(SharedPreferencesUtils.getSharedPreferencesData(
                                WebActivity.this, ShareKeys.ANNOUNCEMENT_READ_KEY))) {
                            showAnnouncementDialog(title, message);
                        }
                    }
                }
            }
        });
    }

    private void doGetVersionInfo() {
        AVQuery<AVObject> query = new AVQuery<>("VersionInfo");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (LeanCloundUtil.handleLeanResult(WebActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        versionName = list.get(0).getString("versionName");
                        versionCode = list.get(0).getInt("versionCode");
                        forceUpdate = list.get(0).getBoolean("forceUpdate");
                        msg = list.get(0).getString("description");
                        downloadFile = list.get(0).getAVFile("apk");
                        qrCodeFile = list.get(0).getAVFile("QRcode");
                        if (null != qrCodeFile) {
                            doDownloadQRcode();
                        }
                        if (BaseParameterUtil.getInstance(WebActivity.this).
                                getAppVersionCode() >= versionCode || SharedPreferencesUtils.
                                getBooleanSharedPreferencesData(WebActivity.this,
                                        ShareKeys.IGNORE_THIS_VERSION_KEY + versionName, false)) {
                        } else {
                            showVersionDialog();
                        }
                    }
                }
            }
        });
    }

    @AfterPermissionGranted(222)
    private void doDownloadQRcode() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            final String folderPath = Globle.DOWNLOAD_PATH;
            final File file = new File(folderPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            final String qrFileName = "QR" + versionName + ".png";
            qrFilePath = Globle.DOWNLOAD_PATH + "/" + qrFileName;
            final File qrFile = new File(qrFilePath);
            if (qrFile.exists()) {
                //有就不下了
                return;
            }
            qrCodeFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    // bytes 就是文件的数据流
                    if (LeanCloundUtil.handleLeanResult(WebActivity.this, e)) {
                        File apkFile = FileUtil.byte2File(bytes, folderPath, qrFileName);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                }
            });

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    222, perms);
        }
    }

    private void openYoudao() {
        // ComponentName（组件名称）是用来打开其他应用程序中的Activity或服务的
        Intent intent = new Intent();
//        ComponentName cmp = new ComponentName("com.youdao.dict", "com.youdao.dict.DictDemo");// 包名或者启动页是错的
        ComponentName cmp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI");// 报名该有activity
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);

        startActivity(intent);
    }


    private void translation(final String word) {
        clip.setText(word);
        //记录查过的单词
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (this, ShareKeys.CLOSE_TRANSLATE, false)) {
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
                        baseToast.showToast("没查到该词");
                    }
                } else {
                    baseToast.showToast("网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                baseToast.showToast("error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                WebActivity.this, url, params,
                YoudaoResponse.class, callback);
    }

    private void showQrDialog() {
        QrDialog qrDialog = new QrDialog(this);
        qrDialog.show();
        qrDialog.setImg("file://" + qrFilePath);
    }

    private void showOnlyOkDialog(String title, String msg) {
        if (null == dialog) {
            dialog = new MangaDialog(this);
        }
        dialog.show();
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setOkText("确定");
    }

    private void showSaveImgDialog(final String imgUrl) {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                DownLoadUtil.downloadImg(WebActivity.this, imgUrl);
                baseToast.showToast("如果成功保存了,那就会保存在\n" + "garbage/img文件夹中");

            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
        dialog.setTitle("是否保存图片");
        dialog.setOkText("是");
        dialog.setCancelText("否");
    }

    private void showWebSubTopBar() {
        WebSubTopBar webSubTopBar = new WebSubTopBar(this);
        webSubTopBar.setOnCopyClickListener(new OnCopyClickListener() {
            @Override
            public void onCopy() {
                webTopBar.toggleEditAndShow(false);
                clip.setText(translateWebView.getUrl());
                baseToast.showToast("已复制链接");
            }
        });
        webSubTopBar.show();
    }

    private void showAnnouncementDialog(String title, String msg) {
        MangaDialog dialog = new MangaDialog(this);
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                String date = df.format(new Date());
                SharedPreferencesUtils.setSharedPreferencesData
                        (WebActivity.this, ShareKeys.ANNOUNCEMENT_READ_KEY, date);
            }

            @Override
            public void onCancelClick() {

            }
        });
        if (WebActivity.this.isFinishing()) {
            return;
        }
        dialog.show();
        dialog.setCancelable(false);
        dialog.setTitle(title);
        dialog.setMessage(msg);
        dialog.setOkText("知道了");
    }

    private void showVersionDialog() {
        if (null == versionDialog) {
            versionDialog = new MangaDialog(WebActivity.this);
            versionDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    versionDialog.dismiss();
                    doDownload();
                }

                @Override
                public void onCancelClick() {
                    if (forceUpdate) {
                        ActivityPoor.finishAllActivity();
                    } else {
                        SharedPreferencesUtils.setSharedPreferencesData(WebActivity.this,
                                ShareKeys.IGNORE_THIS_VERSION_KEY + versionName, true);
                        baseToast.showToast("忽略后可在'我的'页中点击'版本'按钮升级至最新版!", true);
                    }
                }
            });
        }
        versionDialog.show();

        versionDialog.setTitle("有新版本啦" + "v_" + versionName);
        versionDialog.setMessage(msg);
        versionDialog.setOkText("升级");
        versionDialog.setCancelable(false);

        if (!forceUpdate) {
            versionDialog.setCancelText("忽略");
        } else {
            versionDialog.setCancelText("退出");
        }
    }

    @AfterPermissionGranted(111)
    private void doDownload() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            final String filePath = Globle.DOWNLOAD_PATH + "/apk";
            final File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
            downloadFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, AVException e) {
                    // bytes 就是文件的数据流
                    if (null != downloadDialog && downloadDialog.isShowing()) {
                        downloadDialog.dismiss();
                    }
                    if (LeanCloundUtil.handleLeanResult(WebActivity.this, e)) {
                        File apkFile = FileUtil.byte2File(bytes, filePath, "english_browser.apk");

                        Intent intent = new Intent();
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("android.intent.action.VIEW");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                        startActivity(intent);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    // 下载进度数据，integer 介于 0 和 100。
                    downloadDialog.setProgress(integer);
                }
            });

        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(this);
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }


    private void showLogoutDialog() {
        MangaDialog logoutDialog = new MangaDialog(WebActivity.this);
        logoutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                ActivityPoor.finishAllActivity();
            }

            @Override
            public void onCancelClick() {

            }
        });
        logoutDialog.show();

        logoutDialog.setTitle("确定退出?");
        logoutDialog.setOkText("退出");
        logoutDialog.setCancelText("再逛逛");
        logoutDialog.setCancelable(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        translateWebView.clearCache(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
//        baseToast.showToast(getResources().getString(R.string.no_permissions), true);
        if (111 == requestCode) {
            MangaDialog peanutDialog = new MangaDialog(WebActivity.this);
            peanutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                    ActivityPoor.finishAllActivity();
                }

                @Override
                public void onCancelClick() {

                }
            });
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法更新App!可以授权后重试!");
        }
    }
}
