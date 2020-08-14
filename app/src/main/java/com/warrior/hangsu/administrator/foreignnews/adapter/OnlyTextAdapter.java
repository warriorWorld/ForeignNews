package com.warrior.hangsu.administrator.foreignnews.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.warrior.hangsu.administrator.foreignnews.R;
import com.warrior.hangsu.administrator.foreignnews.listener.OnRecycleItemClickListener;
import com.warrior.hangsu.administrator.foreignnews.listener.OnRecycleItemLongClickListener;

import java.util.List;


public class OnlyTextAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected Context context;
    private List<String> list;
    private OnRecycleItemClickListener mOnRecycleItemClickListener;
    private OnRecycleItemLongClickListener mOnRecycleItemLongClickListener;

    public OnlyTextAdapter(Context context) {
        this.context = context;
    }

    // 创建新View，被LayoutManager所调用
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_only_text, viewGroup, false);
        return new NormalViewHolder(view);
    }

    //将数据与界面进行绑定的操作
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        String item = list.get(position);
        NormalViewHolder vh = (NormalViewHolder) viewHolder;
        vh.onlyTextTv.setText(item);
        vh.onlyTextRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mOnRecycleItemClickListener) {
                    mOnRecycleItemClickListener.onItemClick(viewHolder.getAdapterPosition());
                }
            }
        });
        vh.onlyTextRl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (null != mOnRecycleItemLongClickListener) {
                    mOnRecycleItemLongClickListener.onItemClick(viewHolder.getAdapterPosition());
                    return true;
                }
                return false;
            }
        });
    }

    //获取数据的数量
    @Override
    public int getItemCount() {
        if (null == list || list.size() == 0) {
            return 0;
        } else {
            return list.size();
        }
    }

    public void setOnRecycleItemClickListener(OnRecycleItemClickListener onRecycleItemClickListener) {
        mOnRecycleItemClickListener = onRecycleItemClickListener;
    }

    public void setOnRecycleItemLongClickListener(OnRecycleItemLongClickListener onRecycleItemLongClickListener) {
        mOnRecycleItemLongClickListener = onRecycleItemLongClickListener;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class NormalViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout onlyTextRl;
        private TextView onlyTextTv;

        public NormalViewHolder(View view) {
            super(view);
            onlyTextRl = (RelativeLayout) view.findViewById(R.id.only_text_rl);
            onlyTextTv = (TextView) view.findViewById(R.id.only_text_tv);
        }
    }
}
