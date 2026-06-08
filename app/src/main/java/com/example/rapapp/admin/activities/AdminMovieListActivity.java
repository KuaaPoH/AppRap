package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminMovieAdapter;
import com.example.rapapp.models.Movie;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminMovieListActivity extends AppCompatActivity {

    private RecyclerView rvAdminMovies;
    private AdminMovieAdapter adapter;
    private List<Movie> movieList;
    private FirebaseFirestore db;

    private List<Movie> fullMovieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_movie_list);

        db = FirebaseFirestore.getInstance();
        movieList = new ArrayList<>();

        initViews();
        setupSearch();
        loadMovies();
    }

    private void setupSearch() {
        android.widget.EditText etSearch = findViewById(R.id.etSearchMovie);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterMovies(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });
    }

    private void filterMovies(String query) {
        List<Movie> filteredList = new ArrayList<>();
        for (Movie movie : fullMovieList) {
            if (movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(movie);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvAdminMovies = findViewById(R.id.rvAdminMovies);
        rvAdminMovies.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminMovieAdapter(movieList, movie -> {
            // Edit movie
            Intent intent = new Intent(this, AdminMovieFormActivity.class);
            intent.putExtra("movieId", movie.getId());
            startActivity(intent);
        }, movie -> {
            // Delete movie
            deleteMovie(movie);
        });
        rvAdminMovies.setAdapter(adapter);

        ImageView btnAddMovie = findViewById(R.id.btnAddMovie);
        btnAddMovie.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminMovieFormActivity.class);
            startActivity(intent);
        });
    }

    private void loadMovies() {
        db.collection("movies")
                .orderBy("releaseDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        movieList.clear();
                        fullMovieList.clear();
                        List<Movie> objects = value.toObjects(Movie.class);
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            objects.get(i).setId(value.getDocuments().get(i).getId());
                        }
                        movieList.addAll(objects);
                        fullMovieList.addAll(objects);
                        
                        android.widget.EditText etSearch = findViewById(R.id.etSearchMovie);
                        filterMovies(etSearch.getText().toString());
                    }
                });
    }

    private void deleteMovie(Movie movie) {
        db.collection("movies").document(movie.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá phim", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show());
    }
}
