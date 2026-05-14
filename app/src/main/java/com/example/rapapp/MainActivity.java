package com.example.rapapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rapapp.adapters.BannerAdapter;
import com.example.rapapp.adapters.MovieAdapter;
import com.example.rapapp.models.Movie;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> movies; // Danh sách hiển thị
    private List<Movie> allMovies; // Toàn bộ phim lấy từ Firebase
    private List<Movie> filteredMovies; // Phim sau khi lọc theo tab
    private TextView btnViewMore;
    private TextView tabNowShowing, tabComingSoon;
    private boolean isNowShowingSelected = true;

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutDots;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMovies = findViewById(R.id.rvMovies);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabLayoutDots = findViewById(R.id.tabLayoutDots);
        btnViewMore = findViewById(R.id.btnViewMore);
        tabNowShowing = findViewById(R.id.tabNowShowing);
        tabComingSoon = findViewById(R.id.tabComingSoon);

        // Thiết lập Banner
        setupBanner();

        // Khởi tạo danh sách
        movies = new ArrayList<>();
        allMovies = new ArrayList<>();
        filteredMovies = new ArrayList<>();
        
        movieAdapter = new MovieAdapter(this, movies);
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setNestedScrollingEnabled(false);

        // Xử lý sự kiện nút Xem Thêm
        btnViewMore.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(MainActivity.this, MovieListActivity.class);
            startActivity(intent);
        });

        // Xử lý sự kiện Tab
        tabNowShowing.setOnClickListener(v -> {
            if (!isNowShowingSelected) {
                isNowShowingSelected = true;
                updateTabUI();
                filterMovies();
            }
        });

        tabComingSoon.setOnClickListener(v -> {
            if (isNowShowingSelected) {
                isNowShowingSelected = false;
                updateTabUI();
                filterMovies();
            }
        });

        // Lấy dữ liệu từ Firebase
        loadMoviesFromFirebase();
    }

    private void updateTabUI() {
        float activeScale = 1.05f; // Chỉ to hơn một chút (~1px tùy màn hình)
        float inactiveScale = 1.0f;
        int activeColor = android.graphics.Color.parseColor("#034EA2");
        int inactiveColor = android.graphics.Color.parseColor("#888888");
        long duration = 200;

        // Cả hai luôn để Bold theo yêu cầu mới
        tabNowShowing.setTypeface(null, android.graphics.Typeface.BOLD);
        tabComingSoon.setTypeface(null, android.graphics.Typeface.BOLD);

        if (isNowShowingSelected) {
            tabNowShowing.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabNowShowing.setTextColor(activeColor);

            tabComingSoon.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabComingSoon.setTextColor(inactiveColor);
        } else {
            tabNowShowing.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabNowShowing.setTextColor(inactiveColor);

            tabComingSoon.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabComingSoon.setTextColor(activeColor);
        }
    }

    private void filterMovies() {
        filteredMovies.clear();
        com.google.firebase.Timestamp now = com.google.firebase.Timestamp.now();

        for (Movie movie : allMovies) {
            if (movie.getReleaseDate() == null) continue;
            
            if (isNowShowingSelected) {
                // Đang chiếu: releaseDate <= now
                if (movie.getReleaseDate().compareTo(now) <= 0) {
                    filteredMovies.add(movie);
                }
            } else {
                // Sắp chiếu: releaseDate > now
                if (movie.getReleaseDate().compareTo(now) > 0) {
                    filteredMovies.add(movie);
                }
            }
        }

        // Hiển thị tối đa 6 phim ban đầu
        movies.clear();
        if (filteredMovies.size() > 6) {
            movies.addAll(filteredMovies.subList(0, 6));
            btnViewMore.setVisibility(android.view.View.VISIBLE);
        } else {
            movies.addAll(filteredMovies);
            btnViewMore.setVisibility(android.view.View.GONE);
        }
        movieAdapter.notifyDataSetChanged();
    }

    private void setupBanner() {
        List<String> bannerUrls = new ArrayList<>();
        BannerAdapter bannerAdapter = new BannerAdapter(bannerUrls);
        viewPagerBanner.setAdapter(bannerAdapter);

        viewPagerBanner.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(10));
        viewPagerBanner.setPageTransformer(transformer);

        new TabLayoutMediator(tabLayoutDots, viewPagerBanner, (tab, position) -> {}).attach();

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!bannerUrls.isEmpty()) {
                    int currentItem = viewPagerBanner.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerUrls.size();
                    viewPagerBanner.setCurrentItem(nextItem, true);
                    bannerHandler.postDelayed(this, 5000);
                }
            }
        };

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banners").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                bannerUrls.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String url = document.getString("imageUrl");
                    if (url != null) bannerUrls.add(url);
                }
                bannerAdapter.notifyDataSetChanged();
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 5000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bannerRunnable != null) {
            bannerHandler.postDelayed(bannerRunnable, 5000);
        }
    }

    private void loadMoviesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Sắp xếp theo ngày phát hành mới nhất lên đầu
        db.collection("movies")
          .orderBy("releaseDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
          .get()
          .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allMovies.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    allMovies.add(movie);
                }
                // Sau khi lấy toàn bộ phim, thực hiện lọc theo tab mặc định (Đang chiếu)
                filterMovies();
            } else {
                Log.e("MainActivity", "Lỗi lấy dữ liệu phim", task.getException());
                Toast.makeText(MainActivity.this, "Không thể tải danh sách phim", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

