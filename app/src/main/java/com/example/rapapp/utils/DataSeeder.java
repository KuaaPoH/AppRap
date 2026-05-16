package com.example.rapapp.utils;

import android.util.Log;
import com.example.rapapp.models.Cinema;
import com.example.rapapp.models.Product;
import com.example.rapapp.models.News;
import com.example.rapapp.models.Movie;
import com.example.rapapp.models.Showtime;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DataSeeder {

    public static void seedMovies() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Movie m = new Movie();
        m.setTitle("Phim Test");
        m.setDescription("Nội dung phim test");
        m.setPosterUrl("https://www.galaxycine.vn/media/2024/5/15/doraemon-500_1715744040641.jpg");
        m.setDuration(120);
        m.setRating(9.0);
        m.setAgeRating("P");
        m.setReleaseDate(new Timestamp(new Date()));
        m.setDirector("Đạo diễn Test");
        m.setCast("Diễn viên Test");
        m.setPrice(90000);
        m.setTrailerUrl("https://www.youtube.com/watch?v=test");

        db.collection("movies").document("test").set(m);
    }

    public static void seedCinemas() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        Cinema c = new Cinema();
        c.setName("Rạp Test");
        c.setAddress("Địa chỉ Test");
        c.setPhone("1900 0000");
        c.setCity("TP Hồ Chí Minh");
        c.setImageUrl("https://www.galaxycine.vn/media/2023/10/26/nguyen-du-1_1698310323381.jpg");

        db.collection("cinemas").document("test").set(c);
    }

    public static void seedShowtimes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Tạo vài suất chiếu mẫu cho phim 'test' tại rạp 'test'
        db.collection("showtimes").add(createShowtime("test", "test", "TP Hồ Chí Minh", "2026-05-16", "14:00", "CINE DE KIDS 2D LỒNG TIẾNG"));
        db.collection("showtimes").add(createShowtime("test", "test", "TP Hồ Chí Minh", "2026-05-16", "16:00", "CINE DE KIDS 2D LỒNG TIẾNG"));
        db.collection("showtimes").add(createShowtime("test", "test", "TP Hồ Chí Minh", "2026-05-16", "18:00", "2D LỒNG TIẾNG"));
        db.collection("showtimes").add(createShowtime("test", "test", "TP Hồ Chí Minh", "2026-05-16", "20:00", "2D LỒNG TIẾNG"));
    }

    private static Showtime createShowtime(String movieId, String cinemaId, String city, String date, String time, String format) {
        Showtime s = new Showtime();
        s.setMovieId(movieId);
        s.setCinemaId(cinemaId);
        s.setCity(city);
        s.setDate(date);
        s.setTime(time);
        s.setFormat(format);
        return s;
    }

    public static void seedNews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<News> newsList = new ArrayList<>();
        newsList.add(new News("[Review] Phim Test", "https://i.ibb.co/Vp8nZ5T/news1.jpg", "Review", "Nội dung test", new Timestamp(new Date())));
        for (News n : newsList) {
            db.collection("news").add(n);
        }
    }

    public static void seedProducts() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").add(new Product("Sản phẩm Test", 50000, "https://i.ibb.co/L6v3n4K/capybara.jpg", "Movie"));
    }
}
