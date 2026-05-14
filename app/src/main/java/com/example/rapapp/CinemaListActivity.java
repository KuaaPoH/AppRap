package com.example.rapapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.adapters.CinemaAdapter;
import com.example.rapapp.models.Cinema;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CinemaListActivity extends AppCompatActivity {

    private RecyclerView rvCinemas;
    private CinemaAdapter cinemaAdapter;
    private List<Cinema> allCinemas;
    private List<Cinema> filteredCinemas;
    private TextView tvLocation;
    private String selectedLocation = "Toàn quốc";
    private final String[] locations = {
        "Toàn quốc", "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cinema_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cinema_list_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvCinemas = findViewById(R.id.rvCinemas);
        tvLocation = findViewById(R.id.tvLocation);

        allCinemas = new ArrayList<>();
        filteredCinemas = new ArrayList<>();
        cinemaAdapter = new CinemaAdapter(this, filteredCinemas);
        rvCinemas.setLayoutManager(new LinearLayoutManager(this));
        rvCinemas.setAdapter(cinemaAdapter);

        setupBottomNavigation();

        tvLocation.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                showLocationPickerDialog();
            }).start();
        });

        loadCinemasFromFirebase();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        bottomNav.setSelectedItemId(R.id.nav_cinema);
        
        // Vô hiệu hóa Tooltip
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            View menuView = bottomNav.findViewById(bottomNav.getMenu().getItem(i).getItemId());
            if (menuView != null) {
                menuView.setOnLongClickListener(v -> true);
            }
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (id == R.id.nav_cinema) {
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav.getSelectedItemId() != R.id.nav_cinema) {
            bottomNav.setSelectedItemId(R.id.nav_cinema);
        }
    }

    private void showLocationPickerDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        bottomSheetDialog.setContentView(view);

        android.widget.NumberPicker picker = view.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(locations.length - 1);
        picker.setDisplayedValues(locations);

        for (int i = 0; i < locations.length; i++) {
            if (locations[i].equals(selectedLocation)) {
                picker.setValue(i);
                break;
            }
        }

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedLocation = locations[picker.getValue()];
            tvLocation.setText(selectedLocation);
            bottomSheetDialog.dismiss();
            filterCinemas();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void loadCinemasFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cinemas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allCinemas.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Cinema cinema = document.toObject(Cinema.class);
                    cinema.setId(document.getId());
                    allCinemas.add(cinema);
                }
                filterCinemas();
            } else {
                Log.e("CinemaListActivity", "Error loading cinemas", task.getException());
            }
        });
    }

    private void filterCinemas() {
        filteredCinemas.clear();
        if (selectedLocation.equals("Toàn quốc")) {
            filteredCinemas.addAll(allCinemas);
        } else {
            for (Cinema cinema : allCinemas) {
                if (cinema.getCity() != null && cinema.getCity().equals(selectedLocation)) {
                    filteredCinemas.add(cinema);
                }
            }
        }
        cinemaAdapter.notifyDataSetChanged();
    }
}