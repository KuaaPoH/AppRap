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
import com.example.rapapp.models.Movie;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> movieList;

    public MovieAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        holder.txtTitle.setText(movie.getTitle());
        holder.txtRating.setText(String.valueOf(movie.getRating()));
        holder.txtAgeRating.setText(movie.getAgeRating());

        // Sử dụng một màu vàng cam duy nhất cho tất cả các nhãn độ tuổi (theo yêu cầu)
        holder.txtAgeRating.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F58020")));

        Glide.with(context)
                .load(movie.getPosterUrl())
                .diskCacheStrategy(com.bumptech.glide.load.engine.DiskCacheStrategy.ALL)
                .thumbnail(0.1f)
                .placeholder(R.drawable.bg_placeholder)
                .error(R.drawable.bg_placeholder)
                .into(holder.imgPoster);
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView txtTitle, txtRating, txtAgeRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtRating = itemView.findViewById(R.id.txtRating);
            txtAgeRating = itemView.findViewById(R.id.txtAgeRating);
        }
    }
}
