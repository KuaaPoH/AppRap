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
    private List<Movie> movies;
    private List<Movie> allMovies;
    private TextView btnViewMore;

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutDots;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        // Sửa lỗi 'main' ID bị thiếu: Dùng ID gốc của CoordinatorLayout là 'main_content'
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvMovies = findViewById(R.id.rvMovies);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        tabLayoutDots = findViewById(R.id.tabLayoutDots);
        btnViewMore = findViewById(R.id.btnViewMore);

        // Thiết lập Banner
        setupBanner();

        // Khởi tạo danh sách rỗng và Adapter trước
        movies = new ArrayList<>();
        allMovies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies);
        rvMovies.setLayoutManager(new GridLayoutManager(this, 2));
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setNestedScrollingEnabled(false);

        // Xử lý sự kiện nút Xem Thêm
        btnViewMore.setOnClickListener(v -> {
            movies.clear();
            movies.addAll(allMovies);
            movieAdapter.notifyDataSetChanged();
            btnViewMore.setVisibility(android.view.View.GONE);
        });

        // Lấy dữ liệu từ Firebase
        loadMoviesFromFirebase();
    }

    private void setupBanner() {
        List<String> bannerUrls = new ArrayList<>();
        BannerAdapter bannerAdapter = new BannerAdapter(bannerUrls);
        viewPagerBanner.setAdapter(bannerAdapter);

        // Hiệu ứng xem trước banner sau
        viewPagerBanner.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(10));
        viewPagerBanner.setPageTransformer(transformer);

        // Liên kết ViewPager2 với TabLayout để tạo dấu chấm
        new TabLayoutMediator(tabLayoutDots, viewPagerBanner, (tab, position) -> {
            // Không cần set text, chỉ cần hiện chấm tròn
        }).attach();

        // Tự động chuyển banner sau 5s
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

        // Lấy dữ liệu banner từ Firebase
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banners").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                bannerUrls.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String url = document.getString("imageUrl");
                    if (url != null) {
                        bannerUrls.add(url);
                    }
                }
                bannerAdapter.notifyDataSetChanged();
                
                // Bắt đầu chạy slide sau khi có dữ liệu
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 5000);
            } else {
                Log.e("MainActivity", "Lỗi lấy dữ liệu banner", task.getException());
                // Fallback nếu lỗi hoặc chưa có database
                bannerUrls.clear();
                bannerUrls.addAll(Arrays.asList(
                        "https://ocw.mobi/img/movie/doi-tham-tu-cuu.jpg",
                        "https://image.tmdb.org/t/p/w500/gKkl37BQuKT9S60m2sycM6biOqy.jpg",
                        "https://ocw.mobi/img/movie/lat-mat-7.jpg"
                ));
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
        
        db.collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allMovies.clear();
                movies.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Movie movie = new Movie();
                    
                    movie.setTitle(document.getString("title"));
                    movie.setDescription(document.getString("description"));
                    movie.setPoster(document.getString("posterUrl"));
                    Log.d("FirebaseData", "Link anh: " + movie.getPoster());
                    movie.setAgeRating(document.getString("ageRating"));
                    
                    Long duration = document.getLong("duration");
                    if (duration != null) {
                        movie.setDuration(duration.intValue());
                    }
                    
                    Double rating = document.getDouble("rating");
                    if (rating != null) {
                        movie.setRating(rating);
                    }
                    
                    allMovies.add(movie);
                }

                if (allMovies.size() > 6) {
                    movies.addAll(allMovies.subList(0, 6));
                    btnViewMore.setVisibility(android.view.View.VISIBLE);
                } else {
                    movies.addAll(allMovies);
                    btnViewMore.setVisibility(android.view.View.GONE);
                }
                
                movieAdapter.notifyDataSetChanged();
                
            } else {
                Log.e("MainActivity", "Lỗi lấy dữ liệu phim", task.getException());
                Toast.makeText(MainActivity.this, "Không thể tải danh sách phim", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
