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
import com.example.rapapp.models.Cinema;

import java.util.List;

public class AdminCinemaAdapter extends RecyclerView.Adapter<AdminCinemaAdapter.ViewHolder> {

    private List<Cinema> cinemaList;
    private OnCinemaClickListener editListener;
    private OnCinemaClickListener deleteListener;

    public interface OnCinemaClickListener {
        void onClick(Cinema cinema);
    }

    public void updateList(List<Cinema> newList) {
        this.cinemaList = newList;
        notifyDataSetChanged();
    }

    public AdminCinemaAdapter(List<Cinema> cinemaList, OnCinemaClickListener editListener, OnCinemaClickListener deleteListener) {
        this.cinemaList = cinemaList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_cinema, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cinema cinema = cinemaList.get(position);
        holder.tvCinemaName.setText(cinema.getName());
        holder.tvCinemaAddress.setText(cinema.getAddress());
        holder.tvCinemaCity.setText(cinema.getCity());

        Glide.with(holder.itemView.getContext())
                .load(cinema.getImageUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivCinemaImage);

        holder.btnEdit.setOnClickListener(v -> editListener.onClick(cinema));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(cinema));
    }

    @Override
    public int getItemCount() {
        return cinemaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCinemaImage, btnEdit, btnDelete;
        TextView tvCinemaName, tvCinemaAddress, tvCinemaCity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCinemaImage = itemView.findViewById(R.id.ivCinemaImage);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvCinemaAddress = itemView.findViewById(R.id.tvCinemaAddress);
            tvCinemaCity = itemView.findViewById(R.id.tvCinemaCity);
        }
    }
}
