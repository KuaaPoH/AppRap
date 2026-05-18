package com.example.rapapp;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.example.rapapp.fragments.MovieInfoFragment;
import com.example.rapapp.fragments.MovieNewsFragment;
import com.example.rapapp.fragments.ShowtimeFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MovieDetailActivity extends AppCompatActivity {

    private String movieId;
    private String movieTitle;
    private String selectedLocation;
    private TextView tvToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getStringExtra("movieId");
        movieTitle = getIntent().getStringExtra("movieTitle");
        selectedLocation = getIntent().getStringExtra("selectedLocation");
        if (selectedLocation == null) selectedLocation = "Toàn quốc";

        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(movieTitle);

        setupToolbar();
        setupViewPager();
    }

    private void setupToolbar() {
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
        findViewById(R.id.btnShare).setOnClickListener(v -> {
            // Share logic
        });
    }

    private void setupViewPager() {
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        viewPager.setUserInputEnabled(false);
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return ShowtimeFragment.newInstance(movieId, selectedLocation);
                    case 1: return MovieInfoFragment.newInstance(movieId);
                    case 2: return MovieNewsFragment.newInstance(movieId);
                    default: return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3;
            }
        });

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Suất Chiếu"); break;
                case 1: tab.setText("Thông Tin"); break;
                case 2: tab.setText("Tin Tức"); break;
            }
        }).attach();

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null && tab.view != null) {
                for (int j = 0; j < tab.view.getChildCount(); j++) {
                    View child = tab.view.getChildAt(j);
                    if (child instanceof android.widget.TextView) {
                        ((android.widget.TextView) child).setAllCaps(false);
                    }
                }
            }
        }

        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            LinearLayout linearLayout = (LinearLayout) root;
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.tab_divider));
            linearLayout.setDividerPadding(32);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
                View tabView = tab.view;
                if (tabView != null) {
                    tabView.animate().scaleX(1.05f).scaleY(1.05f).setDuration(200).start();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View tabView = tab.view;
                if (tabView != null) {
                    tabView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        tabLayout.post(() -> {
            TabLayout.Tab firstTab = tabLayout.getTabAt(0);
            if (firstTab != null && firstTab.view != null) {
                firstTab.view.setScaleX(1.05f);
                firstTab.view.setScaleY(1.05f);
            }
        });
    }
}
