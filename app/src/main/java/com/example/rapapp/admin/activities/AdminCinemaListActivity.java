package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminCinemaAdapter;
import com.example.rapapp.models.Cinema;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminCinemaListActivity extends AppCompatActivity {

    private RecyclerView rvAdminCinemas;
    private AdminCinemaAdapter adapter;
    private List<Cinema> cinemaList;
    private List<Cinema> fullCinemaList = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cinema_list);

        db = FirebaseFirestore.getInstance();
        cinemaList = new ArrayList<>();

        initViews();
        setupSearch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCinemas();
    }

    private void setupSearch() {
        android.widget.EditText etSearch = findViewById(R.id.etSearchCinema);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterCinemas(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterCinemas(String query) {
        List<Cinema> filteredList = new ArrayList<>();
        for (Cinema cinema : fullCinemaList) {
            if (cinema.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(cinema);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        rvAdminCinemas = findViewById(R.id.rvAdminCinemas);
        rvAdminCinemas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminCinemaAdapter(cinemaList, cinema -> {
            Intent intent = new Intent(this, AdminCinemaFormActivity.class);
            intent.putExtra("cinemaId", cinema.getId());
            startActivity(intent);
        }, this::confirmDelete);
        rvAdminCinemas.setAdapter(adapter);

        ImageView btnAddCinema = findViewById(R.id.btnAddCinema);
        btnAddCinema.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminCinemaFormActivity.class));
        });
    }

    private void loadCinemas() {
        db.collection("cinemas").get().addOnSuccessListener(value -> {
            if (value != null) {
                cinemaList.clear();
                fullCinemaList.clear();
                for (int i = 0; i < value.getDocuments().size(); i++) {
                    Cinema cinema = value.getDocuments().get(i).toObject(Cinema.class);
                    if (cinema != null) {
                        cinema.setId(value.getDocuments().get(i).getId());
                        cinemaList.add(cinema);
                        fullCinemaList.add(cinema);
                    }
                }
                android.widget.EditText etSearch = findViewById(R.id.etSearchCinema);
                filterCinemas(etSearch.getText().toString());
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void confirmDelete(Cinema cinema) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa rạp này không?")
                .setPositiveButton("Xóa", (dialog, which) -> deleteCinema(cinema))
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCinema(Cinema cinema) {
        db.collection("cinemas").document(cinema.getId()).delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đã xoá rạp", Toast.LENGTH_SHORT).show();
                    loadCinemas();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi xoá: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
