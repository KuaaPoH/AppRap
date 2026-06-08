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
import com.example.rapapp.admin.adapters.AdminNewsAdapter;
import com.example.rapapp.models.News;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class AdminNewsListActivity extends AppCompatActivity {

    private RecyclerView rvAdminNews;
    private AdminNewsAdapter adapter;
    private List<News> newsList;
    private FirebaseFirestore db;

    private List<News> fullNewsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_news_list);

        db = FirebaseFirestore.getInstance();
        newsList = new ArrayList<>();

        initViews();
        setupSearch();
        loadNews();
    }

    private void setupSearch() {
        EditText etSearch = findViewById(R.id.etSearchNews);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNews(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterNews(String query) {
        List<News> filteredList = new ArrayList<>();
        for (News news : fullNewsList) {
            if (news.getTitle() != null && news.getTitle().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(news);
            }
        }
        adapter.updateList(filteredList);
    }

    private void initViews() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        rvAdminNews = findViewById(R.id.rvAdminNews);
        rvAdminNews.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AdminNewsAdapter(newsList, news -> {
            Intent intent = new Intent(this, AdminNewsFormActivity.class);
            intent.putExtra("newsId", news.getId());
            startActivity(intent);
        }, news -> {
            deleteNews(news);
        });
        rvAdminNews.setAdapter(adapter);

        ImageView btnAddNews = findViewById(R.id.btnAddNews);
        btnAddNews.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminNewsFormActivity.class);
            startActivity(intent);
        });
    }

    private void loadNews() {
        db.collection("news")
                .orderBy("publishedDate", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Toast.makeText(this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (value != null) {
                        newsList.clear();
                        fullNewsList.clear();
                        List<News> objects = value.toObjects(News.class);
                        for (int i = 0; i < value.getDocuments().size(); i++) {
                            objects.get(i).setId(value.getDocuments().get(i).getId());
                        }
                        newsList.addAll(objects);
                        fullNewsList.addAll(objects);
                        
                        EditText etSearch = findViewById(R.id.etSearchNews);
                        filterNews(etSearch.getText().toString());
                    }
                });
    }

    private void deleteNews(News news) {
        db.collection("news").document(news.getId())
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "Đã xoá tin tức", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi xoá", Toast.LENGTH_SHORT).show());
    }
}