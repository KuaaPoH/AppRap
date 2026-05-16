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
    private List<String> locations = new ArrayList<>();

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
        loadLocationsFromFirebase();
    }

    private void loadLocationsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("metadata").document("locations").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> locList = (List<String>) documentSnapshot.get("list");
                if (locList != null) {
                    locations.clear();
                    locations.addAll(locList);
                }
            }
        });
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
        if (locations.isEmpty()) {
            locations.add("Toàn quốc");
        }
        picker.setMinValue(0);
        picker.setMaxValue(locations.size() - 1);
        picker.setDisplayedValues(locations.toArray(new String[0]));

        // Set current value
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).equals(selectedLocation)) {
                picker.setValue(i);
                break;
            }
        }

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedLocation = locations.get(picker.getValue());
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
