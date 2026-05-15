package com.example.rapapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;
import com.example.rapapp.models.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<News> newsList;

    public NewsAdapter(Context context, List<News> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.tvNewsTitle.setText(news.getTitle());

        Glide.with(context)
                .load(news.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .error(R.drawable.bg_placeholder)
                .into(holder.imgNews);

        // Chỉ hiển thị nội dung tóm tắt ở tab Nhân Vật (Character)
        if ("Character".equals(news.getCategory()) && news.getContent() != null) {
            holder.tvNewsContent.setVisibility(View.VISIBLE);
            holder.tvNewsContent.setText(news.getContent());
        } else {
            holder.tvNewsContent.setVisibility(View.GONE);
        }

        holder.btnReadMore.setOnClickListener(v -> {
            Toast.makeText(context, "Đọc thêm: " + news.getTitle(), Toast.LENGTH_SHORT).show();
        });

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Mở bài viết: " + news.getTitle(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return newsList != null ? newsList.size() : 0;
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imgNews;
        TextView tvNewsTitle, tvNewsContent, btnReadMore;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgNews = itemView.findViewById(R.id.imgNews);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvNewsContent = itemView.findViewById(R.id.tvNewsContent);
            btnReadMore = itemView.findViewById(R.id.btnReadMore);
        }
    }
}
