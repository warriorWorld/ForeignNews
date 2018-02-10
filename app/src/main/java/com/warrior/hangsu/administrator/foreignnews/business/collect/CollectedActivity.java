package com.warrior.hangsu.administrator.foreignnews.business.collect;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.bean.CollectBean;
import com.warrior.hangsu.administrator.foreignnews.db.DbAdapter;
import com.warrior.hangsu.administrator.foreignnews.base.BaseActivity;
import com.warrior.hangsu.administrator.foreignnews.utils.Globle;
import com.warrior.hangsu.administrator.foreignnews.utils.SharedPreferencesUtils;

import java.util.ArrayList;

public class CollectedActivity extends BaseActivity
        implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView collectedLv;
    private CheckBox closeQueryWordCB;
    private ArrayList<CollectBean> collectList = new ArrayList<CollectBean>();
    private CollectedAdapter adapter;
    private View emptyView;
    private DbAdapter db;
    private String[] optionsList = {"设置为首页", "删除该收藏"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DbAdapter(this);
        initUI();
        refresh();
    }

    private void initUI() {

        emptyView = findViewById(R.id.empty_view);
        collectedLv = (ListView) findViewById(R.id.collected_listview);
        closeQueryWordCB = (CheckBox) findViewById(R.id.close_query_word);
        closeQueryWordCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Globle.closeQueryWord = isChecked;
                SharedPreferencesUtils.setSharedPreferencesData(
                        CollectedActivity.this, "closeQueryWord", isChecked + "");
            }
        });
        closeQueryWordCB.setChecked(Globle.closeQueryWord);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    private void refresh() {
        collectList = db.queryAllCollect();
        initListView();
    }

    private void initListView() {
        if (null == adapter) {
            adapter = new CollectedAdapter(CollectedActivity.this, collectList);
            collectedLv.setAdapter(adapter);
            collectedLv.setOnItemLongClickListener(this);
            collectedLv.setOnItemClickListener(this);
            collectedLv.setEmptyView(emptyView);
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
        db.closeDb();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        CollectBean item = collectList.get(position);
        showOptionsDialog(item);
        return true;
    }

    private void showOptionsDialog(final CollectBean item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                CollectedActivity.this);
        builder.setTitle("选项");
        builder.setItems(optionsList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Globle.firstPageURL = item.getUrl();
                        SharedPreferencesUtils.setSharedPreferencesData(
                                CollectedActivity.this, "firstPageURL", Globle.firstPageURL);
                        break;
                    case 1:
                        db.deleteCollect(item.getUrl());
                        refresh();
                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
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
