package com.example.rapapp.adapters;

import android.content.Context;
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

public class CinemaAdapter extends RecyclerView.Adapter<CinemaAdapter.CinemaViewHolder> {

    private Context context;
    private List<Cinema> cinemas;

    public CinemaAdapter(Context context, List<Cinema> cinemas) {
        this.context = context;
        this.cinemas = cinemas;
    }

    @NonNull
    @Override
    public CinemaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cinema, parent, false);
        return new CinemaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CinemaViewHolder holder, int position) {
        Cinema cinema = cinemas.get(position);
        holder.tvName.setText(cinema.getName());
        holder.tvAddress.setText(cinema.getAddress());
        holder.tvPhone.setText(cinema.getPhone());

        Glide.with(context)
                .load(cinema.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivCinema);
    }

    @Override
    public int getItemCount() {
        return cinemas.size();
    }

    public static class CinemaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCinema;
        TextView tvName, tvAddress, tvPhone;

        public CinemaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCinema = itemView.findViewById(R.id.ivCinema);
            tvName = itemView.findViewById(R.id.tvCinemaName);
            tvAddress = itemView.findViewById(R.id.tvCinemaAddress);
            tvPhone = itemView.findViewById(R.id.tvCinemaPhone);
        }
    }
}