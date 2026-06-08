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
import com.example.rapapp.models.Movie;

import java.util.List;

public class AdminMovieAdapter extends RecyclerView.Adapter<AdminMovieAdapter.ViewHolder> {

    private List<Movie> movieList;
    private OnMovieClickListener editListener;
    private OnMovieClickListener deleteListener;

    public interface OnMovieClickListener {
        void onClick(Movie movie);
    }

    public void updateList(List<Movie> newList) {
        this.movieList = newList;
        notifyDataSetChanged();
    }

    public AdminMovieAdapter(List<Movie> movieList, OnMovieClickListener editListener, OnMovieClickListener deleteListener) {
        this.movieList = movieList;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_movie, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.tvMovieTitle.setText(movie.getTitle());
        holder.tvMovieRating.setText(String.valueOf(movie.getRating()));
        holder.tvMovieDuration.setText(movie.getDuration() + " Phút");

        Glide.with(holder.itemView.getContext())
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.bg_placeholder)
                .into(holder.ivMoviePoster);

        holder.btnEdit.setOnClickListener(v -> editListener.onClick(movie));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMoviePoster, btnEdit, btnDelete;
        TextView tvMovieTitle, tvMovieRating, tvMovieDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivMoviePoster = itemView.findViewById(R.id.ivMoviePoster);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvMovieTitle = itemView.findViewById(R.id.tvMovieTitle);
            tvMovieRating = itemView.findViewById(R.id.tvMovieRating);
            tvMovieDuration = itemView.findViewById(R.id.tvMovieDuration);
        }
    }
}
