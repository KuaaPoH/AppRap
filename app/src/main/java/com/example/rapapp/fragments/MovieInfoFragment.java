package com.example.rapapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.rapapp.R;
import com.example.rapapp.models.Movie;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MovieInfoFragment extends Fragment {
    private String movieId;
    private Movie currentMovie;
    
    private ImageView imgTrailerThumb, imgPosterHeader;
    private TextView tvMovieTitleHeader, tvRatingHeader, tvDurationHeader, tvReleaseDateHeader, tvDescription, btnSeeMore;
    private RecyclerView rvCast, rvDirector, rvGallery;
    private boolean isExpanded = false;

    public static MovieInfoFragment newInstance(String movieId) {
        MovieInfoFragment fragment = new MovieInfoFragment();
        Bundle args = new Bundle();
        args.putString("movieId", movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            movieId = getArguments().getString("movieId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie_info, container, false);
        
        initViews(view);
        setupSeeMore();
        loadMovieInfo();
        
        return view;
    }

    private void initViews(View view) {
        imgTrailerThumb = view.findViewById(R.id.imgTrailerThumb);
        imgPosterHeader = view.findViewById(R.id.imgPosterHeader);
        tvMovieTitleHeader = view.findViewById(R.id.tvMovieTitleHeader);
        tvRatingHeader = view.findViewById(R.id.tvRatingHeader);
        tvDurationHeader = view.findViewById(R.id.tvDurationHeader);
        tvReleaseDateHeader = view.findViewById(R.id.tvReleaseDateHeader);
        tvDescription = view.findViewById(R.id.tvDescription);
        btnSeeMore = view.findViewById(R.id.btnSeeMore);
        rvCast = view.findViewById(R.id.rvCast);
        rvDirector = view.findViewById(R.id.rvDirector);
        rvGallery = view.findViewById(R.id.rvGallery);

        View.OnClickListener playTrailerListener = v -> {
            Toast.makeText(getContext(), "Tính năng đang được phát triển", Toast.LENGTH_SHORT).show();
        };

        view.findViewById(R.id.btnPlayTrailer).setOnClickListener(playTrailerListener);
        imgTrailerThumb.setOnClickListener(playTrailerListener);
    }

    private void setupSeeMore() {
        btnSeeMore.setOnClickListener(v -> {
            isExpanded = !isExpanded;
            tvDescription.setMaxLines(isExpanded ? Integer.MAX_VALUE : 5);
            btnSeeMore.setText(isExpanded ? "Thu gọn" : "Xem thêm");
        });
    }

    private void loadMovieInfo() {
        FirebaseFirestore.getInstance().collection("movies").document(movieId)
                .get().addOnSuccessListener(documentSnapshot -> {
                    currentMovie = documentSnapshot.toObject(Movie.class);
                    if (currentMovie != null) {
                        updateUI();
                    }
                });
    }

    private void updateUI() {
        tvMovieTitleHeader.setText(currentMovie.getTitle());
        tvRatingHeader.setText(String.valueOf(currentMovie.getRating()));
        tvDurationHeader.setText(currentMovie.getDuration() + " Phút");
        
        // Xử lý chuỗi xuống dòng literal "\n" từ Firestore
        String desc = currentMovie.getDescription();
        if (desc != null) {
            tvDescription.setText(desc.replace("\\n", "\n"));
        }

        if (currentMovie.getReleaseDate() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            tvReleaseDateHeader.setText(sdf.format(currentMovie.getReleaseDate().toDate()));
        }

        if (getContext() != null) {
            Glide.with(getContext()).load(currentMovie.getPosterUrl())
                    .placeholder(R.drawable.bg_placeholder).into(imgPosterHeader);
            Glide.with(getContext()).load(currentMovie.getPosterUrl())
                    .placeholder(R.drawable.bg_placeholder).into(imgTrailerThumb);
        }

        // Tách chuỗi Diễn viên & Đạo diễn
        List<String> castList = currentMovie.getCast() != null ? Arrays.asList(currentMovie.getCast().split(",")) : new ArrayList<>();
        List<String> directorList = currentMovie.getDirector() != null ? Arrays.asList(currentMovie.getDirector().split(",")) : new ArrayList<>();
        
        setupHorizontalList(rvCast, castList, true);
        setupHorizontalList(rvDirector, directorList, true);
        
        // Thư viện ảnh: Lấy từ CSDL (galleryUrls), nếu trống thì dùng tạm posterUrl
        List<String> gallery = currentMovie.getGalleryUrls();
        if (gallery == null || gallery.isEmpty()) {
            gallery = new ArrayList<>();
            gallery.add(currentMovie.getPosterUrl());
            gallery.add(currentMovie.getPosterUrl());
        }
        setupHorizontalList(rvGallery, gallery, false);
    }

    private void setupHorizontalList(RecyclerView rv, List<String> data, boolean isPeople) {
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(isPeople ? R.layout.item_cast : R.layout.item_gallery, parent, false);
                return isPeople ? new CastViewHolder(v) : new GalleryViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if (holder instanceof CastViewHolder) {
                    CastViewHolder h = (CastViewHolder) holder;
                    h.tvName.setText(data.get(position).trim());
                    // Không cần load Glide nữa, để mặc định lấy icon từ item_cast.xml
                } else if (holder instanceof GalleryViewHolder) {
                    GalleryViewHolder h = (GalleryViewHolder) holder;
                    Glide.with(MovieInfoFragment.this).load(data.get(position)).into(h.img);
                }
            }

            @Override
            public int getItemCount() { return data.size(); }
        });
    }

    static class CastViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView tvName;
        CastViewHolder(View v) { super(v); img = v.findViewById(R.id.imgCast); tvName = v.findViewById(R.id.tvCastName); }
    }

    static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        GalleryViewHolder(View v) { super(v); img = v.findViewById(R.id.imgGallery); }
    }
}
