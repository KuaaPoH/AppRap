package com.example.rapapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rapapp.MovieListActivity;
import com.example.rapapp.R;
import com.example.rapapp.adapters.BannerAdapter;
import com.example.rapapp.adapters.MovieAdapter;
import com.example.rapapp.models.Movie;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvMovies;
    private MovieAdapter movieAdapter;
    private List<Movie> movies; // Danh sách hiển thị
    private List<Movie> allMovies; // Toàn bộ phim lấy từ Firebase
    private List<Movie> filteredMovies; // Phim sau khi lọc theo tab
    private TextView tvLocation, btnViewMore;
    private TextView tabNowShowing, tabComingSoon;
    private boolean isNowShowingSelected = true;
    private String selectedLocation = "Toàn quốc";
    private List<String> locations = new ArrayList<>();

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutDots;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvMovies = view.findViewById(R.id.rvMovies);
        tvLocation = view.findViewById(R.id.tvLocation);
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        tabLayoutDots = view.findViewById(R.id.tabLayoutDots);
        btnViewMore = view.findViewById(R.id.btnViewMore);
        tabNowShowing = view.findViewById(R.id.tabNowShowing);
        tabComingSoon = view.findViewById(R.id.tabComingSoon);

        setupBanner();

        movies = new ArrayList<>();
        allMovies = new ArrayList<>();
        filteredMovies = new ArrayList<>();
        
        movieAdapter = new MovieAdapter(getContext(), movies);
        rvMovies.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setNestedScrollingEnabled(false);

        btnViewMore.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MovieListActivity.class);
            startActivity(intent);
        });

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
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                showLocationPickerDialog();
            }).start();
        });

        loadMoviesFromFirebase();
        loadLocationsFromFirebase();

        return view;
    }

    private void loadLocationsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("metadata").document("locations").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> locList = (List<String>) documentSnapshot.get("list");
                if (locList != null) {
                    locations.clear();
                    locations.addAll(locList);
                }
            }
        });
    }

    private void showLocationPickerDialog() {
        if (getActivity() == null) return;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        bottomSheetDialog.setContentView(view);

        NumberPicker picker = view.findViewById(R.id.locationPicker);
        if (locations.isEmpty()) {
            locations.add("Toàn quốc");
        }
        picker.setMinValue(0);
        picker.setMaxValue(locations.size() - 1);
        picker.setDisplayedValues(locations.toArray(new String[0]));

        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).equals(selectedLocation)) {
                picker.setValue(i);
                break;
            }
        }

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedLocation = locations.get(picker.getValue());
            tvLocation.setText(selectedLocation);
            bottomSheetDialog.dismiss();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void updateTabUI() {
        float activeScale = 1.05f;
        float inactiveScale = 1.0f;
        int activeColor = Color.parseColor("#034EA2");
        int inactiveColor = Color.parseColor("#888888");
        long duration = 200;

        tabNowShowing.setTypeface(null, Typeface.BOLD);
        tabComingSoon.setTypeface(null, Typeface.BOLD);

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
        Timestamp now = Timestamp.now();

        for (Movie movie : allMovies) {
            if (movie.getReleaseDate() == null) continue;
            
            if (isNowShowingSelected) {
                if (movie.getReleaseDate().compareTo(now) <= 0) {
                    filteredMovies.add(movie);
                }
            } else {
                if (movie.getReleaseDate().compareTo(now) > 0) {
                    filteredMovies.add(movie);
                }
            }
        }

        movies.clear();
        if (filteredMovies.size() > 6) {
            movies.addAll(filteredMovies.subList(0, 6));
            btnViewMore.setVisibility(View.VISIBLE);
        } else {
            movies.addAll(filteredMovies);
            btnViewMore.setVisibility(View.GONE);
        }
        movieAdapter.notifyDataSetChanged();
    }

    private void setupBanner() {
        List<com.example.rapapp.models.Banner> bannerList = new ArrayList<>();
        BannerAdapter bannerAdapter = new BannerAdapter(bannerList, banner -> {
            Intent intent = new Intent(getActivity(), com.example.rapapp.PromoDetailActivity.class);
            intent.putExtra("bannerId", banner.getId());
            intent.putExtra("imageUrl", banner.getImageUrl());
            startActivity(intent);
        });
        viewPagerBanner.setAdapter(bannerAdapter);

        viewPagerBanner.setOffscreenPageLimit(3);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(10));
        viewPagerBanner.setPageTransformer(transformer);

        new TabLayoutMediator(tabLayoutDots, viewPagerBanner, (tab, position) -> {}).attach();

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!bannerList.isEmpty()) {
                    int currentItem = viewPagerBanner.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerList.size();
                    viewPagerBanner.setCurrentItem(nextItem, true);
                    bannerHandler.postDelayed(this, 5000);
                }
            }
        };

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("banners").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                bannerList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    com.example.rapapp.models.Banner banner = document.toObject(com.example.rapapp.models.Banner.class);
                    banner.setId(document.getId());
                    bannerList.add(banner);
                }
                bannerAdapter.notifyDataSetChanged();
                bannerHandler.removeCallbacks(bannerRunnable);
                bannerHandler.postDelayed(bannerRunnable, 5000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        bannerHandler.removeCallbacks(bannerRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerRunnable != null) {
            bannerHandler.postDelayed(bannerRunnable, 5000);
        }
    }

    private void loadMoviesFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        db.collection("movies")
          .orderBy("releaseDate", Query.Direction.DESCENDING)
          .get()
          .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allMovies.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Movie movie = document.toObject(Movie.class);
                    movie.setId(document.getId());
                    allMovies.add(movie);
                }
                filterMovies();
            } else {
                Log.e("HomeFragment", "Lỗi lấy dữ liệu phim", task.getException());
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Không thể tải danh sách phim", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}