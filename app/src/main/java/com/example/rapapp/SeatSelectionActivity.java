package com.example.rapapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.rapapp.models.Movie;
import com.example.rapapp.models.Room;
import com.example.rapapp.models.Showtime;
import com.example.rapapp.utils.PriceUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {

    private String movieId, showtimeId, cinemaId;
    private FirebaseFirestore db;
    private Movie movie;
    private Showtime showtime;
    private Room room;

    private TextView tvMovieTitle, tvFormat, tvShowtime, tvCinemaName;
    private TextView tvSelectedSeats, tvTotalPrice;
    private LinearLayout layoutSeatContainer;
    private View layoutLoading;
    
    private List<String> selectedSeats = new ArrayList<>();
    private double totalPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        movieId = getIntent().getStringExtra("movieId");
        showtimeId = getIntent().getStringExtra("showtimeId");
        cinemaId = getIntent().getStringExtra("cinemaId");

        db = FirebaseFirestore.getInstance();
        initViews();
        setupLegends();
        loadData();
    }

    private void initViews() {
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvFormat = findViewById(R.id.tvFormat);
        tvShowtime = findViewById(R.id.tvShowtime);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvSelectedSeats = findViewById(R.id.tvSelectedSeats);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        layoutSeatContainer = findViewById(R.id.layoutSeatContainer);
        layoutLoading = findViewById(R.id.layoutLoading);

        // Hiển thị dữ liệu ngay lập tức từ Intent (Không cần chờ Firebase)
        String passedMovieTitle = getIntent().getStringExtra("movieTitle");
        String passedCinemaName = getIntent().getStringExtra("cinemaName");
        String passedFormat = getIntent().getStringExtra("format");
        String passedTime = getIntent().getStringExtra("time");

        if (passedMovieTitle != null) tvMovieTitle.setText(passedMovieTitle);
        if (passedCinemaName != null) tvCinemaName.setText(passedCinemaName);
        if (passedFormat != null) tvFormat.setText(passedFormat);
        if (passedTime != null) tvShowtime.setText(passedTime);

        // Vô hiệu hóa cuộn dọc và cuộn ngang để cố định sơ đồ hoàn toàn
        findViewById(R.id.scrollViewSeat).setOnTouchListener((v, event) -> true);
        findViewById(R.id.horizontalScrollViewSeat).setOnTouchListener((v, event) -> true);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnContinue).setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ghế!", Toast.LENGTH_SHORT).show();
            } else {
                android.content.Intent intent = new android.content.Intent(SeatSelectionActivity.this, ComboSelectionActivity.class);
                intent.putStringArrayListExtra("selectedSeats", new ArrayList<>(selectedSeats));
                intent.putExtra("totalPrice", totalPrice);
                intent.putExtra("movieId", movieId);
                intent.putExtra("showtimeId", showtimeId); // Quan trọng: Truyền showtimeId để Checkout biết cần cập nhật Firebase
                intent.putExtra("movieTitle", tvMovieTitle.getText().toString());
                intent.putExtra("cinemaName", tvCinemaName.getText().toString());
                intent.putExtra("format", tvFormat.getText().toString());
                intent.putExtra("time", tvShowtime.getText().toString());
                if (showtime != null) {
                    intent.putExtra("date", showtime.getDate());
                    intent.putExtra("posterUrl", movie.getPosterUrl());
                }
                startActivity(intent);
            }
        });
    }

    private void setupLegends() {
        int orange = Color.parseColor("#F58020");
        int blue = Color.parseColor("#034EA2");
        int yellow = Color.parseColor("#FFD700");
        int grey = Color.parseColor("#CCCCCC");
        int bookedGrey = Color.parseColor("#EAEAEA");

        findViewById(R.id.legendSingle).setBackground(getSeatDrawable(grey, Color.TRANSPARENT));
        findViewById(R.id.legendVip).setBackground(getSeatDrawable(yellow, Color.TRANSPARENT));
        findViewById(R.id.legendCouple).setBackground(getSeatDrawable(blue, Color.TRANSPARENT));
        findViewById(R.id.legendTriple).setBackground(getSeatDrawable(orange, Color.TRANSPARENT));
        findViewById(R.id.legendBooked).setBackground(getSeatDrawable(bookedGrey, bookedGrey));
        findViewById(R.id.legendSelected).setBackground(getSeatDrawable(orange, orange));
    }

    private void loadData() {
        if (movieId == null || showtimeId == null || cinemaId == null) {
            Toast.makeText(this, "Thiếu dữ liệu suất chiếu!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Đảm bảo Loading Overlay hiển thị ngay lập tức (chỉ che khu vực ghế)
        layoutLoading.setVisibility(View.VISIBLE);

        // Lấy giá phim (để tính tiền)
        db.collection("movies").document(movieId).get().addOnSuccessListener(movieDoc -> {
            movie = movieDoc.toObject(Movie.class);
            if (movie == null) {
                Toast.makeText(this, "Không tìm thấy thông tin phim!", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            
            // Nếu Intent bị thiếu tên phim, ta vẫn có thể fallback lấy từ Firebase
            if (tvMovieTitle.getText().toString().equals("Tên phim đang chọn")) {
                tvMovieTitle.setText(movie.getTitle());
            }

            db.collection("showtimes").document(showtimeId).get().addOnSuccessListener(showtimeDoc -> {
                showtime = showtimeDoc.toObject(Showtime.class);
                if (showtime == null) {
                    Toast.makeText(this, "Suất chiếu không tồn tại!", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                showtime.setId(showtimeDoc.getId());
                
                // Tải các suất chiếu khác trong ngày để tạo Dropdown
                loadAvailableShowtimes(showtime.getDate());
                
                // Fallback nếu thiếu từ Intent
                if (tvFormat.getText().toString().equals("2D LỒNG TIẾNG")) tvFormat.setText(showtime.getFormat());
                if (tvShowtime.getText().toString().equals("12:30")) tvShowtime.setText(showtime.getTime());

                db.collection("cinemas").document(cinemaId).get().addOnSuccessListener(cinemaDoc -> {
                    if (cinemaDoc.exists() && tvCinemaName.getText().toString().equals("Galaxy CineX - Hanoi Centre")) {
                        tvCinemaName.setText(cinemaDoc.getString("name"));
                    }
                    
                    if (showtime.getRoomId() != null && !showtime.getRoomId().isEmpty()) {
                        db.collection("rooms").document(showtime.getRoomId()).get().addOnSuccessListener(roomDoc -> {
                            room = roomDoc.toObject(Room.class);
                            if (room != null) {
                                room.setId(roomDoc.getId());
                                renderSeatMap();
                                // Chỉ ẩn Loading khi sơ đồ ghế đã vẽ xong
                                layoutLoading.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(this, "Không tìm thấy sơ đồ phòng!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(this, "Lỗi tải phòng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    } else {
                        Toast.makeText(this, "Suất chiếu này chưa được gán phòng!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Lỗi kết nối CSDL!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void renderSeatMap() {
        layoutSeatContainer.removeAllViews();
        if (room == null || room.getLayout() == null) return;
        
        List<String> layout = room.getLayout();
        char rowChar = 'I';

        for (int r = 0; r < layout.size(); r++) {
            String rowData = layout.get(r);
            LinearLayout rowLayout = new LinearLayout(this);
            rowLayout.setOrientation(LinearLayout.HORIZONTAL);
            rowLayout.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            rowLayout.setPadding(0, 2, 0, 2);

            // Row Label (Left) - Cố định độ rộng để căn lề thẳng hàng
            TextView tvLabel = new TextView(this);
            tvLabel.setText(String.valueOf((char)(rowChar - r)));
            tvLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
            tvLabel.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
            
            // Đặt độ rộng cố định cho cột nhãn hàng (VD: 24dp)
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(dpToPx(24), ViewGroup.LayoutParams.WRAP_CONTENT);
            tvLabel.setLayoutParams(labelParams);
            rowLayout.addView(tvLabel);

            for (int c = 0; c < rowData.length(); c++) {
                String type = String.valueOf(rowData.charAt(c));
                if (type.equals(Room.SEAT_TYPE_EMPTY)) {
                    View emptySpace = new View(this);
                    LinearLayout.LayoutParams emptyParams = new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20));
                    emptyParams.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
                    emptySpace.setLayoutParams(emptyParams);
                    rowLayout.addView(emptySpace);
                    continue;
                }

                String seatCode = (char)(rowChar - r) + String.valueOf(c + 1);
                boolean isBooked = showtime.getBookedSeats() != null && showtime.getBookedSeats().contains(seatCode);

                View seatView = createSeatView(type, seatCode, isBooked);
                rowLayout.addView(seatView);
            }

            layoutSeatContainer.addView(rowLayout);
        }
    }

    private View createSeatView(String type, String seatCode, boolean isBooked) {
        TextView seat = new TextView(this);
        int size = dpToPx(20); // Thu nhỏ từ 24dp xuống 20dp
        int margin = dpToPx(2); // Thu hẹp margin từ 3dp xuống 2dp
        
        // Điều chỉnh chiều rộng nếu là ghế đôi/ba để khớp hoàn hảo với lưới
        // Ghế đôi = 2 ghế + 1 khoảng trống (2 lần margin)
        // Ghế ba = 3 ghế + 2 khoảng trống (4 lần margin)
        int width = size;
        if (type.equals(Room.SEAT_TYPE_COUPLE)) width = size * 2 + margin * 2;
        if (type.equals(Room.SEAT_TYPE_TRIPLE)) width = size * 3 + margin * 4;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, size);
        params.setMargins(margin, margin, margin, margin);
        seat.setLayoutParams(params);
        seat.setGravity(Gravity.CENTER);
        seat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8); // Giảm cỡ chữ xuống 8sp
        seat.setTextColor(Color.WHITE);

        updateSeatUI(seat, type, seatCode, false, isBooked);

        if (!isBooked) {
            seat.setOnClickListener(v -> {
                // Hiệu ứng nảy (Pop Animation)
                v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(100).withEndAction(() -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                }).start();

                boolean isSelected = selectedSeats.contains(seatCode);
                if (isSelected) {
                    selectedSeats.remove(seatCode);
                    totalPrice -= PriceUtils.calculateSeatPrice(movie.getPrice(), type);
                } else {
                    if (selectedSeats.size() >= 8) {
                        Toast.makeText(this, "Chỉ được chọn tối đa 8 ghế", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedSeats.add(seatCode);
                    totalPrice += PriceUtils.calculateSeatPrice(movie.getPrice(), type);
                }
                updateSeatUI(seat, type, seatCode, !isSelected, false);
                updateSummary();
            });
        }

        return seat;
    }

    private GradientDrawable getSeatDrawable(int strokeColor, int fillColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(fillColor);
        gd.setCornerRadius(dpToPx(4));
        gd.setStroke(dpToPx(1), strokeColor);
        return gd;
    }

    private void updateSeatUI(TextView seat, String type, String seatCode, boolean isSelected, boolean isBooked) {
        int orange = Color.parseColor("#F58020");
        int blue = Color.parseColor("#034EA2");
        int yellow = Color.parseColor("#FFD700");
        int grey = Color.parseColor("#CCCCCC");
        int bookedGrey = Color.parseColor("#EAEAEA");

        if (isBooked) {
            seat.setBackground(getSeatDrawable(bookedGrey, bookedGrey));
            seat.setText("");
            return;
        }

        if (isSelected) {
            seat.setBackground(getSeatDrawable(orange, orange));
            seat.setText(seatCode);
        } else {
            seat.setText("");
            switch (type) {
                case Room.SEAT_TYPE_VIP:
                    seat.setBackground(getSeatDrawable(yellow, Color.TRANSPARENT));
                    break;
                case Room.SEAT_TYPE_COUPLE:
                    seat.setBackground(getSeatDrawable(blue, Color.TRANSPARENT));
                    break;
                case Room.SEAT_TYPE_TRIPLE:
                    seat.setBackground(getSeatDrawable(orange, Color.TRANSPARENT));
                    break;
                case Room.SEAT_TYPE_SINGLE:
                default:
                    seat.setBackground(getSeatDrawable(grey, Color.TRANSPARENT));
                    break;
            }
        }
    }

    private void updateSummary() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("Chưa chọn ghế");
        } else {
            tvSelectedSeats.setText(selectedSeats.size() + "x ghế: " + TextUtils.join(", ", selectedSeats));
        }
        tvTotalPrice.setText("Tổng Cộng: " + PriceUtils.formatCurrency(totalPrice));
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private void loadAvailableShowtimes(String date) {
        db.collection("showtimes")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("cinemaId", cinemaId)
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Showtime> availableShowtimes = new ArrayList<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        Showtime s = doc.toObject(Showtime.class);
                        if (s != null) {
                            s.setId(doc.getId());
                            availableShowtimes.add(s);
                        }
                    }
                    if (availableShowtimes.size() > 1) {
                        java.util.Collections.sort(availableShowtimes, (s1, s2) -> s1.getTime().compareTo(s2.getTime()));
                        tvShowtime.setOnClickListener(v -> showTimeSelectionDropdown(v, availableShowtimes));
                    }
                });
    }

    private void showTimeSelectionDropdown(View anchor, List<Showtime> availableShowtimes) {
        LinearLayout dropdownLayout = new LinearLayout(this);
        dropdownLayout.setOrientation(LinearLayout.VERTICAL);
        dropdownLayout.setBackgroundResource(R.drawable.bg_dropdown);
        dropdownLayout.setClipToOutline(true);

        for (int i = 0; i < availableShowtimes.size(); i++) {
            Showtime s = availableShowtimes.get(i);
            TextView tv = new TextView(this);
            tv.setText(s.getTime()); // Chỉ hiện giờ, bỏ format
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            
            if (s.getId().equals(showtimeId)) {
                tv.setTextColor(Color.parseColor("#F58020")); // Màu cam cho giờ đang chọn
                tv.setTypeface(null, android.graphics.Typeface.BOLD);
            } else {
                tv.setTextColor(Color.parseColor("#333333"));
            }
            
            // Căn giữa text trong dropdown cho cân đối
            tv.setPadding(0, dpToPx(10), 0, dpToPx(10));
            tv.setGravity(Gravity.CENTER);
            
            // Hiệu ứng ripple khi chạm
            TypedValue outValue = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            tv.setBackgroundResource(outValue.resourceId);
            
            dropdownLayout.addView(tv);

            // Đường kẻ phân cách giữa các item
            if (i < availableShowtimes.size() - 1) {
                View divider = new View(this);
                divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(1)));
                divider.setBackgroundColor(Color.parseColor("#EEEEEE"));
                dropdownLayout.addView(divider);
            }
        }

        android.widget.PopupWindow popupWindow = new android.widget.PopupWindow(
                dropdownLayout,
                anchor.getWidth(), // Chiều rộng bằng đúng với ô Giờ chiếu (anchor)
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
        );
        
        // Cấu hình nền trong suốt để giữ nguyên bo góc và bóng (elevation)
        popupWindow.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
        popupWindow.setElevation(dpToPx(8));
        
        // Gắn sự kiện click
        for (int i = 0; i < dropdownLayout.getChildCount(); i++) {
            View child = dropdownLayout.getChildAt(i);
            if (child instanceof TextView) {
                int index = i / 2; // Do có divider xen kẽ
                Showtime s = availableShowtimes.get(index);
                child.setOnClickListener(v -> {
                    popupWindow.dismiss();
                    if (!s.getId().equals(showtimeId)) {
                        android.content.Intent intent = getIntent();
                        intent.putExtra("showtimeId", s.getId());
                        intent.putExtra("time", s.getTime());
                        intent.putExtra("format", s.getFormat());
                        finish();
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                });
            }
        }

        // Hiển thị Dropdown ngay dưới nút chọn giờ, lệch xuống 4dp
        popupWindow.showAsDropDown(anchor, 0, dpToPx(4));
    }
}
