package com.example.rapapp.admin.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.models.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminShowtimeAdapter extends RecyclerView.Adapter<AdminShowtimeAdapter.ViewHolder> {

    private List<Showtime> showtimeList;
    private OnShowtimeClickListener editListener;
    private OnShowtimeClickListener deleteListener;
    private Map<String, String> movieNames;
    private Map<String, String> cinemaNames;

    public interface OnShowtimeClickListener {
        void onClick(Showtime showtime);
    }

    public void updateList(List<Showtime> newList) {
        this.showtimeList = newList;
        notifyDataSetChanged();
    }

    public AdminShowtimeAdapter(List<Showtime> showtimeList, 
                               Map<String, String> movieNames, 
                               Map<String, String> cinemaNames,
                               OnShowtimeClickListener editListener, 
                               OnShowtimeClickListener deleteListener) {
        this.showtimeList = showtimeList;
        this.movieNames = movieNames;
        this.cinemaNames = cinemaNames;
        this.editListener = editListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_showtime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Showtime showtime = showtimeList.get(position);
        holder.tvShowtimeDate.setText(showtime.getDate());
        holder.tvShowtimeTime.setText(showtime.getTime());
        holder.tvShowtimeFormat.setText(showtime.getFormat());

        // Use pre-loaded Movie Name
        String movieName = movieNames.get(showtime.getMovieId());
        holder.tvMovieName.setText(movieName != null ? movieName : "Không xác định");

        // Use pre-loaded Cinema Name
        String cinemaName = cinemaNames.get(showtime.getCinemaId());
        holder.tvCinemaName.setText(cinemaName != null ? cinemaName : "Không xác định");

        holder.btnEdit.setOnClickListener(v -> editListener.onClick(showtime));
        holder.btnDelete.setOnClickListener(v -> deleteListener.onClick(showtime));
    }

    @Override
    public int getItemCount() {
        return showtimeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnEdit, btnDelete;
        TextView tvMovieName, tvCinemaName, tvShowtimeDate, tvShowtimeTime, tvShowtimeFormat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            tvMovieName = itemView.findViewById(R.id.tvMovieName);
            tvCinemaName = itemView.findViewById(R.id.tvCinemaName);
            tvShowtimeDate = itemView.findViewById(R.id.tvShowtimeDate);
            tvShowtimeTime = itemView.findViewById(R.id.tvShowtimeTime);
            tvShowtimeFormat = itemView.findViewById(R.id.tvShowtimeFormat);
        }
    }
}
