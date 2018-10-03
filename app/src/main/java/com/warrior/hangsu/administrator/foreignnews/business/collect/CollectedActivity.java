package com.warrior.hangsu.administrator.foreignnews.business.collect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.CloudQueryCallback;
import com.avos.avoscloud.FindCallback;
import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.base.BaseFragmentActivity;
import com.warrior.hangsu.administrator.foreignnews.bean.CollectBean;
import com.warrior.hangsu.administrator.foreignnews.bean.LoginBean;
import com.warrior.hangsu.administrator.foreignnews.configure.ShareKeys;
import com.warrior.hangsu.administrator.foreignnews.listener.OnSevenFourteenListDialogListener;
import com.warrior.hangsu.administrator.foreignnews.utils.LeanCloundUtil;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.ListDialog;
import com.warrior.hangsu.administrator.foreignnews.widget.dialog.SingleLoadBarUtil;

import java.util.ArrayList;
import java.util.List;

public class CollectedActivity extends BaseFragmentActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView collectedLv;
    private ArrayList<CollectBean> collectList = new ArrayList<CollectBean>();
    private CollectedAdapter adapter;
    private View emptyView;
    private String[] optionsList = {"设置为首页", "删除该收藏"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        doGetData();
    }

    @Override
    protected void initUI() {
        super.initUI();
        emptyView = findViewById(R.id.empty_view);
        collectedLv = (ListView) findViewById(R.id.collected_listview);

        baseTopBar.setTitle("我的收藏");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void doGetData() {
        if (TextUtils.isEmpty(LoginBean.getInstance().getUserName(this))) {
            this.finish();
            return;
        }
        SingleLoadBarUtil.getInstance().showLoadBar(CollectedActivity.this);
        AVQuery<AVObject> query = new AVQuery<>("Collect");
        query.whereEqualTo("owner", LoginBean.getInstance().getUserName());
        query.limit(999);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                SingleLoadBarUtil.getInstance().dismissLoadBar();
                if (LeanCloundUtil.handleLeanResult(CollectedActivity.this, e)) {
                    collectList = new ArrayList<CollectBean>();
                    if (null != list && list.size() > 0) {
                        CollectBean item;
                        for (int i = 0; i < list.size(); i++) {
                            item = new CollectBean();
                            item.setTitle(list.get(i).getString("collect_title"));
                            item.setUrl(list.get(i).getString("collect_url"));
                            item.setObjectId(list.get(i).getObjectId());

                            collectList.add(item);
                        }
                    }
                    initListView();
                }
            }
        });
    }

    private void deleteCollected(int position) {
        SingleLoadBarUtil.getInstance().showLoadBar(CollectedActivity.this);
        // 执行 CQL 语句实现删除一个 Todo 对象
        AVQuery.doCloudQueryInBackground(
                "delete from Collect where objectId='" + collectList.get(position).getObjectId() + "'"
                , new CloudQueryCallback<AVCloudQueryResult>() {
                    @Override
                    public void done(AVCloudQueryResult avCloudQueryResult, AVException e) {
                        SingleLoadBarUtil.getInstance().dismissLoadBar();
                        if (LeanCloundUtil.handleLeanResult(CollectedActivity.this, e)) {
                            doGetData();
                        }
                    }
                });
    }

    private void initListView() {
        if (null == adapter) {
            adapter = new CollectedAdapter(CollectedActivity.this, collectList);
            collectedLv.setAdapter(adapter);
            collectedLv.setOnItemLongClickListener(this);
            collectedLv.setOnItemClickListener(this);
            collectedLv.setEmptyView(emptyView);
            collectedLv.setDividerHeight(0);
        } else {
            adapter.setList(collectList);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
        }
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        showOptionsSelectorDialog(position);
        return true;
    }


    private void showOptionsSelectorDialog(final int selected) {
        ListDialog listDialog = new ListDialog(this);
        listDialog.setOnSevenFourteenListDialogListener(new OnSevenFourteenListDialogListener() {
            @Override
            public void onItemClick(String selectedRes, String selectedCodeRes) {

            }

            @Override
            public void onItemClick(String selectedRes) {

            }

            @Override
            public void onItemClick(int position) {
                CollectBean item = collectList.get(selected);
                switch (position) {
                    case 0:
                        SharedPreferencesUtils.setSharedPreferencesData(
                                CollectedActivity.this, ShareKeys.MAIN_URL, item.getUrl());
                        baseToast.showToast("设置成功");
                        break;
                    case 1:
                        deleteCollected(selected);
                        break;
                }
            }
        });
        listDialog.show();
        listDialog.setOptionsList(optionsList);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra("url", collectList.get(position).getUrl());
        setResult(0, intent);
        this.finish();
    }


    /**
     * arraylistadapter
     */
    private class CollectedAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<CollectBean> list;

        public CollectedAdapter(Context context, ArrayList<CollectBean> list) {
            this.context = context;
            this.list = list;
        }

        public void setList(ArrayList<CollectBean> list) {
            this.list = list;
        }

        public ArrayList<CollectBean> getList() {
            return list;
        }

        @Override
        public int getCount() {
            if (null == list || list.size() == 0)
                return 0;
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(context).inflate(
                        R.layout.item_list_collect, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.titleTv = (TextView) convertView
                        .findViewById(R.id.title_tv);
                viewHolder.urlTv = (TextView) convertView
                        .findViewById(R.id.url_tv);

                convertView.setTag(viewHolder);
            } else {
                // 初始化过的话就直接获取
                viewHolder = (ViewHolder) convertView.getTag();
            }
            CollectBean item = list.get(position);
            viewHolder.titleTv.setText(item.getTitle());
            viewHolder.urlTv.setText(item.getUrl());
            return convertView;
        }
    }

    private class ViewHolder {
        TextView titleTv;
        TextView urlTv;
    }
}
