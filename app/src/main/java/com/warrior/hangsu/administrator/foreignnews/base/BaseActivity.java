package com.warrior.hangsu.administrator.foreignnews.base;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.business.main.WebActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.eventbus.EventBusEvent;
import com.warrior.hangsu.administrator.foreignnews.listener.OnEditResultListener;
import com.warrior.hangsu.administrator.foreignnews.service.CopyBoardService;
import com.warrior.hangsu.administrator.foreignnews.utils.ActivityPoor;
import com.warrior.hangsu.administrator.foreignnews.utils.ServiceUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.StringUtils;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.TopBar;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.MangaEditDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.toast.EasyToast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public abstract class BaseActivity extends Activity {
    protected TopBar baseTopBar;
    protected EasyToast baseToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //状态栏透明
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
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

    private void initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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

    protected void hideBack() {
        baseTopBar.hideLeftButton();
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
//            case EventBusEvent.NEED_LOGIN:
//                ToastUtil.tipShort(BaseActivity.this, "需要登录");
//                intent = new Intent(BaseActivity.this, LoginActivity.class);
//                break;
            case EventBusEvent.COPY_BOARD_URL_EVENT:
                showBaseDialog("检测到你复制了某个网址，是否跳转到详情页？", "", "是", "否",
                        new MangaDialog.OnPeanutDialogClickListener() {
                            @Override
                            public void onOkClick() {
                                Intent intent1 = new Intent(BaseActivity.this, WebActivity.class);
                                intent1.putExtra("url", event.getMsg());
                                startActivity(intent1);
                            }

                            @Override
                            public void onCancelClick() {

                            }
                        });
                break;
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
    protected void onResume() {
        super.onResume();
        if (!ServiceUtil.isServiceWork(this,
                "com.warrior.hangsu.administrator.foreignnews.service.CopyBoardService")) {
            Intent intent = new Intent(this, CopyBoardService.class);
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 每次必须取消订阅
        EventBus.getDefault().unregister(this);
        ActivityPoor.finishSingleActivity(this);
    }
}
