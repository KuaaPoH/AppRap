package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminShowtimeAdapter;
import com.example.rapapp.models.Showtime;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminShowtimeListActivity extends AppCompatActivity {

    private RecyclerView rvAdminShowtimes;
    private AdminShowtimeAdapter adapter;
    private List<Showtime> showtimeList;
    private List<Showtime> fullShowtimeList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_showtime_list);

        db = FirebaseFirestore.getInstance();
        showtimeList = new ArrayList<>();

        initViews();
        setupSearch();
        loadShowtimes();
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
            // Filter by date or format since movie/cinema names are loaded async
            if (showtime.getDate().toLowerCase().contains(query.toLowerCase()) || 
                showtime.getFormat().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(showtime);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvAdminShowtimes = findViewById(R.id.rvAdminShowtimes);
        rvAdminShowtimes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminShowtimeAdapter(showtimeList, showtime -> {
            Intent intent = new Intent(this, AdminShowtimeFormActivity.class);
            intent.putExtra("showtimeId", showtime.getId());
            startActivity(intent);
        }, showtime -> {
            deleteShowtime(showtime);
        });
        rvAdminShowtimes.setAdapter(adapter);

        android.widget.ImageView btnAddShowtime = findViewById(R.id.btnAddShowtime);
        btnAddShowtime.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminShowtimeFormActivity.class));
        });
    }

    private void loadShowtimes() {
        db.collection("showtimes")
                .orderBy("date", Query.Direction.DESCENDING)
                .orderBy("time", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        showtimeList.clear();
                        fullShowtimeList.clear();
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            Showtime showtime = value.getDocuments().get(i).toObject(Showtime.class);
                            if (showtime != null) {
                                showtime.setId(value.getDocuments().get(i).getId());
                                showtimeList.add(showtime);
                                fullShowtimeList.add(showtime);
                            }
                        }
                        android.widget.EditText etSearch = findViewById(R.id.etSearchShowtime);
                        filterShowtimes(etSearch.getText().toString());
                    }
                });
    }

    private void deleteShowtime(Showtime showtime) {
        db.collection("showtimes").document(showtime.getId()).delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá suất chiếu", Toast.LENGTH_SHORT).show());
    }
}
