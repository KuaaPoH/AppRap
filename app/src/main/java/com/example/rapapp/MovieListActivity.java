package com.example.rapapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.adapters.MovieAdapter;
import com.example.rapapp.models.Movie;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MovieListActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> movies; // List for display
    private List<Movie> allMovies; // All movies from Firebase
    private TextView tabNowShowing, tabComingSoon, tvLocation;
    private ImageView btnBack;
    private boolean isNowShowingSelected = true;
    private String selectedLocation = "Toàn quốc";
    private final String[] locations = {
        "Toàn quốc", "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_movie_list);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.movie_list_main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMovies = findViewById(R.id.rvMovies);
        tabNowShowing = findViewById(R.id.tabNowShowing);
        tabComingSoon = findViewById(R.id.tabComingSoon);
        tvLocation = findViewById(R.id.tvLocation);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        tvLocation.setOnClickListener(v -> {
            // Hiệu ứng click: Thu nhỏ nhẹ rồi mở dialog
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                showLocationPickerDialog();
            }).start();
        });

        // Initialize lists
        movies = new ArrayList<>();
        allMovies = new ArrayList<>();
        
        movieAdapter = new MovieAdapter(this, movies);
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        rvMovies.setAdapter(movieAdapter);

        // Tab events
        tabNowShowing.setOnClickListener(v -> {
            if (!isNowShowingSelected) {
                isNowShowingSelected = true;
                updateTabUI();
                filterMovies();
            }
        });

        tabComingSoon.setOnClickListener(v -> {
            if (isNowShowingSelected) {
                isNowShowingSelected = false;
                updateTabUI();
                filterMovies();
            }
        });

        // Load data
        loadMoviesFromFirebase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showLocationPickerDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        bottomSheetDialog.setContentView(view);

        android.widget.NumberPicker picker = view.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(locations.length - 1);
        picker.setDisplayedValues(locations);

        // Set current value
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
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
    }

    private void updateTabUI() {
        float activeScale = 1.05f;
        float inactiveScale = 1.0f;
        int activeColor = android.graphics.Color.parseColor("#034EA2");
        int inactiveColor = android.graphics.Color.parseColor("#888888");
        long duration = 200;

        tabNowShowing.setTypeface(null, android.graphics.Typeface.BOLD);
        tabComingSoon.setTypeface(null, android.graphics.Typeface.BOLD);

        if (isNowShowingSelected) {
            tabNowShowing.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabNowShowing.setTextColor(activeColor);

            tabComingSoon.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabComingSoon.setTextColor(inactiveColor);
        } else {
            tabNowShowing.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabNowShowing.setTextColor(inactiveColor);

            tabComingSoon.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabComingSoon.setTextColor(activeColor);
        }
    }

    private void filterMovies() {
        movies.clear();
        com.google.firebase.Timestamp now = com.google.firebase.Timestamp.now();

        for (Movie movie : allMovies) {
            if (movie.getReleaseDate() == null) continue;
            
            if (isNowShowingSelected) {
                if (movie.getReleaseDate().compareTo(now) <= 0) {
                    movies.add(movie);
                }
            } else {
                if (movie.getReleaseDate().compareTo(now) > 0) {
                    movies.add(movie);
                }
            }
        }
        movieAdapter.notifyDataSetChanged();
    }

    private void loadMoviesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("movies")
          .orderBy("releaseDate", Query.Direction.DESCENDING)
          .get()
          .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allMovies.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    allMovies.add(movie);
                }
                filterMovies();
            } else {
                Log.e("MovieListActivity", "Error loading movies", task.getException());
                Toast.makeText(this, "Không thể tải danh sách phim", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
