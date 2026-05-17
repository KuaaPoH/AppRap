package com.example.rapapp.models;

import com.google.firebase.firestore.Exclude;

public class Cinema {
    @Exclude
    private String id;
    private String name;
    private String address;
    private String imageUrl;
    private String phone;
    private String city;

    public Cinema() {} // Required for Firebase

    @Exclude
    public String getId() { return id; }
    @Exclude
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}