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
    private TextView tabNowShowing, tabComingSoon;
    private ImageView btnBack;
    private boolean isNowShowingSelected = true;

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
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            onBackPressed();
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
