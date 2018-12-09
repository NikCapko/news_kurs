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

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<News> newsList;
    Context context;

    private OnItemClickListener mOnItemClickListener;

    public Adapter(List<News> newsList, Context context) {
        this.newsList = newsList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDescripyion;
        TextView tvView;
        ImageView ivLogo;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDescripyion = itemView.findViewById(R.id.tv_description);
            tvView = itemView.findViewById(R.id.tv_view);
            ivLogo = itemView.findViewById(R.id.iv_logo);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).tvTitle.setText(newsList.get(position).getTitle());
        ((ViewHolder) holder).tvDescripyion.setText(newsList.get(position).getDescription());
        switch (newsList.get(position).getResource()) {
            case 1:
                ((ViewHolder) holder).ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_lenta));
                break;
            case 2:
                ((ViewHolder) holder).ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_yandex));
                break;
            case 3:
                ((ViewHolder) holder).ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_tass));
                break;
            case 4:
                ((ViewHolder) holder).ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_rambler));
                break;
        }
        final int id = newsList.get(position).getId();
        holder.itemView.setOnClickListener(view -> {
            if (mOnItemClickListener != null) {
                ((ViewHolder) holder).tvView.setText(newsList.get(position).getViewed() + 1);
                ((ViewHolder) holder).tvView.setBackground(context.getResources().getDrawable(R.drawable.shape_round_green));
                mOnItemClickListener.onItemClick(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public interface OnItemClickListener {

        void onItemClick(int id);
    }
}

