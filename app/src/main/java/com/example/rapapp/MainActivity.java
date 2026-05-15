package com.example.rapapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.rapapp.fragments.CinemaFragment;
import com.example.rapapp.fragments.HomeFragment;
import com.example.rapapp.fragments.NewsFragment;
import com.example.rapapp.fragments.ProfileFragment;
import com.example.rapapp.fragments.StarShopFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

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

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        
        // Vô hiệu hóa Tooltip (thông báo khi nhấn giữ) trên Bottom Navigation
        for (int i = 0; i < bottomNav.getMenu().size(); i++) {
            View menuView = bottomNav.findViewById(bottomNav.getMenu().getItem(i).getItemId());
            if (menuView != null) {
                menuView.setOnLongClickListener(v -> true);
            }
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_cinema) {
                selectedFragment = new CinemaFragment();
            } else if (id == R.id.nav_ticket) {
                selectedFragment = new StarShopFragment();
            } else if (id == R.id.nav_news) {
                selectedFragment = new NewsFragment();
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Hiển thị Fragment Trang chủ mặc định khi mở app
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}
