package com.example.rapapp;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rapapp.models.Booking;
import com.example.rapapp.models.Movie;
import com.example.rapapp.models.Showtime;
import com.example.rapapp.utils.PriceUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class TransactionDetailActivity extends AppCompatActivity {

    private ImageView ivPosterDetail;
    private TextView tvTitleDetail, tvFormatDetail, tvAgeRatingDetail;
    private TextView tvCinemaDetail, tvTimeDateDetail, tvBookingIdDetail;
    private TextView tvStarsEarnedDetail, tvTotalPriceDetail;
    private android.widget.LinearLayout layoutShopItems;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_detail);

        db = FirebaseFirestore.getInstance();
        initViews();

        Booking booking = (Booking) getIntent().getSerializableExtra("booking");
        if (booking != null) {
            displayBookingInfo(booking);
        } else {
            finish();
        }
    }

    private void initViews() {
        ivPosterDetail = findViewById(R.id.ivPosterDetail);
        tvTitleDetail = findViewById(R.id.tvTitleDetail);
        tvFormatDetail = findViewById(R.id.tvFormatDetail);
        tvAgeRatingDetail = findViewById(R.id.tvAgeRatingDetail);
        tvCinemaDetail = findViewById(R.id.tvCinemaDetail);
        tvTimeDateDetail = findViewById(R.id.tvTimeDateDetail);
        tvBookingIdDetail = findViewById(R.id.tvBookingIdDetail);
        tvStarsEarnedDetail = findViewById(R.id.tvStarsEarnedDetail);
        tvTotalPriceDetail = findViewById(R.id.tvTotalPriceDetail);
        layoutShopItems = findViewById(R.id.layoutShopItems);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCloseDetail).setOnClickListener(v -> finish());
        findViewById(R.id.btnExportInvoice).setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void displayBookingInfo(Booking booking) {
        if (booking.getId() != null) {
            tvBookingIdDetail.setText(booking.getId().substring(0, Math.min(booking.getId().length(), 8)).toUpperCase());
        }
        tvTotalPriceDetail.setText(PriceUtils.formatCurrency(booking.getTotalPrice()));
        
        // Calculate stars (e.g., 5% of total price)
        int stars = (int) (booking.getTotalPrice() / 20000); // 1 star per 20k
        tvStarsEarnedDetail.setText(String.valueOf(stars));

        // Display summary title and image if available
        if (booking.getMainTitle() != null) {
            tvTitleDetail.setText(booking.getMainTitle());
        }
        
        if (booking.getMainImage() != null && !booking.getMainImage().isEmpty()) {
            Glide.with(this).load(booking.getMainImage()).placeholder(R.drawable.bg_placeholder).into(ivPosterDetail);
        } else {
            int placeholder = "movie_ticket".equals(booking.getType()) ? R.drawable.bg_placeholder : R.drawable.gift;
            ivPosterDetail.setImageResource(placeholder);
        }

        if ("movie_ticket".equals(booking.getType())) {
            showMovieBreakdown(booking);
            if (booking.getMainTitle() == null) {
                db.collection("movies").document(booking.getMovieId()).get().addOnSuccessListener(movieDoc -> {
                    Movie movie = movieDoc.toObject(Movie.class);
                    if (movie != null) {
                        tvTitleDetail.setText(movie.getTitle());
                        tvAgeRatingDetail.setText(movie.getAgeRating());
                        Glide.with(this).load(movie.getPosterUrl()).placeholder(R.drawable.bg_placeholder).into(ivPosterDetail);
                    }
                });
            }

            db.collection("showtimes").document(booking.getShowtimeId()).get().addOnSuccessListener(showtimeDoc -> {
                Showtime showtime = showtimeDoc.toObject(Showtime.class);
                if (showtime != null) {
                    tvFormatDetail.setText(showtime.getFormat());
                    
                    if (booking.getCinemaName() != null) {
                        tvCinemaDetail.setText(booking.getCinemaName());
                    } else if (showtime.getCinemaId() != null) {
                        db.collection("cinemas").document(showtime.getCinemaId()).get().addOnSuccessListener(cinemaDoc -> {
                            com.example.rapapp.models.Cinema cinema = cinemaDoc.toObject(com.example.rapapp.models.Cinema.class);
                            if (cinema != null) tvCinemaDetail.setText(cinema.getName());
                        });
                    }
                    
                    tvTimeDateDetail.setText("Suất " + showtime.getTime() + " - " + showtime.getDate());
                }
            });
        } else {
            tvAgeRatingDetail.setVisibility(View.GONE);
            tvCinemaDetail.setText("Nhận tại: " + booking.getCinemaName());
            tvTimeDateDetail.setText("Trạng thái: Đã hoàn thành");
            
            showShopItems(booking.getItems());
        }
    }

    private void showMovieBreakdown(Booking booking) {
        layoutShopItems.removeAllViews();
        layoutShopItems.setVisibility(View.VISIBLE);

        String shortId = booking.getId() != null ? booking.getId().substring(0, Math.min(booking.getId().length(), 8)).toUpperCase() : "UNKNOWN";

        // Thêm tiêu đề
        TextView tvHeader = new TextView(this);
        tvHeader.setText("Chi tiết đơn hàng:");
        tvHeader.setTextColor(Color.BLACK);
        tvHeader.setTextSize(14);
        tvHeader.setTypeface(null, android.graphics.Typeface.BOLD);
        tvHeader.setPadding(0, 0, 0, 8);
        layoutShopItems.addView(tvHeader);

        // Danh sách ghế
        List<String> seats = booking.getSeats();
        if (seats != null && !seats.isEmpty()) {
            double pricePerSeat = booking.getSeatTotalPrice() / seats.size();
            int starsPerSeat = (int) (pricePerSeat / 20000);
            
            for (String seat : seats) {
                TextView tvSeat = new TextView(this);
                String text = "• Vé ghế " + seat + " (Mã: " + shortId + "-" + seat + ")\n" + 
                              "  Giá: " + PriceUtils.formatCurrency(pricePerSeat) + " | +" + starsPerSeat + " Stars";
                tvSeat.setText(text);
                tvSeat.setTextColor(Color.parseColor("#333333"));
                tvSeat.setTextSize(13);
                tvSeat.setPadding(0, 4, 0, 8);
                layoutShopItems.addView(tvSeat);
            }
        }

        // Danh sách combo
        List<String> combos = booking.getCombos();
        List<String> comboPrices = booking.getComboPrices();
        if (combos != null && !combos.isEmpty()) {
            for (int i = 0; i < combos.size(); i++) {
                String comboName = combos.get(i);
                String comboPriceStr = (comboPrices != null && i < comboPrices.size()) ? comboPrices.get(i) : "0đ";
                
                int comboStars = 0;
                try {
                    String numStr = comboPriceStr.replaceAll("[^0-9]", "");
                    if (!numStr.isEmpty()) {
                        double cPrice = Double.parseDouble(numStr);
                        comboStars = (int) (cPrice / 20000);
                    }
                } catch(Exception ignored){}

                TextView tvCombo = new TextView(this);
                String text = "• " + comboName + "\n" + 
                              "  Giá: " + comboPriceStr + " | +" + comboStars + " Stars";
                tvCombo.setText(text);
                tvCombo.setTextColor(Color.parseColor("#333333"));
                tvCombo.setTextSize(13);
                tvCombo.setPadding(0, 4, 0, 8);
                layoutShopItems.addView(tvCombo);
            }
        }
    }

    private void showShopItems(List<Map<String, Object>> items) {
        if (items == null || items.isEmpty()) return;
        
        layoutShopItems.removeAllViews();
        layoutShopItems.setVisibility(View.VISIBLE);
        
        for (Map<String, Object> item : items) {
            TextView tvItem = new TextView(this);
            String name = (String) item.get("productName");
            long qty = 0;
            Object qtyObj = item.get("quantity");
            if (qtyObj instanceof Long) qty = (long) qtyObj;
            else if (qtyObj instanceof Integer) qty = (int) qtyObj;
            
            tvItem.setText("• " + qty + "x " + name);
            tvItem.setTextColor(Color.parseColor("#333333"));
            tvItem.setTextSize(14);
            tvItem.setPadding(0, 4, 0, 4);
            layoutShopItems.addView(tvItem);
        }
    }
}
