package com.example.rapapp.admin.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Movie;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdminMovieFormActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etDescription, etDuration, etRating, etAgeRating, etPrice, etReleaseDate, etPosterUrl, etTrailerUrl, etDirector, etCast;
    private TextView tvFormTitle, btnSave;
    private FirebaseFirestore db;
    private String movieId;
    private Calendar calendar;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_movie_form);

        db = FirebaseFirestore.getInstance();
        calendar = Calendar.getInstance();

        initViews();
        movieId = getIntent().getStringExtra("movieId");
        if (movieId != null) {
            tvFormTitle.setText("Chỉnh sửa phim");
            loadMovieData();
        }

        btnSave.setOnClickListener(v -> saveMovie());
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        etDuration = findViewById(R.id.etDuration);
        etRating = findViewById(R.id.etRating);
        etAgeRating = findViewById(R.id.etAgeRating);
        etPrice = findViewById(R.id.etPrice);
        etReleaseDate = findViewById(R.id.etReleaseDate);
        etPosterUrl = findViewById(R.id.etPosterUrl);
        etTrailerUrl = findViewById(R.id.etTrailerUrl);
        etDirector = findViewById(R.id.etDirector);
        etCast = findViewById(R.id.etCast);

        etReleaseDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etReleaseDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void loadMovieData() {
        db.collection("movies").document(movieId).get().addOnSuccessListener(documentSnapshot -> {
            Movie movie = documentSnapshot.toObject(Movie.class);
            if (movie != null) {
                etTitle.setText(movie.getTitle());
                etDescription.setText(movie.getDescription());
                etDuration.setText(String.valueOf(movie.getDuration()));
                etRating.setText(String.valueOf(movie.getRating()));
                etAgeRating.setText(movie.getAgeRating());
                etPrice.setText(String.valueOf((int) movie.getPrice()));
                if (movie.getReleaseDate() != null) {
                    calendar.setTime(movie.getReleaseDate().toDate());
                    etReleaseDate.setText(sdf.format(calendar.getTime()));
                }
                etPosterUrl.setText(movie.getPosterUrl());
                etTrailerUrl.setText(movie.getTrailerUrl());
                etDirector.setText(movie.getDirector());
                etCast.setText(movie.getCast());
            }
        });
    }

    private void saveMovie() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String ratingStr = etRating.getText().toString().trim();
        String ageRating = etAgeRating.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String posterUrl = etPosterUrl.getText().toString().trim();
        String trailerUrl = etTrailerUrl.getText().toString().trim();
        String director = etDirector.getText().toString().trim();
        String cast = etCast.getText().toString().trim();

        if (title.isEmpty() || posterUrl.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và link ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setDescription(description);
        movie.setDuration(durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr));
        movie.setRating(ratingStr.isEmpty() ? 0 : Double.parseDouble(ratingStr));
        movie.setAgeRating(ageRating);
        movie.setPrice(priceStr.isEmpty() ? 0 : Double.parseDouble(priceStr));
        movie.setReleaseDate(new Timestamp(calendar.getTime()));
        movie.setPosterUrl(posterUrl);
        movie.setTrailerUrl(trailerUrl);
        movie.setDirector(director);
        movie.setCast(cast);

        if (movieId != null) {
            db.collection("movies").document(movieId).set(movie)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            db.collection("movies").add(movie)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thêm phim thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
