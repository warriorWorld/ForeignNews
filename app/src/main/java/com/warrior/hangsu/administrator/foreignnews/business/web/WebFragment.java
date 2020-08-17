package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseRefreshFragment;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnAllVersionScrollChangeListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnUrlChangeListener;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.TopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.DownloadDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaEditDialog;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/9/4.
 */
public class WebFragment extends BaseRefreshFragment implements
        EasyPermissions.PermissionCallbacks {
    protected TranslateWebView myWebView;
    private String url = "";
    protected ClipboardManager clip;//复制文本用
    private DownloadDialog downloadDialog;
    private String noRefreshUrl = "";
    private boolean hideTopLeft = false;
    private Handler mh = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreateAfterInitUI() {
        try {
            clip = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            if (getActivity() instanceof WebActivity) {
                Intent intent = getActivity().getIntent();
                url = intent.getStringExtra("url");
                if (TextUtils.isEmpty(url)) {
                    getActivity().finish();
                }

                myWebView.loadUrl(url);
            }
        } catch (Exception e) {
            //catch
            if (Globle.IS_TEST) {
                MangaDialog dialog = new MangaDialog(getActivity());
                dialog.show();
                dialog.setTitle(e + "");
            }
        }
    }

    @Override
    protected void onCreateBeforInitUI() {

    }

    @Override
    protected void initFrgmentUI(ViewGroup view) {
        myWebView = (TranslateWebView) view.findViewById(R.id.translate_webview);
        refreshBaseTopbar.setTitle("读取中");
        if (hideTopLeft) {
            refreshBaseTopbar.hideLeftButton();
        }
//        myWebView.setOnPeanutWebViewListener(new MyWebView.OnPeanutWebViewListener() {
//            @Override
//            public void onReceivedTitle(String title) {
//                if (!MatchStringUtil.isChinese(title)) {
//                    refreshBaseTopbar.setTitle(getResources().getString(R.string.app_name));
//                } else {
//                    refreshBaseTopbar.setTitle(title);
//                }
//
//            }
//        });
        myWebView.setOnUrlChangeListener(new OnUrlChangeListener() {
            @Override
            public void onUrlChange(String url) {
                if (!url.equals(noRefreshUrl)) {
                    baseSwipeLayout.setEnabled(true);
                }
                if (isLocalUrl()) {
                    mh.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myWebView.scrollTo(0, SharedPreferencesUtils.
                                    getIntSharedPreferencesData(getActivity(), getUrl() + ShareKeys.READ_PROGRESS));
                        }
                    }, 500);
                }
            }
        });
        myWebView.setOnWebViewLongClickListener(new MyWebView.OnWebViewLongClickListener() {
            @Override
            public void onImgLongClick(String imgUrl) {

            }
        });
        refreshBaseTopbar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                getActivity().finish();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                if (Globle.IS_TEST) {
                    MangaDialog dialog = new MangaDialog(getActivity());
                    dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                        @Override
                        public void onOkClick() {
                            clip.setText(myWebView.getUrl());
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                    dialog.show();
                    dialog.setTitle(myWebView.getUrl());
                    dialog.setOkText("复制地址");
                }
            }
        });
        refreshBaseTopbar.setTopBarLongClickLister(new TopBar.OnTopBarLongClickListener() {
            @Override
            public void onLeftLongClick() {

            }

            @Override
            public void onRightLongClick() {

            }

            @Override
            public void onTitleLongClick() {
                if (Globle.IS_TEST) {
                    MangaEditDialog dialog = new MangaEditDialog(getActivity());
                    dialog.setOnEditResultListener(new OnEditResultListener() {
                        @Override
                        public void onResult(String text) {
                            myWebView.loadUrl(text);
                        }

                        @Override
                        public void onCancelClick() {

                        }
                    });
                    dialog.show();
                }
            }
        });


        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(final String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                MangaDialog dialog = new MangaDialog(getActivity());
                dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                    @Override
                    public void onOkClick() {
                        //目前仅支持apk文件
                        downloadUrl = url;
                        doDownload();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
                dialog.show();
                dialog.setTitle("是否下载文件?");
                dialog.setOkText("是");
                dialog.setCancelText("否");
            }
        });
        myWebView.setOnAllVersionScrollChangeListener(new OnAllVersionScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == 0) {
                    baseSwipeLayout.setEnabled(true);
                } else {
                    baseSwipeLayout.setEnabled(false);
                }
            }
        });
        hideLoadMore();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveProgress();
        mh.removeCallbacksAndMessages(null);
    }

    private void saveProgress() {
        if (isLocalUrl()) {
            SharedPreferencesUtils.setSharedPreferencesData
                    (getActivity(), getUrl() + ShareKeys.READ_PROGRESS, myWebView.getScrollY());
        }
    }

    private boolean isLocalUrl() {
        return getUrl().contains("file://");
    }

    private String downloadUrl = "";

    @AfterPermissionGranted(333)
    private void doDownload() {
        //不需要处理拒绝后的回调 因为一亿元是强制更新 用户不更新就不让进应用
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(getActivity(), perms)) {
            // Already have permission, do the thing
            // ...
            showDownLoadDialog();
            // 下载apk，自动安装
            FinalHttp client = new FinalHttp();
            // url:下载的地址
            // target:保存的地址，包含文件的名称
            // callback 下载时的回调对象
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                client.download(downloadUrl, Environment.getExternalStorageDirectory() + "/other.apk",
                        new AjaxCallBack<File>() {

                            // 下载失败时回调这个方法
                            @Override
                            public void onFailure(Throwable t, String strMsg) {
                                super.onFailure(t, strMsg);
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                if (Globle.IS_TEST) {
                                    baseToast.showToast(t.getMessage() + "\n" + strMsg);
                                } else {
                                    baseToast.showToast("请检查你的网络");
                                }
                            }

                            // 下载时回调这个方法
                            // count ：下载文件需要的总时间，单位是毫秒
                            // current :当前进度,单位是毫秒
                            @Override
                            public void onLoading(long count, long current) {
                                super.onLoading(count, current);
                                String progress = current * 100 / count + "";
                                Integer integer = Integer.parseInt(progress);
                                downloadDialog.setProgress(integer);
                            }

                            // 下载成功时回调这个方法
                            @Override
                            public void onSuccess(File t) {
                                super.onSuccess(t);
                                if (null != downloadDialog && downloadDialog.isShowing()) {
                                    downloadDialog.dismiss();
                                }
                                baseToast.showToast("下载成功,文件保存在" + t.getPath());
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setAction("android.intent.action.VIEW");
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setDataAndType(Uri.fromFile(t), "application/vnd.android.package-archive");
                                startActivity(intent);
                            }
                        });
            }
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "需要文件读写权限才能下载文件",
                    333, perms);
        }
    }

    private void showDownLoadDialog() {
        if (null == downloadDialog) {
            downloadDialog = new DownloadDialog(getActivity());
        }
        downloadDialog.show();
        downloadDialog.setCancelable(false);
    }

    public String getUrl() {
        return myWebView.getUrl();
    }

    public String getTitle() {
        return myWebView.getTitle();
    }

    public void clearCache() {
        myWebView.clearCache(true);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCurrentUrl() {
        return myWebView.getUrl();
    }

    public void goBack() {
        saveProgress();
        myWebView.goBack();
    }

    public void goForward() {
        saveProgress();
        myWebView.goForward();
    }

    public boolean canGoBack() {
        return myWebView.canGoBack();
    }

    public boolean canGoForward() {
        return myWebView.canGoForward();
    }

    @Override
    public void doGetData() {
        try {
            if (TextUtils.isEmpty(myWebView.getUrl())) {
                myWebView.loadUrl(url);
            } else {
                myWebView.reload();
            }
            noMoreData();
        } catch (Exception e) {
            //有可能fragment还没加载完就调用所以可能空指针
            e.printStackTrace();
        }
    }

    public void loadUrl(String url) {
        if (null != myWebView) {
            myWebView.loadUrl(url);
        }
    }

    public void loadData(String data) {
        if (null != myWebView) {
            myWebView.loadData(data, "text/html", "utf-8");
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        //这个调用不能调用带参数的方法
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        baseToast.showToast("已获得授权,请继续!");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (requestCode == 333) {
            MangaDialog dialog = new MangaDialog(getActivity());
            dialog.show();
            dialog.setTitle("没有文件读写权限,无法下载!");
            dialog.setOkText("知道了");
        }
    }

    public void setHideTopLeft(boolean hideTopLeft) {
        this.hideTopLeft = hideTopLeft;
    }
}
