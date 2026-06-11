package com.example.rapapp.models;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Booking implements Serializable {
    private String id;
    private String userId;
    private String type; // "movie_ticket" or "star_shop"
    private double totalPrice;
    private String mainTitle; // Added for easy display
    private String mainImage; // Added for easy display
    
    @com.google.firebase.firestore.ServerTimestamp
    private java.util.Date timestamp;
    
    // Movie fields
    private String movieId;
    private String showtimeId;
    private String cinemaId;
    private String cinemaName;
    private List<String> seats;
    private List<String> combos;
    private List<String> comboPrices;
    private double seatTotalPrice;
    
    // Shop fields
    private List<Map<String, Object>> items;

    public Booking() {
    }

    public List<String> getCombos() {
        return combos;
    }

    public void setCombos(List<String> combos) {
        this.combos = combos;
    }

    public List<String> getComboPrices() {
        return comboPrices;
    }

    public void setComboPrices(List<String> comboPrices) {
        this.comboPrices = comboPrices;
    }

    public double getSeatTotalPrice() {
        return seatTotalPrice;
    }

    public void setSeatTotalPrice(double seatTotalPrice) {
        this.seatTotalPrice = seatTotalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public void setMainTitle(String mainTitle) {
        this.mainTitle = mainTitle;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public java.util.Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(java.util.Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getShowtimeId() {
        return showtimeId;
    }

    public void setShowtimeId(String showtimeId) {
        this.showtimeId = showtimeId;
    }

    public String getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(String cinemaId) {
        this.cinemaId = cinemaId;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public List<String> getSeats() {
        return seats;
    }

    public void setSeats(List<String> seats) {
        this.seats = seats;
    }

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }
}
