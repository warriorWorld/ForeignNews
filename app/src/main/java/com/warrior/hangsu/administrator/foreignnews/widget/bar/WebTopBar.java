package com.warrior.hangsu.administrator.foreignnews.widget.bar;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarRefreshClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnWebTopBarSkipToURLListener;
import com.warrior.hangsu.administrator.foreignnews.utils.ToastUtil;

/**
 * Created by Administrator on 2016/10/5.
 */
public class WebTopBar extends BaseWebTopBar implements View.OnClickListener {
    private Context context;
    private RelativeLayout showRL, editRL;
    private EditText titleET;
    private TextView titleTV;
    private ImageView refreshIV, cancelIV;
    private OnWebTopBarRefreshClickListener onWebTopBarRefreshClickListener;
    private OnWebTopBarSkipToURLListener onWebTopBarSkipToURLListener;
    private ProgressBar progressBar;

    public WebTopBar(Context context) {
        this(context, null);
    }

    public WebTopBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_topbar_web, this);
        showRL = (RelativeLayout) findViewById(R.id.topbar_show_rl);
        editRL = (RelativeLayout) findViewById(R.id.topbar_edit_rl);
        titleET = (EditText) findViewById(R.id.web_title_et);
        titleET.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                toggleEditAndShow(hasFocus);
            }
        });
        titleET.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (null != onWebTopBarSkipToURLListener && !TextUtils.isEmpty(titleET.getText().toString())) {
                        toggleEditAndShow(false);
                        onWebTopBarSkipToURLListener.skipToURL(titleET.getText().toString());
                    } else {
                        ToastUtil.tipShort(context, "网址呢?");
                    }
                }
                return false;
            }
        });
        titleTV = (TextView) findViewById(R.id.web_title_tv);
        refreshIV = (ImageView) findViewById(R.id.refresh_iv);
        cancelIV = (ImageView) findViewById(R.id.cancel_iv);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.incrementProgressBy(10);
        titleTV.setOnClickListener(this);
        cancelIV.setOnClickListener(this);
        refreshIV.setOnClickListener(this);

        toggleEditAndShow(false);
    }

    @Override
    public void toggleEditAndShow(boolean isEdit) {
        if (isEdit) {
            showRL.setVisibility(View.GONE);
            editRL.setVisibility(View.VISIBLE);
            titleET.requestFocus();
            titleET.setSelection(0, titleET.getText().length());
            openKeyboard();
        } else {
            showRL.setVisibility(View.VISIBLE);
            editRL.setVisibility(View.GONE);
            closeKeyboard();
        }
    }

    private void openKeyboard() {
        ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(titleET.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.web_title_tv:
                toggleEditAndShow(true);
                break;
            case R.id.refresh_iv:
                if (null != onWebTopBarRefreshClickListener) {
                    onWebTopBarRefreshClickListener.onRefreshClick();
                }
                break;
            case R.id.cancel_iv:
                titleET.setText("");
                break;
        }
    }

    @Override
    public void setProgress(int progress) {
        if (progress == 100) {
            progressBar.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
        progressBar.setProgress(progress);
    }

    @Override
    public void setTitle(String title) {
        titleTV.setText(title);
    }

    @Override
    public void setTitleTextColor(int color) {
        titleTV.setTextColor(color);
    }

    public String getTitle() {
        return titleTV.getText().toString();
    }

    @Override
    public void setPath(String path) {
        titleET.setText(path);
    }

    public String getPaht() {
        return titleET.getText().toString();
    }

    @Override
    public void setOnWebTopBarRefreshClickListener(OnWebTopBarRefreshClickListener onWebTopBarRefreshClickListener) {
        this.onWebTopBarRefreshClickListener = onWebTopBarRefreshClickListener;
    }

    @Override
    public void setOnWebTopBarSkipToURLListener(OnWebTopBarSkipToURLListener onWebTopBarSkipToURLListener) {
        this.onWebTopBarSkipToURLListener = onWebTopBarSkipToURLListener;
    }
}
