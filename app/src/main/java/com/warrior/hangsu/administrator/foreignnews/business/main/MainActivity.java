package com.warrior.hangsu.administrator.foreignnews.business.main;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseMultiTabActivity;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.business.ad.AdvertisingActivity;
import com.warrior.hangsu.administrator.foreignnews.business.collect.CollectedActivity;
import com.warrior.hangsu.administrator.foreignnews.business.login.LoginActivity;
import com.warrior.hangsu.administrator.foreignnews.business.other.AboutActivity;
import com.warrior.hangsu.administrator.foreignnews.business.read.ReadTextOnlyActivity;
import com.warrior.hangsu.administrator.foreignnews.business.web.TranslateWebFragment;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.eventbus.EventBusEvent;
import com.warrior.hangsu.administrator.foreignnews.listener.OnCopyClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReceivedWebInfoListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarHomeClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarWebNumClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarRefreshClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarSkipToURLListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.utils.BaseParameterUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.FileUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebSubTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.DownloadDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaEditDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.OnlyEditDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.QrDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseMultiTabActivity
        implements View.OnClickListener,
        EasyPermissions.PermissionCallbacks, View.OnTouchListener {
    private TranslateWebFragment currentWebFragment;
    private WebTopBar webTopBar;
    private WebBottomBar webBottomBar;
    private MangaDialog dialog;
    private ClipboardManager clip;//复制文本用
    //版本更新
    private String versionName, msg;
    private int versionCode;
    private boolean forceUpdate;
    private AVFile downloadFile, qrCodeFile;
    private MangaDialog versionDialog;
    private DownloadDialog downloadDialog;
    private String qrFilePath;
    private ImageView tranlateIv;
    private OnlyEditDialog searchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        doGetAnnouncement();
        doGetVersionInfo();
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
            baseToast.showToast("登录后就可以收藏网站了");
        }
        if (!SharedPreferencesUtils.getBooleanSharedPreferencesData(this, ShareKeys.CLOSE_TUTORIAL, false)) {
            MangaDialog dialog = new MangaDialog(this);
            dialog.show();
            dialog.setTitle("教程");
            dialog.setMessage("1,可长按单词翻译+\n" +
                    "2,也可以点击右下角图标翻译");
        }
    }

    @Override
    protected void initFragment() {
        currentWebFragment = new TranslateWebFragment();
        currentWebFragment.setOnReceivedWebInfoListener(new OnReceivedWebInfoListener() {
            @Override
            public void onReceivedTitle(String title) {
                if (null != webTopBar) {
                    webTopBar.setTitle(title);
                    webTopBar.setPath(currentWebFragment.getUrl());
                }
            }

            @Override
            public void onProgress(int progress) {
                if (null != webTopBar) {
                    webTopBar.setProgress(progress);
                }
            }
        });
    }

    @Override
    protected String getActivityTitle() {
        return "";
    }

    @Override
    protected int getPageCount() {
        return 1;
    }

    @Override
    protected ViewPager.OnPageChangeListener getPageListener() {
        return null;
    }

    @Override
    protected String[] getTabTitleList() {
        String[] titleList = new String[1];
        titleList[0] = "test";
        return titleList;
    }

    @Override
    protected Fragment getFragmentByPosition(int position) {
        switch (position) {
            case 0:
                return currentWebFragment;
            default:
                return currentWebFragment;
        }
    }


    @Override
    protected void initUI() {
        super.initUI();
        hideBaseTopBar();
        tabLayout.setVisibility(View.GONE);
        tranlateIv = (ImageView) findViewById(R.id.translate_iv);
        webTopBar = (WebTopBar) findViewById(R.id.top_bar);
        webTopBar.setOnWebTopClickListener(new OnWebTopClickListener() {
            @Override
            public void onTitleClick() {
                showWebSubTopBar();
            }
        });
        webTopBar.setOnWebTopBarRefreshClickListener(new OnWebTopBarRefreshClickListener() {
            @Override
            public void onRefreshClick() {
                currentWebFragment.doGetData();
            }
        });
        webTopBar.setOnWebTopBarSkipToURLListener(new OnWebTopBarSkipToURLListener() {
            @Override
            public void skipToURL(String url) {
                if (!url.contains("http://") && !url.contains("https://")) {
                    currentWebFragment.loadUrl("http://" + url + "/");
                } else {
                    currentWebFragment.loadUrl(url);
                }
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
                currentWebFragment.loadUrl
                        (SharedPreferencesUtils.getSharedPreferencesData
                                (MainActivity.this, ShareKeys.MAIN_URL, Globle.DEFAULT_MAIN_URL));
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
                Intent intent = new Intent(MainActivity.this, CollectedActivity.class);
                startActivityForResult(intent, 33);
            }

            @Override
            public void onShareClick() {
            }

            @Override
            public void onLoginClick() {
                if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    baseToast.showToast("你好!" + LoginBean.getInstance().getUserName() + ".");
                }
            }

            @Override
            public void onOptionsClick() {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }

            @Override
            public void onMangaClick() {
                Intent intent = new Intent(MainActivity.this, AdvertisingActivity.class);
                startActivity(intent);
            }

            @Override
            public void onShareAppClick() {
                showQrDialog();
            }
        });
        webBottomBar.setOnWebBottomBarClickListener(new OnWebBottomBarClickListener() {
            @Override
            public void onBackwardClick() {
                currentWebFragment.goBack(); // goBack()表示返回WebView的上一页面
                if (currentWebFragment.canGoBack()) {
                    webBottomBar.toggleBackwardState(true);
                } else {
                    webBottomBar.toggleBackwardState(false);
                }
            }

            @Override
            public void onForwardClick() {
                currentWebFragment.goForward();
                if (currentWebFragment.canGoForward()) {
                    webBottomBar.toggleForwardState(true);
                } else {
                    webBottomBar.toggleForwardState(false);
                }
            }

            @Override
            public void onRefreshClick() {
                currentWebFragment.doGetData();
            }

            @Override
            public void onTextOnlyClick() {
                Intent intent = new Intent(MainActivity.this, ReadTextOnlyActivity.class);
                intent.putExtra("url", currentWebFragment.getUrl());
                intent.putExtra("title", currentWebFragment.getTitle());
                startActivity(intent);
            }
        });

        tranlateIv.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_webview;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 33 && null != data) {
            String url = data.getStringExtra("url");
            currentWebFragment.loadUrl(url);
        }
    }

    @Override
    public void onEventMainThread(final EventBusEvent event) {
        if (event.getEventType() == EventBusEvent.COPY_BOARD_URL_EVENT || event.getEventType() == EventBusEvent.COPY_BOARD_TEXT_EVENT) {
            switch (event.getEventType()) {
                case EventBusEvent.COPY_BOARD_URL_EVENT:
                    showBaseDialog("检测到你复制了某个网址，是否跳转到详情页？", "", "是", "否",
                            new MangaDialog.OnPeanutDialogClickListener() {
                                @Override
                                public void onOkClick() {
                                    currentWebFragment.loadUrl(event.getMsg());
                                }

                                @Override
                                public void onCancelClick() {

                                }
                            });
                    break;
                case EventBusEvent.COPY_BOARD_TEXT_EVENT:
                    showSaveDialog(event.getMsg());
                    break;
            }
        } else {
            super.onEventMainThread(event);
        }
    }

    @AfterPermissionGranted(111)
    private void showSaveDialog(final String content) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            // Already have permission, do the thing
            // ...
            MangaEditDialog dialog = new MangaEditDialog(this);
            dialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    File file = new File(Globle.DOWNLOAD_PATH);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    try {
                        FileWriter fw = new FileWriter(Globle.DOWNLOAD_PATH + File.separator
                                + text + ".txt", true);
                        fw.write(content);
                        fw.close();
                        baseToast.showToast("保存成功!\n已保存至" + Globle.DOWNLOAD_PATH + "文件夹");
                        // 上传错误信息到服务器
//                uploadToServer(crashInfo);
                    } catch (IOException e) {
                        baseToast.showToast(e + "");
                    }
                }

                @Override
                public void onCancelClick() {

                }
            });
            dialog.show();
            dialog.setTitle("检测到你复制了一段文本,是否将其转换为TXT文件?");
            dialog.setHint("请输入文本标题");
