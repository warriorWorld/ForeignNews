package com.warrior.hangsu.administrator.foreignnews.base;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.widget.bar.TopBar;


/**
 * 个人信息页
 */
public abstract class BaseRefreshFragment extends BaseFragment implements View.OnClickListener {
    protected TopBar refreshBaseTopbar;
    protected SwipeRefreshLayout baseSwipeLayout;

    protected int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_only_refresh, container, false);
        onCreateBeforInitUI();
        initUI(v);
        onCreateAfterInitUI();
        doGetData();
        return v;
    }

    protected abstract void onCreateAfterInitUI();

    protected abstract void onCreateBeforInitUI();

    protected abstract void doGetData();

    protected abstract int getLayoutId();

    protected void initUI(View v) {
        refreshBaseTopbar = (TopBar) v.findViewById(R.id.refresh_frgament_topbar);
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
            }
        });
        baseSwipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.base_swipe_layout);
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        baseSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
//        baseSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        baseSwipeLayout.setColorSchemeResources(R.color.english_browser);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        baseSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 开始刷新，设置当前为刷新状态
                baseSwipeLayout.setRefreshing(true);
                page = 0;
                doGetData();
            }
        });

        ViewGroup containerView = (ViewGroup) v.findViewById(R.id.swipe_target);
        LayoutInflater.from(getActivity()).inflate(getLayoutId(), containerView);

        initFrgmentUI(containerView);
    }

    protected abstract void initFrgmentUI(ViewGroup view);

    protected void hideRefreshTopBar() {
        refreshBaseTopbar.setVisibility(View.GONE);
    }

    protected void showRefreshTopBar() {
        refreshBaseTopbar.setVisibility(View.GONE);
    }

    protected void hideLoadMore() {
        baseSwipeLayout.setEnabled(false);
    }

    protected void noMoreData() {
        baseSwipeLayout.setRefreshing(false);
    }


//    @Override
//    public void onLoadMore() {
//        page++;
//        doGetData();
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}
