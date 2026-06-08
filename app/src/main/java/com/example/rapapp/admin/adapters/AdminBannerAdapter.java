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
import com.example.rapapp.models.Banner;

import java.util.List;

public class AdminBannerAdapter extends RecyclerView.Adapter<AdminBannerAdapter.ViewHolder> {

    private List<Banner> bannerList;
    private OnBannerClickListener editListener;
    private OnBannerClickListener deleteListener;

    public interface OnBannerClickListener {
        void onClick(Banner banner);
    }

    public void updateList(List<Banner> newList) {
        this.bannerList = newList;
        notifyDataSetChanged();
    }

    public AdminBannerAdapter(List<Banner> bannerList, OnBannerClickListener editListener, OnBannerClickListener deleteListener) {
        this.bannerList = bannerList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_banner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Banner banner = bannerList.get(position);

        if (banner.getContentBlocks() != null && !banner.getContentBlocks().isEmpty()) {
            holder.tvNewsId.setText("Nội dung (" + banner.getContentBlocks().size() + " khối)");
        } else {
            holder.tvNewsId.setText("Chỉ hiển thị ảnh");
        }

        Glide.with(holder.itemView.getContext())
                .load(banner.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivBannerImage);

        holder.btnEdit.setOnClickListener(v -> editListener.onClick(banner));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(banner));
    }

    @Override
    public int getItemCount() {
        return bannerList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBannerImage, btnEdit, btnDelete;
        TextView tvNewsId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBannerImage = itemView.findViewById(R.id.ivBannerImage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvNewsId = itemView.findViewById(R.id.tvNewsId);
        }
    }
}