//        dialog.setEditTextContent(StringUtils.replaceAllSpecialCharacterTo(title, "_"));
            dialog.setOkText("确定");
            dialog.setCancelText("取消");
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void doCollect() {
        String userName = LoginBean.getInstance().getUserName(this);
        if (TextUtils.isEmpty(userName)) {
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(MainActivity.this);
        AVObject object = new AVObject("Collect");
        object.put("owner", userName);
        object.put("collect_title", currentWebFragment.getTitle());
        object.put("collect_url", currentWebFragment.getUrl());
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
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
                if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
                    if (null != list && list.size() > 0) {
                        String title = list.get(0).getString("title");
                        String message = list.get(0).getString("message");
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        String date = df.format(new Date());
                        if (!date.equals(SharedPreferencesUtils.getSharedPreferencesData(
                                MainActivity.this, ShareKeys.ANNOUNCEMENT_READ_KEY))) {
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
                if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
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
                        if (BaseParameterUtil.getInstance(MainActivity.this).
                                getAppVersionCode() >= versionCode || SharedPreferencesUtils.
                                getBooleanSharedPreferencesData(MainActivity.this,
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
                    if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
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


    private void showQrDialog() {
        QrDialog qrDialog = new QrDialog(this);
        qrDialog.show();
        qrDialog.setImg("file://" + qrFilePath);
    }


    private void showWebSubTopBar() {
        WebSubTopBar webSubTopBar = new WebSubTopBar(this);
        webSubTopBar.setOnCopyClickListener(new OnCopyClickListener() {
            @Override
            public void onCopy() {
                webTopBar.toggleEditAndShow(false);
                clip.setText(currentWebFragment.getUrl());
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
                        (MainActivity.this, ShareKeys.ANNOUNCEMENT_READ_KEY, date);
            }

            @Override
            public void onCancelClick() {

            }
        });
        if (MainActivity.this.isFinishing()) {
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
            versionDialog = new MangaDialog(MainActivity.this);
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
                        SharedPreferencesUtils.setSharedPreferencesData(MainActivity.this,
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
                    if (LeanCloundUtil.handleLeanResult(MainActivity.this, e)) {
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

    private void showTranslateDialog() {
        if (null == searchDialog) {
            searchDialog = new OnlyEditDialog(this);
            searchDialog.setOnEditResultListener(new OnEditResultListener() {
                @Override
                public void onResult(String text) {
                    currentWebFragment.translation(text);
                }

                @Override
                public void onCancelClick() {

                }
            });
            searchDialog.setCancelable(true);
        }
        searchDialog.show();
        searchDialog.clearEdit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.translate_iv:
                showTranslateDialog();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        showLogoutDialog();
    }


    private void showLogoutDialog() {
        MangaDialog logoutDialog = new MangaDialog(MainActivity.this);
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
    public boolean onTouch(View v, MotionEvent event) {
        if (null != webTopBar) {
            webTopBar.toggleEditAndShow(false);
        }
        return false;
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
            MangaDialog peanutDialog = new MangaDialog(MainActivity.this);
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
