package com.warrior.hangsu.administrator.foreignnews.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.warrior.hangsu.administrator.foreignnews.R;

import java.lang.reflect.Field;

/**
 * Created by Administrator on 2017/7/29.
 */

public abstract class BaseMultiTabActivity extends BaseFragmentActivity {
    private TabLayout tabLayout;
    private ViewPager vp;
    private MyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFragment();
    }

    protected abstract void initFragment();

    protected abstract String getActivityTitle();

    protected abstract int getPageCount();

    protected abstract ViewPager.OnPageChangeListener getPageListener();

    protected abstract String[] getTabTitleList();

    protected abstract Fragment getFragmentByPosition(int position);

    @Override
    protected void initUI() {
        super.initUI();
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        vp = (ViewPager) findViewById(R.id.view_pager);
        vp.addOnPageChangeListener(getPageListener());

        vp.setAdapter(adapter = new MyFragmentPagerAdapter(this.getSupportFragmentManager()));
        vp.setOffscreenPageLimit(5);
        tabLayout.setupWithViewPager(vp);
//        setTabLayoutIndicatorWidth(tabLayout, 84, 84);
//        reflex(tabLayout);
        baseTopBar.setTitle(getActivityTitle());
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_only_vp;
    }

    /*
  setOffscreenPageLimit对此无用,全都在内存里
  FragmentPagerAdapter 继承自 PagerAdapter。相比通用的 PagerAdapter，该类更专注于每一页均为 Fragment
   的情况。如文档所述，<b>该类内的每一个生成的 Fragment 都将保存在内存之中，因此适用于那些相对静态的页</b>，数量也比
   较少的那种；如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，应该使用
   FragmentStatePagerAdapter。FragmentPagerAdapter 重载实现了几个必须的函数，因此来自 PagerAdapter
   的函数，我们只需要实现 getCount()，即可。且，由于 FragmentPagerAdapter.instantiateItem() 的实现中，
   调用了一个新增的虚函数 getItem()，因此，我们还至少需要实现一个 getItem()。因此，总体上来说，相对于继承自
   PagerAdapter，更方便一些。*/
    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragmentByPosition(position);
        }


        @Override
        public int getCount() {
            return getPageCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getTabTitleList()[position];
        }
    }
}
