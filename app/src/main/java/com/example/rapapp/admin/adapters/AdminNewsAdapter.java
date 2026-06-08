package com.example.rapapp.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;
import com.example.rapapp.models.News;

import java.util.List;

public class AdminNewsAdapter extends RecyclerView.Adapter<AdminNewsAdapter.ViewHolder> {

    private List<News> newsList;
    private OnNewsClickListener editListener;
    private OnNewsClickListener deleteListener;

    public interface OnNewsClickListener {
        void onClick(News news);
    }

    public void updateList(List<News> newList) {
        this.newsList = newList;
        notifyDataSetChanged();
    }

    public AdminNewsAdapter(List<News> newsList, OnNewsClickListener editListener, OnNewsClickListener deleteListener) {
        this.newsList = newsList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.tvNewsTitle.setText(news.getTitle());
        holder.tvNewsCategory.setVisibility(View.GONE); // Ẩn hoàn toàn trường này đi

        Glide.with(holder.itemView.getContext())
                .load(news.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivNewsImage);

        holder.btnEdit.setOnClickListener(v -> editListener.onClick(news));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(news));
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivNewsImage, btnEdit, btnDelete;
        TextView tvNewsTitle, tvNewsCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivNewsImage = itemView.findViewById(R.id.ivNewsImage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvNewsTitle = itemView.findViewById(R.id.tvNewsTitle);
            tvNewsCategory = itemView.findViewById(R.id.tvNewsCategory);
        }
    }
}