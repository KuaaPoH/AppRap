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

        // Ẩn nội dung tóm tắt ở màn hình danh sách theo yêu cầu
        holder.tvNewsContent.setVisibility(View.GONE);

        holder.btnReadMore.setOnClickListener(v -> {
            openNewsDetail(news.getId());
        });

        holder.itemView.setOnClickListener(v -> {
            openNewsDetail(news.getId());
        });
    }

    private void openNewsDetail(String newsId) {
        android.content.Intent intent = new android.content.Intent(context, com.example.rapapp.NewsDetailActivity.class);
        intent.putExtra("newsId", newsId);
        context.startActivity(intent);
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
