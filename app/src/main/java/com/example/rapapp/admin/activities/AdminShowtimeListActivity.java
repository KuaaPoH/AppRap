package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminShowtimeAdapter;
import com.example.rapapp.models.Showtime;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminShowtimeListActivity extends AppCompatActivity {

    private RecyclerView rvAdminShowtimes;
    private AdminShowtimeAdapter adapter;
    private List<Showtime> showtimeList;
    private List<Showtime> fullShowtimeList = new ArrayList<>();
    private FirebaseFirestore db;
    private Map<String, String> movieNamesMap = new HashMap<>();
    private Map<String, String> cinemaNamesMap = new HashMap<>();
    private ListenerRegistration showtimeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_showtime_list);

        db = FirebaseFirestore.getInstance();
        showtimeList = new ArrayList<>();

        initViews();
        setupSearch();
        preLoadDataAndListen();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showtimeListener != null) {
            showtimeListener.remove();
        }
    }

    private void setupSearch() {
        android.widget.EditText etSearch = findViewById(R.id.etSearchShowtime);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterShowtimes(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterShowtimes(String query) {
        List<Showtime> filteredList = new ArrayList<>();
        for (Showtime showtime : fullShowtimeList) {
            String movieName = movieNamesMap.get(showtime.getMovieId());
            String cinemaName = cinemaNamesMap.get(showtime.getCinemaId());
            
            boolean matchesMovie = movieName != null && movieName.toLowerCase().contains(query.toLowerCase());
            boolean matchesCinema = cinemaName != null && cinemaName.toLowerCase().contains(query.toLowerCase());
            boolean matchesDate = showtime.getDate().toLowerCase().contains(query.toLowerCase());
            boolean matchesFormat = showtime.getFormat().toLowerCase().contains(query.toLowerCase());

            if (matchesMovie || matchesCinema || matchesDate || matchesFormat) {
                filteredList.add(showtime);
            }
        }
        if (adapter != null) {
            adapter.updateList(filteredList);
        }
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        rvAdminShowtimes = findViewById(R.id.rvAdminShowtimes);
        rvAdminShowtimes.setLayoutManager(new LinearLayoutManager(this));

        android.widget.ImageView btnAddShowtime = findViewById(R.id.btnAddShowtime);
        btnAddShowtime.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminShowtimeFormActivity.class));
        });
    }

    private void preLoadDataAndListen() {
        // 1. Load Movie Names
        db.collection("movies").get().addOnSuccessListener(movieDocs -> {
            for (com.google.firebase.firestore.DocumentSnapshot doc : movieDocs) {
                movieNamesMap.put(doc.getId(), doc.getString("title"));
            }

            // 2. Load Cinema Names
            db.collection("cinemas").get().addOnSuccessListener(cinemaDocs -> {
                for (com.google.firebase.firestore.DocumentSnapshot doc : cinemaDocs) {
                    cinemaNamesMap.put(doc.getId(), doc.getString("name"));
                }

                // 3. Setup Adapter and Start Real-time Listening
                setupAdapter();
                startListeningShowtimes();
            });
        });
    }

    private void setupAdapter() {
        adapter = new AdminShowtimeAdapter(showtimeList, movieNamesMap, cinemaNamesMap, showtime -> {
            Intent intent = new Intent(this, AdminShowtimeFormActivity.class);
            intent.putExtra("showtimeId", showtime.getId());
            startActivity(intent);
        }, showtime -> {
            confirmDelete(showtime);
        });
        rvAdminShowtimes.setAdapter(adapter);
    }

    private void startListeningShowtimes() {
        if (showtimeListener != null) showtimeListener.remove();

        showtimeListener = db.collection("showtimes")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải suất chiếu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        fullShowtimeList.clear();
                        for (com.google.firebase.firestore.DocumentSnapshot doc : value.getDocuments()) {
                            Showtime showtime = doc.toObject(Showtime.class);
                            if (showtime != null) {
                                showtime.setId(doc.getId());
                                fullShowtimeList.add(showtime);
                            }
                        }
                        android.widget.EditText etSearch = findViewById(R.id.etSearchShowtime);
                        filterShowtimes(etSearch.getText().toString());
                    }
                });
    }

    private void confirmDelete(Showtime showtime) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xoá")
                .setMessage("Bạn có chắc chắn muốn xoá suất chiếu này không?")
                .setPositiveButton("Xoá", (dialog, which) -> deleteShowtime(showtime))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteShowtime(Showtime showtime) {
        db.collection("showtimes").document(showtime.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xoá suất chiếu thành công", Toast.LENGTH_SHORT).show();
                    // Listener will automatically update the UI
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
