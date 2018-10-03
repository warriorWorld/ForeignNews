package com.warrior.hangsu.administrator.foreignnews.base;/**
 * Created by Administrator on 2016/10/17.
 */

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.eventbus.EventBusEvent;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.TopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.toast.EasyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


/**
 * 作者：苏航 on 2016/10/17 11:56
 * 邮箱：772192594@qq.com
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {
    protected TopBar baseTopBar;
    protected EasyToast baseToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//此FLAG可使状态栏透明，且当前视图在绘制时，从屏幕顶端开始即top = 0开始绘制，这也是实现沉浸效果的基础
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.english_browser));
        }
        initUI();
        baseToast = new EasyToast(this);
        // 在oncreate里订阅
        EventBus.getDefault().register(this);
        ActivityPoor.addActivity(this);
    }

    protected void initUI() {
        setContentView(R.layout.activity_base);
        baseTopBar = (TopBar) findViewById(R.id.base_topbar);
        ViewGroup containerView = (ViewGroup) findViewById(R.id.base_container);
        LayoutInflater.from(this).inflate(getLayoutId(), containerView);

        baseTopBar.setOnTopBarClickListener(new TopBar.OnTopBarClickListener() {
            @Override
            public void onLeftClick() {
                topBarOnLeftClick();
            }

            @Override
            public void onRightClick() {
                topBarOnRightClick();
            }

            @Override
            public void onTitleClick() {
                topBarOnTitleClick();
            }
        });
    }

    protected abstract int getLayoutId();

    protected void hideBaseTopBar() {
        baseTopBar.setVisibility(View.GONE);
    }

    protected void topBarOnLeftClick() {
        this.finish();
    }

    protected void topBarOnRightClick() {

    }

    protected void topBarOnTitleClick() {

    }

    /**
     * 在主线程中执行,eventbus遍历所有方法,就为了找到该方法并执行.传值自己随意写
     *
     * @param event
     */
    @Subscribe
    public void onEventMainThread(final EventBusEvent event) {
        if (null == event)
            return;
        Intent intent = null;
        switch (event.getEventType()) {
//            case PeanutEvent.NEED_LOGIN:
//                ToastUtil.tipShort(BaseFragmentActivity.this, "需要登录");
//                intent = new Intent(BaseFragmentActivity.this, LoginActivity.class);
//                break;
        }
        if (null != intent) {
            startActivity(intent);
        }
    }

    protected void showBaseDialog(String title, String msg, String okText, String cancelText, MangaDialog.OnPeanutDialogClickListener listener) {
        MangaDialog baseDialog = new MangaDialog(this);
        if (null != listener)
            baseDialog.setOnPeanutDialogClickListener(listener);
        baseDialog.show();
        if (!TextUtils.isEmpty(title)) {
            baseDialog.setTitle(title);
        }
        if (!TextUtils.isEmpty(msg)) {
            baseDialog.setMessage(msg);
        }
        if (!TextUtils.isEmpty(okText)) {
            baseDialog.setOkText(okText);
        }
        if (!TextUtils.isEmpty(cancelText)) {
            baseDialog.setCancelText(cancelText);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 每次必须取消订阅
        EventBus.getDefault().unregister(this);
        ActivityPoor.finishSingleActivity(this);
    }
}
