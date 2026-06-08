package com.example.rapapp.admin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.admin.adapters.AdminBannerAdapter;
import com.example.rapapp.models.Banner;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminBannerListActivity extends AppCompatActivity {

    private RecyclerView rvAdminBanners;
    private AdminBannerAdapter adapter;
    private List<Banner> bannerList;
    private FirebaseFirestore db;

    private List<Banner> fullBannerList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_banner_list);

        db = FirebaseFirestore.getInstance();
        bannerList = new ArrayList<>();

        initViews();
        setupSearch();
        loadBanners();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearchBanner);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBanners(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterBanners(String query) {
        List<Banner> filteredList = new ArrayList<>();
        for (Banner banner : fullBannerList) {
            if (banner.getImageUrl() != null && banner.getImageUrl().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(banner);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvAdminBanners = findViewById(R.id.rvAdminBanners);
        rvAdminBanners.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminBannerAdapter(bannerList, banner -> {
            Intent intent = new Intent(this, AdminBannerFormActivity.class);
            intent.putExtra("bannerId", banner.getId());
            startActivity(intent);
        }, banner -> {
            deleteBanner(banner);
        });
        rvAdminBanners.setAdapter(adapter);

        ImageView btnAddBanner = findViewById(R.id.btnAddBanner);
        btnAddBanner.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminBannerFormActivity.class);
            startActivity(intent);
        });
    }

    private void loadBanners() {
        db.collection("banners")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        bannerList.clear();
                        fullBannerList.clear();
                        List<Banner> objects = value.toObjects(Banner.class);
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            objects.get(i).setId(value.getDocuments().get(i).getId());
                        }
                        bannerList.addAll(objects);
                        fullBannerList.addAll(objects);
                        
                        EditText etSearch = findViewById(R.id.etSearchBanner);
                        filterBanners(etSearch.getText().toString());
                    }
                });
    }

    private void deleteBanner(Banner banner) {
        db.collection("banners").document(banner.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá banner", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show());
    }
}