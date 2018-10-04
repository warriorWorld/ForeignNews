package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.DownloadListener;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.bean.YoudaoResponse;
import com.warrior.hangsu.administrator.foreignnews.business.ad.AdvertisingActivity;
import com.warrior.hangsu.administrator.foreignnews.business.collect.CollectedActivity;
import com.warrior.hangsu.administrator.foreignnews.business.login.LoginActivity;
import com.warrior.hangsu.administrator.foreignnews.business.main.MainActivity;
import com.warrior.hangsu.administrator.foreignnews.business.other.AboutActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnCopyClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnReceivedWebInfoListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnSpeakClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarHomeClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarWebNumClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.utils.DownLoadUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebSubTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.QrDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.TranslateDialog;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2018/10/3.
 */

public class TranslateWebFragment extends WebFragment implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private TranslateDialog translateResultDialog;
    private OnReceivedWebInfoListener mOnReceivedWebInfoListener;

    @Override
    protected void onCreateAfterInitUI() {
        super.onCreateAfterInitUI();
        initTTS();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_webview;
    }

    private void initTTS() {
        tts = new TextToSpeech(getActivity(), this); // 参数Context,TextToSpeech.OnInitListener
    }

    @Override
    protected void initFrgmentUI(ViewGroup view) {
        super.initFrgmentUI(view);
        hideRefreshTopBar();
        myWebView.setSelectionListener(new TextSelectionListener() {
            @Override
            public void seletedWord(String word) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        myWebView.clearFocus();
                    }
                }, 150);//n秒后执行Runnable中的run方法
                translation(word);
            }

            @Override
            public void clickWord(String word) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        /**
                         *要执行的操作
                         */
                        myWebView.clearFocus();
                    }
                }, 50);//n秒后执行Runnable中的run方法
                translation(word);
            }
        });
        myWebView.setOnWebViewLongClickListener(new TranslateWebView.OnWebViewLongClickListener() {
            @Override
            public void onImgLongClick(String imgUrl) {
                showSaveImgDialog(imgUrl);
            }
        });

        myWebView.loadUrl
                (SharedPreferencesUtils.getSharedPreferencesData
                        (getActivity(), ShareKeys.MAIN_URL, Globle.DEFAULT_MAIN_URL));
        myWebView.setOnReceivedWebInfoListener(mOnReceivedWebInfoListener);
    }

    private void translation(final String word) {
        clip.setText(word);
        text2Speech(word);
        //记录查过的单词
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (getActivity(), ShareKeys.CLOSE_TRANSLATE, false)) {
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
                        showTranslateResultDialog(word, result.getQuery() + " [" + item.getPhonetic() +
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
        VolleyTool.getInstance(getActivity()).requestData(Request.Method.GET,
                getActivity(), url, params,
                YoudaoResponse.class, callback);
    }

    private void text2Speech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.setPitch(0.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            HashMap<String, String> myHashAlarm = new HashMap();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_ALARM));
            tts.speak(text,
                    TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
    }

    private void showTranslateResultDialog(final String title, String msg) {
        if (null == translateResultDialog) {
            translateResultDialog = new TranslateDialog(getActivity());
            translateResultDialog.setOnSpeakClickListener(new OnSpeakClickListener() {
                @Override
                public void onSpeakClick(String word) {
                    text2Speech(word);
                }
            });
        }
        translateResultDialog.show();

        translateResultDialog.setTitle(title);
        translateResultDialog.setMessage(msg);
        translateResultDialog.setOkText("确定");
        translateResultDialog.setCancelable(true);
    }

    private void showSaveImgDialog(final String imgUrl) {
        MangaDialog dialog = new MangaDialog(getActivity());
        dialog.setOnPeanutDialogClickListener(new MangaDialog.OnPeanutDialogClickListener() {
            @Override
            public void onOkClick() {
                DownLoadUtil.downloadImg(getActivity(), imgUrl);
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

    public void setOnReceivedWebInfoListener(OnReceivedWebInfoListener mListener) {
        this.mOnReceivedWebInfoListener = mListener;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
//        myWebView.clearCache(true);
        tts.stop(); // 不管是否正在朗读TTS都被打断
        tts.shutdown(); // 关闭，释放资源
    }
}
