package com.example.rapapp.models;

import com.google.firebase.firestore.Exclude;
import java.util.List;

public class Room {
    public static final String SEAT_TYPE_SINGLE = "S";
    public static final String SEAT_TYPE_VIP = "V";
    public static final String SEAT_TYPE_COUPLE = "C";
    public static final String SEAT_TYPE_TRIPLE = "B";
    public static final String SEAT_TYPE_EMPTY = "_";

    @Exclude
    private String id;
    private String name;
    private String cinemaId;
    private int totalRows;
    private int totalCols;
    private List<String> layout; // Mỗi String là một hàng, VD: "SS__VVVV__SS"

    public Room() {}

    public Room(String name, String cinemaId, int totalRows, int totalCols, List<String> layout) {
        this.name = name;
        this.cinemaId = cinemaId;
        this.totalRows = totalRows;
        this.totalCols = totalCols;
        this.layout = layout;
    }

    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCinemaId() { return cinemaId; }
    public void setCinemaId(String cinemaId) { this.cinemaId = cinemaId; }

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }

    public int getTotalCols() { return totalCols; }
    public void setTotalCols(int totalCols) { this.totalCols = totalCols; }

    public List<String> getLayout() { return layout; }
    public void setLayout(List<String> layout) { this.layout = layout; }
}
