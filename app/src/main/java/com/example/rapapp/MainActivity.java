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
    private TextView tvLocation, btnViewMore;
    private TextView tabNowShowing, tabComingSoon;
    private boolean isNowShowingSelected = true;
    private String selectedLocation = "Toàn quốc";
    private final String[] locations = {
        "Toàn quốc", "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
    };

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
        tvLocation = findViewById(R.id.tvLocation);
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

        tvLocation.setOnClickListener(v -> {
            // Hiệu ứng click: Thu nhỏ nhẹ rồi mở dialog
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                showLocationPickerDialog();
            }).start();
        });

        // Vô hiệu hóa Tooltip (thông báo khi nhấn giữ) trên Bottom Navigation
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            android.view.View menuView = bottomNav.findViewById(bottomNav.getMenu().getItem(i).getItemId());
            if (menuView != null) {
                menuView.setOnLongClickListener(v -> true);
            }
        }

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_cinema) {
                android.content.Intent intent = new android.content.Intent(MainActivity.this, CinemaListActivity.class);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION | android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });

        // Lấy dữ liệu từ Firebase
        loadMoviesFromFirebase();
    }

    private void showLocationPickerDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        android.view.View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        bottomSheetDialog.setContentView(view);

        android.widget.NumberPicker picker = view.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(locations.length - 1);
        picker.setDisplayedValues(locations);

        // Thiết lập giá trị hiện tại
        for (int i = 0; i < locations.length; i++) {
            if (locations[i].equals(selectedLocation)) {
                picker.setValue(i);
                break;
            }
        }

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedLocation = locations[picker.getValue()];
            tvLocation.setText(selectedLocation);
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();
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
        // Đảm bảo tab Trang chủ luôn được chọn khi quay lại màn hình này
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav.getSelectedItemId() != R.id.nav_home) {
            bottomNav.setSelectedItemId(R.id.nav_home);
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

