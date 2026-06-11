package com.example.rapapp.models;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String birthday;
    private String role; // "user" or "admin"
    private int stars;
    private String avatarUrl;

    public User() {
        // Required for Firestore
    }

    public User(String uid, String name, String email, String phone, String birthday, String role, int stars) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.birthday = birthday;
        this.role = role;
        this.stars = stars;
        this.avatarUrl = "";
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getStars() {
        return stars;
    }

    public void setStars(int stars) {
        this.stars = stars;
    }
}
