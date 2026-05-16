package com.example.rapapp.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.rapapp.R;
import com.example.rapapp.adapters.BannerAdapter;
import com.example.rapapp.adapters.ProductAdapter;
import com.example.rapapp.models.Product;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class StarShopFragment extends Fragment {

    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<Product> allProducts = new ArrayList<>();
    private List<Product> filteredProducts = new ArrayList<>();
    
    private TextView tabSeasonal, tabMovie, tvLocation;
    private boolean isSeasonalSelected = true;
    private String selectedLocation = "Toàn quốc";
    private List<String> locations = new ArrayList<>();

    private ViewPager2 viewPagerBanner;
    private TabLayout tabLayoutDots;
    private Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;

    public StarShopFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_star_shop, container, false);

        rvProducts = view.findViewById(R.id.rvProducts);
        tabSeasonal = view.findViewById(R.id.tabSeasonal);
        tabMovie = view.findViewById(R.id.tabMovie);
        tvLocation = view.findViewById(R.id.tvLocation);
        viewPagerBanner = view.findViewById(R.id.viewPagerBanner);
        tabLayoutDots = view.findViewById(R.id.tabLayoutDots);

        setupBanner();

        productAdapter = new ProductAdapter(getContext(), filteredProducts);
        rvProducts.setLayoutManager(new GridLayoutManager(getContext(), 2));
        rvProducts.setAdapter(productAdapter);
        rvProducts.setNestedScrollingEnabled(false);

        tabSeasonal.setOnClickListener(v -> {
            if (!isSeasonalSelected) {
                isSeasonalSelected = true;
                updateTabUI();
                filterProducts();
            }
        });

        tabMovie.setOnClickListener(v -> {
            if (isSeasonalSelected) {
                isSeasonalSelected = false;
                updateTabUI();
                filterProducts();
            }
        });
        updateTabUI();

        tvLocation.setOnClickListener(v -> showLocationPickerDialog());

        loadProductsFromFirebase();
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

    private void updateTabUI() {
        float activeScale = 1.05f;
        float inactiveScale = 1.0f;
        int activeColor = Color.parseColor("#034EA2");
        int inactiveColor = Color.parseColor("#888888");
        long duration = 200;

        tabSeasonal.setTypeface(null, Typeface.BOLD);
        tabMovie.setTypeface(null, Typeface.BOLD);

        if (isSeasonalSelected) {
            tabSeasonal.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabSeasonal.setTextColor(activeColor);

            tabMovie.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabMovie.setTextColor(inactiveColor);
        } else {
            tabSeasonal.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabSeasonal.setTextColor(inactiveColor);

            tabMovie.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabMovie.setTextColor(activeColor);
        }
    }

    private void filterProducts() {
        filteredProducts.clear();
        String category = isSeasonalSelected ? "Seasonal" : "Movie";
        for (Product product : allProducts) {
            if (category.equals(product.getCategory())) {
                filteredProducts.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
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

    private void loadProductsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("products").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allProducts.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Product product = document.toObject(Product.class);
                    product.setId(document.getId());
                    allProducts.add(product);
                }
                filterProducts();
            } else {
                Log.e("StarShopFragment", "Lỗi lấy dữ liệu sản phẩm", task.getException());
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
}