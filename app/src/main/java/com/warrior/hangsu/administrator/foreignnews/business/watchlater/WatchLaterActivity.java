package com.warrior.hangsu.administrator.foreignnews.business.watchlater;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.adapter.OnlyTextAdapter;
import com.warrior.hangsu.administrator.foreignnews.base.BaseFragmentActivity;
import com.warrior.hangsu.administrator.foreignnews.configure.Globle;
import com.warrior.hangsu.administrator.foreignnews.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnRecycleItemLongClickListener;

import java.util.List;

public class WatchLaterActivity extends BaseFragmentActivity
        implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private RecyclerView urlRcv;
    private WatchLaterViewModel mWatchLaterViewModel;
    private OnlyTextAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initVM();
        mWatchLaterViewModel.getWatchLater();
    }

    private void initVM() {
        mWatchLaterViewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new WatchLaterViewModel(WatchLaterActivity.this);
            }
        }).get(WatchLaterViewModel.class);

        mWatchLaterViewModel.getWatchLaterData().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> result) {
                initRec(result);
            }
        });
    }

    private void initRec(List<String> list) {
        try {
            if (null == mAdapter) {
                mAdapter = new OnlyTextAdapter(this);
                mAdapter.setList(list);
                mAdapter.setOnRecycleItemClickListener(new OnRecycleItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Intent intent = new Intent();
                        intent.putExtra("url", "file://" + Globle.CACHE_PATH + list.get(position));
                        setResult(0, intent);
                        finish();
                    }
                });
                mAdapter.setOnRecycleItemLongClickListener(new OnRecycleItemLongClickListener() {
                    @Override
                    public void onItemClick(int position) {

                    }
                });
                urlRcv.setAdapter(mAdapter);
            } else {
                mAdapter.setList(list);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void initUI() {
        super.initUI();
        urlRcv = (RecyclerView) findViewById(R.id.only_rcv);
        urlRcv.setLayoutManager
                (new LinearLayoutManager
                        (this, LinearLayoutManager.VERTICAL, false));
        urlRcv.setFocusable(false);
        urlRcv.setHasFixedSize(true);

        baseTopBar.setTitle("稍后浏览");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_only_recycler;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.finish();
    }
}
