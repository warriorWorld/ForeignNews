package com.warrior.hangsu.administrator.foreignnews.business.read;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.foreignnews.business.login.LoginActivity;
import com.warrior.hangsu.administrator.foreignnews.business.main.WebActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnUpFlipListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWordClickListener;
import com.warrior.hangsu.administrator.foreignnews.mannger.SettingManager;
import com.warrior.hangsu.administrator.foreignnews.mannger.ThemeManager;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.utils.ScreenUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.StringUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaEditDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.ReadDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人信息页
 */
public class ReadTextOnlyActivity extends BaseActivity implements
        EasyPermissions.PermissionCallbacks, TextToSpeech.OnInitListener {
    private String url;
    private static org.jsoup.nodes.Document doc;
    private String urlContent;
    private BaseReadView mPageWidget;
    private FrameLayout readWidgetFl;
    private ClipboardManager clip;//复制文本用
    private MangaDialog dialog;
    private ReadDialog readDialog;
    private String title;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        title = intent.getStringExtra("title");
        if (TextUtils.isEmpty(url)) {
            return;
        }
        clip = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initUI();
        initPagerWidget();
        doGetData();
        initTTS();
    }

    private void initTTS() {
        tts = new TextToSpeech(this, this); // 参数Context,TextToSpeech.OnInitListener
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_text;
    }

    private void initUI() {
        readWidgetFl = (FrameLayout) findViewById(R.id.read_widget_fl);
        hideBaseTopBar();
    }

    private void doGetData() {
        SingleLoadBarUtil.getInstance().showLoadBar(this);
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
                    Elements test = doc.select("p");
                    for (int i = 0; i < test.size(); i++) {
                        urlContent += test.get(i).text() + "\n";
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initPagerWidget();
                        }
                    });
                }
            }
        }.start();
    }

    private void initPagerWidget() {
        mPageWidget = new OverlappedWidget(this, new ReadListener());

        if (SharedPreferencesUtil.getInstance().getBoolean(ShareKeys.ISNIGHT, false)) {
            mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_night),
                    getResources().getColor((R.color.chapter_title_night)));
        }
        mPageWidget.setOnWordClickListener(new OnWordClickListener() {
            @Override
            public void onWordClick(String word) {
                if (StringUtils.isWord(word)) {
                    translation(word);
                }
            }
        });
        mPageWidget.setOnUpFlipListener(new OnUpFlipListener() {
            @Override
            public void onUpFlip() {
                showReadDialog();
            }
        });
        readWidgetFl.removeAllViews();
        readWidgetFl.addView(mPageWidget);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                /**
                 *要执行的操作
                 */
                mPageWidget.init(SettingManager.getInstance().getReadTheme(), title, urlContent);
                toggleDayNight();
            }
        }, 500);//n秒后执行Runnable中的run方法
    }

    private void translation(final String word) {
        clip.setText(word);
        text2Speech(word);
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
                        ToastUtil.tipShort(ReadTextOnlyActivity.this, "没查到该词");
                    }
                } else {
                    ToastUtil.tipShort(ReadTextOnlyActivity.this, "网络连接失败");
                }
            }

            @Override
            public void loadFailed(VolleyError error) {
                ToastUtil.tipShort(ReadTextOnlyActivity.this, "error" + error);
            }
        };
        VolleyTool.getInstance(this).requestData(Request.Method.GET,
                ReadTextOnlyActivity.this, url, params,
                YoudaoResponse.class, callback);

    }

    private void text2Speech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.setPitch(0.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            tts.speak(text,
                    TextToSpeech.QUEUE_FLUSH, null);
        }
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

    private void showReadDialog() {
        if (null == readDialog) {
            readDialog = new ReadDialog(this);
            readDialog.setOnReadDialogClickListener(new OnReadDialogClickListener() {
                @Override
                public void onSunMoonToggleClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (ReadTextOnlyActivity.this, ShareKeys.ISNIGHT,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(ReadTextOnlyActivity.this,
                                            ShareKeys.ISNIGHT, false));
                    toggleDayNight();
                }

                @Override
                public void onTextSizeClick() {
                    showFontSizeSelectorDialog();
                }

                @Override
                public void onBackgroundStyleClick() {
                    showThemeSelectorDialog();
                }

                @Override
                public void onToggleTranslateWayClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (ReadTextOnlyActivity.this, ShareKeys.DOUBLE_CLICK_TRANSLATE,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(ReadTextOnlyActivity.this,
                                            ShareKeys.DOUBLE_CLICK_TRANSLATE, false));
                }

                @Override
                public void onCloseTranslateClick() {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (ReadTextOnlyActivity.this, ShareKeys.CLOSE_TRANSLATE,
                                    !SharedPreferencesUtils.getBooleanSharedPreferencesData(ReadTextOnlyActivity.this,
                                            ShareKeys.CLOSE_TRANSLATE, false));
                }

                @Override
                public void onUserHeadClick() {
                    if (TextUtils.isEmpty(LoginBean.getInstance().getUserName())) {
                        Intent intent = new Intent(ReadTextOnlyActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        baseToast.showToast("你好!" + LoginBean.getInstance().getUserName() + "!");
                    }
                }

                @Override
                public void onCloseDialogClick() {

                }

                @Override
                public void onExitClick() {
                    ReadTextOnlyActivity.this.finish();
                }

                @Override
                public void onSaveClick() {
                    showSaveDialog();
                }
            });
        }
        readDialog.show();
        readDialog.refreshUI();
    }

    private void toggleDayNight() {
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData(ReadTextOnlyActivity.this,
                ShareKeys.ISNIGHT, false)) {
            mPageWidget.setTheme(ThemeManager.NIGHT);
            mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_night),
                    getResources().getColor(R.color.chapter_title_night));
        } else {
            mPageWidget.setTheme(SettingManager.getInstance().getReadTheme());
            mPageWidget.setTextColor(getResources().getColor(R.color.chapter_content_day),
                    getResources().getColor(R.color.chapter_title_day));
        }
    }

    private void showThemeSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                SettingManager.getInstance().saveReadTheme(position);
                if (position == ThemeManager.NIGHT) {
                    SharedPreferencesUtils.setSharedPreferencesData
                            (ReadTextOnlyActivity.this, ShareKeys.ISNIGHT,
                                    true);
                    toggleDayNight();
                    return;
                }
                mPageWidget.setTheme(position);
            }
        });
        listDialog.show();
        listDialog.setOptionsList(ThemeManager.THEME_LIST);
    }

    @AfterPermissionGranted(111)
    private void showSaveDialog() {
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
                        fw.write(urlContent);
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
            dialog.setTitle("要保存文本吗?");
            dialog.setHint("请输入文本标题");
            dialog.setEditTextContent(StringUtils.replaceAllSpecialCharacterTo(title, "_"));
            dialog.setOkText("确定");
            dialog.setCancelText("取消");
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "我们需要写入/读取权限",
                    111, perms);
        }
    }

    private void showFontSizeSelectorDialog() {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {
                mPageWidget.setFontSize(ScreenUtils.dpToPxInt(Integer.valueOf(selectedCodeRes)));
            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
            }
        });
        listDialog.show();
        listDialog.setOptionsList(SettingManager.FONT_SIZE_LIST);
        listDialog.setCodeList(SettingManager.FONT_SIZE_CODE_LIST);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tts.stop(); // 不管是否正在朗读TTS都被打断
        tts.shutdown(); // 关闭，释放资源
    }

    /**
     * 用来初始化TextToSpeech引擎
     * status:SUCCESS或ERROR这2个值
     * setLanguage设置语言，帮助文档里面写了有22种
     * TextToSpeech.LANG_MISSING_DATA：表示语言的数据丢失。
     * TextToSpeech.LANG_NOT_SUPPORTED:不支持
     */
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.ENGLISH);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                baseToast.showToast("数据丢失或不支持");
            }
        }
    }


    private class ReadListener implements OnReadStateChangeListener {
        @Override
        public void onPageChanged(int page) {
        }

        @Override
        public void onLoadFailure(String path) {
        }

        @Override
        public void onCenterClick() {
            //TODO
//            toggleReadBar();
        }

        @Override
        public void onFlip() {
            //TODO
//            hideReadBar();
        }
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
            MangaDialog peanutDialog = new MangaDialog(ReadTextOnlyActivity.this);
            peanutDialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
                @Override
                public void onOkClick() {
                }

                @Override
                public void onCancelClick() {

                }
            });
            peanutDialog.show();
            peanutDialog.setTitle("没有文件读写权限,无法保存文本!");
        }
    }
}
