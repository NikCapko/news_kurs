package com.example.nikolay.news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nikolay.news.request.News;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ItemHolder> {

    private List<News> newsList;
    private Context context;

    private OnItemClickListener mOnItemClickListener;

    public Adapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescripyion;
        TextView tvView;
        ImageView ivLogo;

        public ItemHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescripyion = itemView.findViewById(R.id.tv_description);
            tvView = itemView.findViewById(R.id.tv_view);
            ivLogo = itemView.findViewById(R.id.iv_logo);
        }
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, null);
        return new ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        if (position == getItemCount() - 2) {
            mOnItemClickListener.loadMoreNews();
        }
        holder.tvTitle.setText(newsList.get(position).getTitle());
        holder.tvDescripyion.setText(newsList.get(position).getDescription());
        holder.tvView.setBackground(context.getResources().getDrawable(R.drawable.shape_round_blue));
        switch (newsList.get(position).getResource()) {
            case 1:
                holder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lenta));
                break;
            case 2:
                holder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_yandex));
                break;
            case 3:
                holder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tass));
                break;
            case 4:
                holder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rambler));
                break;
        }
        holder.tvView.setText(String.valueOf(newsList.get(position).getViewed()));
        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public interface OnItemClickListener {

        void onItemClick(int id);

        void loadMoreNews();
    }
}

