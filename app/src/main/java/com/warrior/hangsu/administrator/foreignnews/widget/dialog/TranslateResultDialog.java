package com.warrior.hangsu.administrator.foreignnews.widget.dialog;/**
 * Created by Administrator on 2016/11/4.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.Group;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.insightsurfface.myword.aidl.TranslateWraper;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.listener.OnSpeakClickListener;
import com.warrior.hangsu.administrator.foreignnews.utils.AudioMgr;


/**
 * 作者：苏航 on 2016/11/4 11:08
 * 邮箱：772192594@qq.com
 */
public class TranslateResultDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView wordTv;
    private ImageView ukIv;
    private TextView ukPhoneticTv;
    private ImageView usIv;
    private TextView usPhoneticTv;
    private TextView translateTv;
    private TextView webTranslateTv;
    private TextView okTv;
    private TranslateWraper mTranslate;
    private Group webTranslateGroup;
    private Group ukGroup, usGroup;
    private OnSpeakClickListener onSpeakClickListener;
    private String word;

    public TranslateResultDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vip_translate);
        init();

        Window window = this.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        WindowManager wm = ((Activity) context).getWindowManager();
        Display d = wm.getDefaultDisplay();
        // lp.height = (int) (d.getHeight() * 0.4);
        lp.width = (int) (d.getWidth() * 1);
        // window.setGravity(Gravity.LEFT | Gravity.TOP);
        window.setGravity(Gravity.CENTER);
//        window.getDecorView().setPadding(0, 0, 0, 0);
        // lp.x = 100;
        // lp.y = 100;
        // lp.height = 30;
        // lp.width = 20;
        window.setAttributes(lp);
    }


    private void init() {
        wordTv = (TextView) findViewById(R.id.word_tv);
        ukIv = (ImageView) findViewById(R.id.uk_iv);
        ukPhoneticTv = (TextView) findViewById(R.id.uk_phonetic_tv);
        usIv = (ImageView) findViewById(R.id.us_iv);
        usPhoneticTv = (TextView) findViewById(R.id.us_phonetic_tv);
        translateTv = (TextView) findViewById(R.id.translate_tv);
        webTranslateTv = (TextView) findViewById(R.id.web_translate_tv);
        okTv = (TextView) findViewById(R.id.ok_tv);
        ukGroup = (Group) findViewById(R.id.uk_group);
        usGroup = (Group) findViewById(R.id.us_group);
        webTranslateGroup = (Group) findViewById(R.id.web_translate_group);
        webTranslateGroup.setVisibility(View.GONE);
        ukPhoneticTv.setOnClickListener(this);
        usPhoneticTv.setOnClickListener(this);
        ukIv.setOnClickListener(this);
        usIv.setOnClickListener(this);
        okTv.setOnClickListener(this);
    }

    public void setTranslate(TranslateWraper translate) {
        mTranslate = translate;
        if (null != translate) {
            word = translate.getQuery();
            wordTv.setText(word);
            if (!TextUtils.isEmpty(translate.getUKPhonetic())) {
                ukPhoneticTv.setText("/" + translate.getUKPhonetic() + "/");
                ukGroup.setVisibility(View.VISIBLE);
            } else {
                ukPhoneticTv.setText("");
                ukGroup.setVisibility(View.GONE);
            }
            if (!TextUtils.isEmpty(translate.getUSPhonetic())) {
                usPhoneticTv.setText("/" + translate.getUSPhonetic() + "/");
                usGroup.setVisibility(View.VISIBLE);
            } else {
                usPhoneticTv.setText("");
                usGroup.setVisibility(View.GONE);
            }
            translateTv.setText(translate.getTranslate());
            if (!TextUtils.isEmpty(translate.getWebTranslate())) {
                webTranslateGroup.setVisibility(View.VISIBLE);
                webTranslateTv.setText(translate.getWebTranslate());
            } else {
                webTranslateGroup.setVisibility(View.GONE);
            }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok_tv:
                dismiss();
                break;
            case R.id.uk_iv:
            case R.id.uk_phonetic_tv:
                if (!TextUtils.isEmpty(mTranslate.getUKSpeakUrl())) {
                    playVoice(mTranslate.getUKSpeakUrl());
                } else {
                    if (null != onSpeakClickListener) {
                        onSpeakClickListener.onSpeakClick(word);
                    }
                }
                break;
            case R.id.us_iv:
            case R.id.us_phonetic_tv:
                if (!TextUtils.isEmpty(mTranslate.getUSSpeakUrl())) {
                    playVoice(mTranslate.getUSSpeakUrl());
                } else {
                    if (null != onSpeakClickListener) {
                        onSpeakClickListener.onSpeakClick(word);
                    }
                }
                break;
        }
    }

    public void setOnSpeakClickListener(OnSpeakClickListener onSpeakClickListener) {
        this.onSpeakClickListener = onSpeakClickListener;
    }
}
