package com.example.rapapp.admin.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.rapapp.R;
import com.example.rapapp.models.Cinema;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminCinemaFormActivity extends AppCompatActivity {

    private TextInputEditText etName, etAddress, etPhone, etImageUrl;
    private android.widget.AutoCompleteTextView spinnerCity;
    private TextView tvFormTitle, btnSave;
    private FirebaseFirestore db;
    private String cinemaId;

    // Danh sách 63 tỉnh thành Việt Nam cơ bản
    private final String[] CITIES = {
            "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định",
            "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk",
            "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh",
            "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum",
            "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận",
            "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng",
            "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh",
            "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_cinema_form);

        db = FirebaseFirestore.getInstance();

        initViews();
        cinemaId = getIntent().getStringExtra("cinemaId");
        if (cinemaId != null) {
            tvFormTitle.setText("Chỉnh sửa rạp");
            loadCinemaData();
        }

        btnSave.setOnClickListener(v -> saveCinema());
    }

    private void initViews() {
        tvFormTitle = findViewById(R.id.tvFormTitle);
        btnSave = findViewById(R.id.btnSave);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        etName = findViewById(R.id.etName);
        etAddress = findViewById(R.id.etAddress);
        etPhone = findViewById(R.id.etPhone);
        etImageUrl = findViewById(R.id.etImageUrl);
        spinnerCity = findViewById(R.id.spinnerCity);

        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, CITIES);
        spinnerCity.setAdapter(adapter);
    }

    private void loadCinemaData() {
        db.collection("cinemas").document(cinemaId).get().addOnSuccessListener(documentSnapshot -> {
            Cinema cinema = documentSnapshot.toObject(Cinema.class);
            if (cinema != null) {
                etName.setText(cinema.getName());
                etAddress.setText(cinema.getAddress());
                etPhone.setText(cinema.getPhone());
                etImageUrl.setText(cinema.getImageUrl());
                spinnerCity.setText(cinema.getCity(), false);
            }
        });
    }

    private void saveCinema() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();
        String city = spinnerCity.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || city.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Cinema cinema = new Cinema();
        cinema.setName(name);
        cinema.setAddress(address);
        cinema.setPhone(phone);
        cinema.setImageUrl(imageUrl);
        cinema.setCity(city);

        if (cinemaId != null) {
            db.collection("cinemas").document(cinemaId).set(cinema)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Cập nhật rạp thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            db.collection("cinemas").add(cinema)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(this, "Thêm rạp thành công", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
