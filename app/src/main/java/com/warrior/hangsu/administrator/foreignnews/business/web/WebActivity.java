package com.warrior.hangsu.administrator.foreignnews.business.web;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.insightsurfface.stylelibrary.keyboard.KeyBoardDialog;
import com.insightsurfface.stylelibrary.listener.OnKeyboardChangeListener;
import com.insightsurfface.stylelibrary.listener.OnKeyboardListener;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseFragment;
import com.warrior.hangsu.administrator.foreignnews.base.FragmentContainerActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.TopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.OnlyEditDialog;

import javax.microedition.khronos.opengles.GL;


/**
 * 个人信息页
 */
public class WebActivity extends FragmentContainerActivity {
    private TranslateWebFragment mWebFragment;
    private String title = "";
    private ImageView translateIv;
    private KeyBoardDialog searchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mWebFragment = new TranslateWebFragment();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initUI() {
        super.initUI();
        translateIv = (ImageView) findViewById(R.id.translate_iv);
        title = getIntent().getStringExtra("title");
        baseTopBar.setTitle(title);
        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                finish();
            }

            @Override
            public void onRightClick() {

            }

            @Override
            public void onTitleClick() {
                if (Globle.IS_TEST) {
                    showBaseDialog(mWebFragment.getUrl(), "", "知道了", "", null);
                }
            }
        });

        translateIv.setOnClickListener(this);
    }

    @Override
    protected BaseFragment getFragment() {
        return mWebFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }

    @Override
    protected String getTopBarTitle() {
        return getResources().getString(R.string.app_name);
    }

    private void showTranslateDialog() {
        if (null == searchDialog) {
            searchDialog = new KeyBoardDialog(this);
            searchDialog.setOnKeyboardListener(new OnKeyboardListener() {
                @Override
                public void onOptionsClick() {
                    baseToast.showToast("未实现");
                }

                @Override
                public void onQuestionClick() {
                    baseToast.showToast("点按后滑动输入");
                }
            });
            searchDialog.setOnKeyboardChangeListener(new OnKeyboardChangeListener() {
                @Override
                public void onChange(String res) {

                }

                @Override
                public void onFinish(String res) {
                    searchDialog.dismiss();
                    mWebFragment.translation(res);
                }
            });
        }
        searchDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.translate_iv:
                showTranslateDialog();
                break;
        }
    }
}
