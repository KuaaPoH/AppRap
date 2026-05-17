package com.example.rapapp.models;

public class DateModel {
    private String dayOfWeek;
    private String dateMonth;
    private String fullDate; // yyyy-MM-dd
    private String displayFullDate; // Thứ Bảy 16, tháng 5 2026
    private boolean isSelected;

    public DateModel(String dayOfWeek, String dateMonth, String fullDate, String displayFullDate, boolean isSelected) {
        this.dayOfWeek = dayOfWeek;
        this.dateMonth = dateMonth;
        this.fullDate = fullDate;
        this.displayFullDate = displayFullDate;
        this.isSelected = isSelected;
    }

    public String getDayOfWeek() { return dayOfWeek; }
    public String getDateMonth() { return dateMonth; }
    public String getFullDate() { return fullDate; }
    public String getDisplayFullDate() { return displayFullDate; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
