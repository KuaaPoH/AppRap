package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Cinema;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView tvMovieCount, tvCinemaCount, tvShowtimeCount, tvProductCount, tvTotalRevenue;
    private AutoCompleteTextView spinnerCinemaFilter;
    private android.widget.Button btnDateFilter;
    private List<Cinema> cinemaList = new ArrayList<>();
    private String selectedCinemaId = null; // null means "All Cinemas"
    private String selectedDate = null; // null means "All Dates"
    private DecimalFormat df = new DecimalFormat("###,### VNĐ");
    private java.util.Calendar calendar = java.util.Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupClickListeners();
        loadCinemasForFilter();
        loadStatistics();
    }

    private void initViews() {
        tvMovieCount = findViewById(R.id.tvMovieCount);
        tvCinemaCount = findViewById(R.id.tvCinemaCount);
        tvShowtimeCount = findViewById(R.id.tvShowtimeCount);
        tvProductCount = findViewById(R.id.tvProductCount);
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        spinnerCinemaFilter = findViewById(R.id.spinnerCinemaFilter);
        btnDateFilter = findViewById(R.id.btnDateFilter);

        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnDateFilter != null) {
            btnDateFilter.setOnClickListener(v -> showDatePicker());
            btnDateFilter.setOnLongClickListener(v -> {
                selectedDate = null;
                btnDateFilter.setText("Ngày Lọc");
                loadStatistics();
                Toast.makeText(this, "Đã bỏ lọc theo ngày", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void showDatePicker() {
        new android.app.DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(java.util.Calendar.YEAR, year);
            calendar.set(java.util.Calendar.MONTH, month);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            selectedDate = sdf.format(calendar.getTime());
            btnDateFilter.setText(selectedDate);
            loadStatistics();
        }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
    }

    private void setupClickListeners() {
        findViewById(R.id.cardManageMovies).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminMovieListActivity.class));
        });

        findViewById(R.id.cardManageCinemas).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminCinemaListActivity.class));
        });

        findViewById(R.id.cardManageShowtimes).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminShowtimeListActivity.class));
        });

        findViewById(R.id.cardManageProducts).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminProductListActivity.class));
        });

        findViewById(R.id.cardManageRooms).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminRoomListActivity.class));
        });

        findViewById(R.id.cardManageBanners).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminBannerListActivity.class));
        });

        findViewById(R.id.cardManageNews).setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AdminNewsListActivity.class));
        });

        findViewById(R.id.cardRevenue).setOnClickListener(v -> {
            showRevenueDetail();
        });
    }

    private void loadCinemasForFilter() {
        db.collection("cinemas").get().addOnSuccessListener(queryDocumentSnapshots -> {
            cinemaList = queryDocumentSnapshots.toObjects(Cinema.class);
            List<String> cinemaNames = new ArrayList<>();
            cinemaNames.add("Tất cả rạp");
            
            for (int i = 0; i < cinemaList.size(); i++) {
                cinemaList.get(i).setId(queryDocumentSnapshots.getDocuments().get(i).getId());
                cinemaNames.add(cinemaList.get(i).getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cinemaNames);
            spinnerCinemaFilter.setAdapter(adapter);
            spinnerCinemaFilter.setOnItemClickListener((parent, view, position, id) -> {
                if (position == 0) {
                    selectedCinemaId = null;
                } else {
                    selectedCinemaId = cinemaList.get(position - 1).getId();
                }
                loadStatistics();
            });
        });
    }

    private void loadStatistics() {
        // 1. Movie Count (Global)
        db.collection("movies").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvMovieCount.setText(String.valueOf(queryDocumentSnapshots.size()));
        });

        // 2. Cinema Count (Global)
        db.collection("cinemas").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvCinemaCount.setText(String.valueOf(queryDocumentSnapshots.size()));
        });

        // 3. Showtime Count (Filtered)
        Query showtimeQuery = db.collection("showtimes");
        if (selectedCinemaId != null) {
            showtimeQuery = showtimeQuery.whereEqualTo("cinemaId", selectedCinemaId);
        }
        if (selectedDate != null) {
            showtimeQuery = showtimeQuery.whereEqualTo("date", selectedDate);
        }
        showtimeQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvShowtimeCount.setText(String.valueOf(queryDocumentSnapshots.size()));
        });

        // 4. Product Count (Global)
        db.collection("products").get().addOnSuccessListener(queryDocumentSnapshots -> {
            tvProductCount.setText(String.valueOf(queryDocumentSnapshots.size()));
        });

        // 5. Total Revenue (Filtered)
        Query revenueQuery = db.collection("bookings");
        if (selectedCinemaId != null) {
            revenueQuery = revenueQuery.whereEqualTo("cinemaId", selectedCinemaId);
        }
        revenueQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            double total = 0;
            for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                // Lọc ngày trên client cho bookings vì timestamp là kiểu Date/Timestamp
                if (selectedDate != null) {
                    com.google.firebase.Timestamp timestamp = doc.getTimestamp("timestamp");
                    if (timestamp != null) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                        String bookingDate = sdf.format(timestamp.toDate());
                        if (!bookingDate.equals(selectedDate)) {
                            continue;
                        }
                    }
                }
                
                Double price = doc.getDouble("totalPrice");
                if (price != null) total += price;
            }
            tvTotalRevenue.setText(df.format(total));
        });
    }

    private void showRevenueDetail() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.dialog_admin_revenue_detail);

        TextView tvDetailTitle = bottomSheetDialog.findViewById(R.id.tvDetailTitle);
        TextView tvDetailValue = bottomSheetDialog.findViewById(R.id.tvDetailValue);
        
        if (tvDetailTitle != null) {
            tvDetailTitle.setText(selectedCinemaId == null ? "Doanh thu Toàn quốc" : "Doanh thu tại Rạp");
        }
        if (tvDetailValue != null) {
            tvDetailValue.setText(tvTotalRevenue.getText());
        }

        View btnClose = bottomSheetDialog.findViewById(R.id.btnClose);
        if (btnClose != null) {
            btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());
        }

        bottomSheetDialog.show();
    }
}
