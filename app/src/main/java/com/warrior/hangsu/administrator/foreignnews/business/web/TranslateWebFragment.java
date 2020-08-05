package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.webkit.DownloadListener;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.insightsurfface.myword.ITranslateAidlInterface;
import com.insightsurfface.myword.aidl.ITranslateCallback;
import com.insightsurfface.myword.aidl.TranslateCallback;
import com.insightsurfface.myword.aidl.TranslateWraper;
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
import com.warrior.hangsu.administrator.foreignnews.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnSpeakClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarHomeClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarLogoutClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarOptionsClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebBottomBarWebNumClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.utils.AudioMgr;
import com.warrior.hangsu.administrator.foreignnews.utils.DownLoadUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.Logger;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.utils.VolumeUtil;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyCallBack;
import com.warrior.hangsu.administrator.foreignnews.volley.VolleyTool;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebBottomBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebSubTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.WebTopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.QrDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.TranslateDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.TranslateResultDialog;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Administrator on 2018/10/3.
 */

public class TranslateWebFragment extends WebFragment implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private TranslateDialog translateResultDialog;
    private OnReceivedWebInfoListener mOnReceivedWebInfoListener;
    private ITranslateAidlInterface mTranslateAidlInterface;

    @Override
    protected void onCreateAfterInitUI() {
        super.onCreateAfterInitUI();
        initTTS();
        bindAidlService();
    }

    private void bindAidlService() {
        Intent intent = new Intent("android.intent.action.TranslateService");
        // 注意在 Android 5.0以后，不能通过隐式 Intent 启动 service，必须制定包名
        intent.setPackage("com.insightsurfface.myword");
        ServiceConnection connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Logger.d("onServiceConnected");
                mTranslateAidlInterface = ITranslateAidlInterface.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mTranslateAidlInterface = null;
            }
        };
        getActivity().bindService(intent, connection, getActivity().BIND_AUTO_CREATE);
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
        hideRefreshTopBar();
        super.initFrgmentUI(view);
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
            public void selectedWord(String[] words) {
                showListDialog(words);
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

    public void translation(final String word) {
        clip.setText(word);
        //记录查过的单词
        if (SharedPreferencesUtils.getBooleanSharedPreferencesData
                (getActivity(), ShareKeys.CLOSE_TRANSLATE, false)) {
            return;
        }
        try {
            TranslateCallback callback = new TranslateCallback() {
                @Override
                public void onResponse(final TranslateWraper translate) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showTranslateResultDialog(translate);
                            if (!SharedPreferencesUtils.getBooleanSharedPreferencesData
                                    (getActivity(), ShareKeys.CLOSE_TTS, false)) {
                                if (!TextUtils.isEmpty(translate.getUSSpeakUrl())) {
                                    playVoice(translate.getUKSpeakUrl());
                                } else {
                                    text2Speech(word);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onFailure(final String message) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MangaDialog dialog = new MangaDialog(getActivity());
                            dialog.show();
                            dialog.setTitle(message);
                        }
                    });
                }
            };
            mTranslateAidlInterface.translate(word, callback.getCallback());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private synchronized void playVoice(String speakUrl) {
        if (!TextUtils.isEmpty(speakUrl) && speakUrl.startsWith("http")) {
            AudioMgr.startPlayVoice(speakUrl, new AudioMgr.SuccessListener() {
                @Override
                public void success() {
                }

                @Override
                public void playover() {
                }
            });
        }
    }

    private void showListDialog(String[] list) {
        ListDialog listDialog = new ListDialog(getActivity());
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {
                translation(selectedRes);
            }

            @Override
            public void onItemClick(int position) {

            }
        });
        listDialog.show();
        listDialog.setOptionsList(list);
    }

    private void text2Speech(String text) {
        if (tts != null && !tts.isSpeaking()) {
            tts.setPitch(0.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
            HashMap<String, String> myHashAlarm = new HashMap();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_STREAM,
                    String.valueOf(AudioManager.STREAM_ALARM));
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_VOLUME,
                    VolumeUtil.getMusicVolumeRate(getActivity()) + "");

            if (VolumeUtil.getHeadPhoneStatus(getActivity())) {
                AudioManager mAudioManager = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);
//            mAudioManager.setStreamMute(AudioManager.STREAM_ALARM, true);
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                mAudioManager.startBluetoothSco();
            }
            tts.speak(text,
                    TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        }
    }

    private void showTranslateResultDialog(TranslateWraper translateWraper) {
        TranslateResultDialog dialog = new TranslateResultDialog(getActivity());
        dialog.setOnSpeakClickListener(new OnSpeakClickListener() {
            @Override
            public void onSpeakClick(String word) {
                text2Speech(word);
            }
        });
        dialog.show();
        dialog.setTranslate(translateWraper);
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
