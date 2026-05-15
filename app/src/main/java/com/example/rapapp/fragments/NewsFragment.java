package com.example.rapapp.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.adapters.NewsAdapter;
import com.example.rapapp.models.News;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NewsFragment extends Fragment {

    private RecyclerView rvNews;
    private NewsAdapter newsAdapter;
    private List<News> allNews = new ArrayList<>();
    private List<News> filteredNews = new ArrayList<>();
    
    private TextView tabReview, tabNews, tabCharacter;
    private EditText edtSearch;
    private TextView btnCancelSearch;
    private View btnScrollTop;
    private String selectedCategory = "Review"; // Mặc định là Bình Luận

    public NewsFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        rvNews = view.findViewById(R.id.rvNews);
        tabReview = view.findViewById(R.id.tabReview);
        tabNews = view.findViewById(R.id.tabNews);
        tabCharacter = view.findViewById(R.id.tabCharacter);
        edtSearch = view.findViewById(R.id.edtSearch);
        btnCancelSearch = view.findViewById(R.id.btnCancelSearch);
        btnScrollTop = view.findViewById(R.id.btnScrollTop);

        setupRecyclerView();
        setupTabs();
        setupSearch();

        // Xử lý nút Cuộn lên đầu
        btnScrollTop.setOnClickListener(v -> rvNews.smoothScrollToPosition(0));
        
        rvNews.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    // Đang cuộn xuống -> Hiện nút
                    if (btnScrollTop.getVisibility() == View.GONE) {
                        btnScrollTop.setVisibility(View.VISIBLE);
                        btnScrollTop.setAlpha(0f);
                        btnScrollTop.animate().alpha(1f).setDuration(300).start();
                    }
                } else if (dy < 0) {
                    // Đang cuộn lên -> Ẩn nút
                    if (btnScrollTop.getVisibility() == View.VISIBLE) {
                        btnScrollTop.animate().alpha(0f).setDuration(300).withEndAction(() -> btnScrollTop.setVisibility(View.GONE)).start();
                    }
                }
                
                // Ẩn luôn nếu đang ở vị trí đầu tiên
                if (!recyclerView.canScrollVertically(-1)) {
                    btnScrollTop.setVisibility(View.GONE);
                }
            }
        });

        loadNewsFromFirebase();

        return view;
    }

    private void setupRecyclerView() {
        newsAdapter = new NewsAdapter(getContext(), filteredNews);
        rvNews.setLayoutManager(new LinearLayoutManager(getContext()));
        rvNews.setAdapter(newsAdapter);
    }

    private void setupTabs() {
        View.OnClickListener tabClickListener = v -> {
            int id = v.getId();
            if (id == R.id.tabReview) selectedCategory = "Review";
            else if (id == R.id.tabNews) selectedCategory = "News";
            else if (id == R.id.tabCharacter) selectedCategory = "Character";
            
            updateTabUI();
            filterNews();
        };

        tabReview.setOnClickListener(tabClickListener);
        tabNews.setOnClickListener(tabClickListener);
        tabCharacter.setOnClickListener(tabClickListener);
        
        updateTabUI();
    }

    private void updateTabUI() {
        float activeScale = 1.05f;
        float inactiveScale = 1.0f;
        int activeColor = Color.parseColor("#034EA2");
        int inactiveColor = Color.parseColor("#888888");
        long duration = 200;

        tabReview.setTypeface(null, android.graphics.Typeface.BOLD);
        tabNews.setTypeface(null, android.graphics.Typeface.BOLD);
        tabCharacter.setTypeface(null, android.graphics.Typeface.BOLD);

        if (selectedCategory.equals("Review")) {
            tabReview.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabReview.setTextColor(activeColor);
        } else {
            tabReview.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabReview.setTextColor(inactiveColor);
        }

        if (selectedCategory.equals("News")) {
            tabNews.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabNews.setTextColor(activeColor);
        } else {
            tabNews.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabNews.setTextColor(inactiveColor);
        }

        if (selectedCategory.equals("Character")) {
            tabCharacter.animate().scaleX(activeScale).scaleY(activeScale).setDuration(duration).start();
            tabCharacter.setTextColor(activeColor);
        } else {
            tabCharacter.animate().scaleX(inactiveScale).scaleY(inactiveScale).setDuration(duration).start();
            tabCharacter.setTextColor(inactiveColor);
        }
    }

    private void setupSearch() {
        edtSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                btnCancelSearch.setVisibility(View.VISIBLE);
            }
        });

        btnCancelSearch.setOnClickListener(v -> {
            edtSearch.setText("");
            edtSearch.clearFocus();
            btnCancelSearch.setVisibility(View.GONE);
            // Ẩn bàn phím
            if (getActivity() != null) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) imm.hideSoftInputFromWindow(edtSearch.getWindowToken(), 0);
            }
        });

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterNews();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterNews() {
        filteredNews.clear();
        String searchText = removeAccents(edtSearch.getText().toString().toLowerCase());

        for (News news : allNews) {
            boolean matchesCategory = news.getCategory().equals(selectedCategory);
            String title = news.getTitle() != null ? news.getTitle() : "";
            boolean matchesSearch = removeAccents(title.toLowerCase()).contains(searchText);

            if (matchesCategory && matchesSearch) {
                filteredNews.add(news);
            }
        }
        newsAdapter.notifyDataSetChanged();
    }

    // Hàm chuyển đổi chuỗi tiếng Việt có dấu thành không dấu để tìm kiếm
    private String removeAccents(String s) {
        if (s == null) return "";
        String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }

    private void loadNewsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("news")
          .orderBy("publishedDate", Query.Direction.DESCENDING)
          .get()
          .addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allNews.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    News news = document.toObject(News.class);
                    news.setId(document.getId());
                    allNews.add(news);
                }
                filterNews();
            } else {
                Log.e("NewsFragment", "Error loading news", task.getException());
            }
        });
    }
}