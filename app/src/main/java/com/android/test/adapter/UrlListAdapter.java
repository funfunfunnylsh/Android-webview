package com.android.test.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.test.R;
import com.android.test.utils.HtmlCacheUtil;

import java.util.List;

/**
 * Created by liusonghao
 * 2020.1.10
 *
 */
public class UrlListAdapter extends RecyclerView.Adapter<UrlListAdapter.MyViewHolder> {
    private List<String> mUrlList;
    private Context mContext;

    public UrlListAdapter(Context context,List<String> urlList) {
        mUrlList = urlList;
        mContext = context;
    }

    @NonNull
    @Override
    public UrlListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final UrlListAdapter.MyViewHolder holder, int position) {
        if (HtmlCacheUtil.getInstance().getCache(mUrlList.get(position)) != null) {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.flag.setVisibility(View.VISIBLE);
        }else{
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.flag.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.getLayoutPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUrlList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }
    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private ProgressBar mProgressBar;
        private ImageView flag;

        public MyViewHolder(View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            mProgressBar = itemView.findViewById(R.id.progress);
            flag = itemView.findViewById(R.id.flag);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
