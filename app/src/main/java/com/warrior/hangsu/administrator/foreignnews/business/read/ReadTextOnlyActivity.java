package com.warrior.hangsu.administrator.foreignnews.business.read;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReadDialogClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReadStateChangeListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnUpFlipListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWordClickListener;
import com.warrior.hangsu.administrator.foreignnews.mannger.SettingManager;
import com.warrior.hangsu.administrator.foreignnews.mannger.ThemeManager;
import com.warrior.hangsu.administrator.foreignnews.utils.ScreenUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.StringUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.ReadDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 个人信息页
 */
public class ReadTextOnlyActivity extends BaseActivity {
    private String url;
    private static org.jsoup.nodes.Document doc;
    private String urlContent;
    private BaseReadView mPageWidget;
    private FrameLayout readWidgetFl;
    private ClipboardManager clip;//复制文本用
    private MangaDialog dialog;
    private ReadDialog readDialog;
    private String title;

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
                    }
                }

                @Override
                public void onCloseDialogClick() {

                }

                @Override
                public void onExitClick() {
                    ReadTextOnlyActivity.this.finish();
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
}
