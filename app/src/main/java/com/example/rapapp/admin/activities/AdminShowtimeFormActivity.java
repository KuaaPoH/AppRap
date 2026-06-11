package com.example.rapapp.admin.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Cinema;
import com.example.rapapp.models.Movie;
import com.example.rapapp.models.Showtime;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdminShowtimeFormActivity extends AppCompatActivity {

    private AutoCompleteTextView spinnerMovie, spinnerCinema, spinnerRoom;
    private TextInputEditText etDate, etTime, etFormat;
    private TextView tvFormTitle, btnSave;
    private FirebaseFirestore db;
    private String showtimeId;
    
    private List<Movie> movieList = new ArrayList<>();
    private List<Cinema> cinemaList = new ArrayList<>();
    private List<com.example.rapapp.models.Room> roomList = new ArrayList<>();
    private String selectedMovieId, selectedCinemaId, selectedCity, selectedRoomId;
    
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_showtime_form);

        db = FirebaseFirestore.getInstance();

        initViews();
        loadMoviesAndCinemas();
        
        showtimeId = getIntent().getStringExtra("showtimeId");
        if (showtimeId != null) {
            tvFormTitle.setText("Chỉnh sửa suất chiếu");
            loadShowtimeData();
        }

        btnSave.setOnClickListener(v -> saveShowtime());
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        spinnerMovie = findViewById(R.id.spinnerMovie);
        spinnerCinema = findViewById(R.id.spinnerCinema);
        spinnerRoom = findViewById(R.id.spinnerRoom);
        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etFormat = findViewById(R.id.etFormat);

        etDate.setOnClickListener(v -> showDatePicker());
        etTime.setOnClickListener(v -> showTimePicker());
    }

    private void loadMoviesAndCinemas() {
        db.collection("movies").get().addOnSuccessListener(queryDocumentSnapshots -> {
            movieList = queryDocumentSnapshots.toObjects(Movie.class);
            List<String> movieTitles = new ArrayList<>();
            for (int i = 0; i < movieList.size(); i++) {
                movieList.get(i).setId(queryDocumentSnapshots.getDocuments().get(i).getId());
                movieTitles.add(movieList.get(i).getTitle());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, movieTitles);
            spinnerMovie.setAdapter(adapter);
            spinnerMovie.setOnItemClickListener((parent, view, position, id) -> {
                selectedMovieId = movieList.get(position).getId();
            });
        });

        db.collection("cinemas").get().addOnSuccessListener(queryDocumentSnapshots -> {
            cinemaList = queryDocumentSnapshots.toObjects(Cinema.class);
            List<String> cinemaNames = new ArrayList<>();
            for (int i = 0; i < cinemaList.size(); i++) {
                cinemaList.get(i).setId(queryDocumentSnapshots.getDocuments().get(i).getId());
                cinemaNames.add(cinemaList.get(i).getName());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cinemaNames);
            spinnerCinema.setAdapter(adapter);
            spinnerCinema.setOnItemClickListener((parent, view, position, id) -> {
                selectedCinemaId = cinemaList.get(position).getId();
                selectedCity = cinemaList.get(position).getCity();
            });
        });

        db.collection("rooms").get().addOnSuccessListener(queryDocumentSnapshots -> {
            roomList = queryDocumentSnapshots.toObjects(com.example.rapapp.models.Room.class);
            List<String> roomNames = new ArrayList<>();
            for (int i = 0; i < roomList.size(); i++) {
                roomList.get(i).setId(queryDocumentSnapshots.getDocuments().get(i).getId());
                roomNames.add(roomList.get(i).getName());
            }
            ArrayAdapter<String> adapterRoom = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, roomNames);
            spinnerRoom.setAdapter(adapterRoom);
            spinnerRoom.setOnItemClickListener((parent, view, position, id) -> {
                selectedRoomId = roomList.get(position).getId();
            });
        });
    }

    private void loadRoomsForCinema(String cinemaId) {
        // Method no longer used as we load all rooms now
    }

    private void showDatePicker() {
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            etDate.setText(sdfDate.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void showTimePicker() {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            etTime.setText(sdfTime.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void loadShowtimeData() {
        db.collection("showtimes").document(showtimeId).get().addOnSuccessListener(doc -> {
            Showtime s = doc.toObject(Showtime.class);
            if (s != null) {
                selectedMovieId = s.getMovieId();
                selectedCinemaId = s.getCinemaId();
                selectedCity = s.getCity();
                etDate.setText(s.getDate());
                etTime.setText(s.getTime());
                etFormat.setText(s.getFormat());
                selectedRoomId = s.getRoomId();
                
                // Set text for spinners (simplified)
                db.collection("movies").document(selectedMovieId).get().addOnSuccessListener(d -> spinnerMovie.setText(d.getString("title"), false));
                db.collection("cinemas").document(selectedCinemaId).get().addOnSuccessListener(d -> spinnerCinema.setText(d.getString("name"), false));
                if (selectedRoomId != null) {
                    db.collection("rooms").document(selectedRoomId).get().addOnSuccessListener(d -> spinnerRoom.setText(d.getString("name"), false));
                }
            }
        });
    }

    private void saveShowtime() {
        String date = etDate.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String format = etFormat.getText().toString().trim();

        if (selectedMovieId == null || selectedCinemaId == null || selectedRoomId == null || date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Fetch movie duration
        db.collection("movies").document(selectedMovieId).get().addOnSuccessListener(movieDoc -> {
            Long durationObj = movieDoc.getLong("duration");
            int durationMinutes = durationObj != null ? durationObj.intValue() : 120; // Default 120 mins if missing
            
            // Allow 15 minutes for cleaning/setup between shows
            final int totalBlockMinutes = durationMinutes + 15; 
            
            // 2. Fetch existing showtimes for the same room on the same date
            db.collection("showtimes")
                .whereEqualTo("roomId", selectedRoomId)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    boolean hasOverlap = false;
                    
                    try {
                        String[] newTimeParts = time.split(":");
                        int newStartMinutes = Integer.parseInt(newTimeParts[0]) * 60 + Integer.parseInt(newTimeParts[1]);
                        int newEndMinutes = newStartMinutes + totalBlockMinutes;

                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            // Skip the current showtime if we are editing
                            if (showtimeId != null && doc.getId().equals(showtimeId)) {
                                continue;
                            }
                            
                            Showtime existingShow = doc.toObject(Showtime.class);
                            if (existingShow != null && existingShow.getTime() != null) {
                                // Fetch duration of existing show's movie (synchronous simulation for logic flow, ideally should fetch each or use a default buffer)
                                // To keep it efficient, we assume a standard 120 min + 15 min buffer for existing shows if we don't query their exact duration here.
                                // A more robust way is to query the movie duration, but for this constraint check, a fixed buffer is often used or we query all movies.
                                // For simplicity and speed in this check, we will use a conservative 150 minutes block for existing shows.
                                int existingBlockMinutes = 150; 
                                
                                String[] existTimeParts = existingShow.getTime().split(":");
                                int existStartMinutes = Integer.parseInt(existTimeParts[0]) * 60 + Integer.parseInt(existTimeParts[1]);
                                int existEndMinutes = existStartMinutes + existingBlockMinutes;
                                
                                // Overlap logic: (StartA < EndB) and (EndA > StartB)
                                if (newStartMinutes < existEndMinutes && newEndMinutes > existStartMinutes) {
                                    hasOverlap = true;
                                    break;
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Lỗi kiểm tra thời gian", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (hasOverlap) {
                        Toast.makeText(this, "Lỗi: Trùng lịch chiếu tại phòng này!", Toast.LENGTH_LONG).show();
                    } else {
                        // 3. Proceed to save
                        Showtime s = new Showtime();
                        s.setMovieId(selectedMovieId);
                        s.setCinemaId(selectedCinemaId);
                        s.setCity(selectedCity);
                        s.setDate(date);
                        s.setTime(time);
                        s.setFormat(format);
                        s.setRoomId(selectedRoomId);
                        s.setBookedSeats(new ArrayList<>());

                        if (showtimeId != null) {
                            db.collection("showtimes").document(showtimeId).set(s)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        } else {
                            db.collection("showtimes").add(s)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                                        finish();
                                    });
                        }
                    }
                });
        }).addOnFailureListener(e -> Toast.makeText(this, "Không thể lấy thông tin phim", Toast.LENGTH_SHORT).show());
    }
}
