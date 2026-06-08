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
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Map<String, String> movieNames = new HashMap<>();
    private Map<String, String> cinemaNames = new HashMap<>();

    public interface OnShowtimeClickListener {
        void onClick(Showtime showtime);
    }

    public void updateList(List<Showtime> newList) {
        this.showtimeList = newList;
        notifyDataSetChanged();
    }

    public AdminShowtimeAdapter(List<Showtime> showtimeList, OnShowtimeClickListener editListener, OnShowtimeClickListener deleteListener) {
        this.showtimeList = showtimeList;
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

        // Fetch Movie Name
        if (movieNames.containsKey(showtime.getMovieId())) {
            holder.tvMovieName.setText(movieNames.get(showtime.getMovieId()));
        } else {
            holder.tvMovieName.setText("Đang tải...");
            db.collection("movies").document(showtime.getMovieId()).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name = doc.getString("title");
                    movieNames.put(showtime.getMovieId(), name);
                    notifyItemChanged(position);
                }
            });
        }

        // Fetch Cinema Name
        if (cinemaNames.containsKey(showtime.getCinemaId())) {
            holder.tvCinemaName.setText(cinemaNames.get(showtime.getCinemaId()));
        } else {
            holder.tvCinemaName.setText("Đang tải...");
            db.collection("cinemas").document(showtime.getCinemaId()).get().addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    String name = doc.getString("name");
                    cinemaNames.put(showtime.getCinemaId(), name);
                    notifyItemChanged(position);
                }
            });
        }

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
