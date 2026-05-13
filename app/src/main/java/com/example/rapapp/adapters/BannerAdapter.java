package com.example.rapapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<String> bannerUrls;

    public BannerAdapter(List<String> bannerUrls) {
        this.bannerUrls = bannerUrls;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(bannerUrls.get(position))
                .into(holder.imgBanner);
    }

    @Override
    public int getItemCount() {
        return bannerUrls != null ? bannerUrls.size() : 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}