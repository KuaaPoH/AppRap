package com.example.rapapp.models;

import com.google.firebase.firestore.Exclude;

public class Showtime {
    @Exclude
    private String id;
    private String movieId;
    private String cinemaId;
    private String city;
    private String date;      // Dùng cho bộ chọn ngày (yyyy-MM-dd)
    private String time;      // Giờ chiếu (HH:mm)
    private String format;    // Gộp cả định dạng và phòng: "CINE DE KIDS 2D LỒNG TIẾNG"
    private String roomId;    // ID phòng chiếu
    private java.util.List<String> bookedSeats; // Danh sách ghế đã đặt: ["A1", "B5"]

    public Showtime() {}

    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }

    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }

    public String getCinemaId() { return cinemaId; }
    public void setCinemaId(String cinemaId) { this.cinemaId = cinemaId; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public java.util.List<String> getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(java.util.List<String> bookedSeats) { this.bookedSeats = bookedSeats; }
}
