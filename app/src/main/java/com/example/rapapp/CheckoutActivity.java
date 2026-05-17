package com.example.rapapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.rapapp.utils.PriceUtils;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    private ImageView ivPoster;
    private TextView tvMovieTitle, tvFormat, tvCinemaName, tvTimeAndDate;
    private TextView tvSeatsDesc, tvSeatsPrice;
    private LinearLayout layoutCombos;
    private TextView tvSubTotal, tvFinalPrice;
    private String showtimeId;
    private ArrayList<String> selectedSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        initViews();
        loadData();
    }

    private void initViews() {
        ivPoster = findViewById(R.id.ivPoster);
        tvMovieTitle = findViewById(R.id.tvMovieTitle);
        tvFormat = findViewById(R.id.tvFormat);
        tvCinemaName = findViewById(R.id.tvCinemaName);
        tvTimeAndDate = findViewById(R.id.tvTimeAndDate);

        tvSeatsDesc = findViewById(R.id.tvSeatsDesc);
        tvSeatsPrice = findViewById(R.id.tvSeatsPrice);
        layoutCombos = findViewById(R.id.layoutCombos);
        
        tvSubTotal = findViewById(R.id.tvSubTotal);
        tvFinalPrice = findViewById(R.id.tvFinalPrice);

        setupPaymentMethods();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCheckout).setOnClickListener(v -> {
            if (showtimeId != null && selectedSeats != null && !selectedSeats.isEmpty()) {
                // Cập nhật ghế lên Firebase
                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("showtimes")
                        .document(showtimeId)
                        .update("bookedSeats", com.google.firebase.firestore.FieldValue.arrayUnion(selectedSeats.toArray()))
                        .addOnSuccessListener(aVoid -> showSuccessDialog())
                        .addOnFailureListener(e -> Toast.makeText(this, "Lỗi cập nhật ghế: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                showSuccessDialog(); // Fallback an toàn
            }
        });
    }

    private void showSuccessDialog() {
        android.app.Dialog dialog = new android.app.Dialog(this);
        dialog.setContentView(R.layout.dialog_payment_success);
        
        // Đặt nền trong suốt cho Dialog để hiển thị được bo góc
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        
        dialog.setCancelable(false); // Không cho phép tắt bằng cách bấm ra ngoài

        dialog.findViewById(R.id.btnGoHome).setOnClickListener(v -> {
            dialog.dismiss();
            // Quay về màn hình Home và xóa các Activity trước đó khỏi stack
            android.content.Intent intent = new android.content.Intent(CheckoutActivity.this, MainActivity.class);
            intent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        dialog.show();
    }

    private void setupPaymentMethods() {
        LinearLayout payOnePay = findViewById(R.id.payOnePay);
        LinearLayout payShopee = findViewById(R.id.payShopee);
        LinearLayout payZalo = findViewById(R.id.payZalo);
        LinearLayout payMomo = findViewById(R.id.payMomo);

        android.widget.RadioButton rbOnePay = findViewById(R.id.rbOnePay);
        android.widget.RadioButton rbShopee = findViewById(R.id.rbShopee);
        android.widget.RadioButton rbZalo = findViewById(R.id.rbZalo);
        android.widget.RadioButton rbMomo = findViewById(R.id.rbMomo);

        View.OnClickListener clickListener = v -> {
            rbOnePay.setChecked(v == payOnePay || v == rbOnePay);
            rbShopee.setChecked(v == payShopee || v == rbShopee);
            rbZalo.setChecked(v == payZalo || v == rbZalo);
            rbMomo.setChecked(v == payMomo || v == rbMomo);
        };

        payOnePay.setOnClickListener(clickListener);
        payShopee.setOnClickListener(clickListener);
        payZalo.setOnClickListener(clickListener);
        payMomo.setOnClickListener(clickListener);
        
        rbOnePay.setOnClickListener(clickListener);
        rbShopee.setOnClickListener(clickListener);
        rbZalo.setOnClickListener(clickListener);
        rbMomo.setOnClickListener(clickListener);

        // Mặc định chọn OnePay
        rbOnePay.setChecked(true);
    }

    private void loadData() {
        android.content.Intent intent = getIntent();
        
        String movieTitle = intent.getStringExtra("movieTitle");
        String format = intent.getStringExtra("format");
        String cinemaName = intent.getStringExtra("cinemaName");
        String time = intent.getStringExtra("time");
        String date = intent.getStringExtra("date");
        String posterUrl = intent.getStringExtra("posterUrl");
        showtimeId = intent.getStringExtra("showtimeId");

        selectedSeats = intent.getStringArrayListExtra("selectedSeats");
        double seatTotalPrice = intent.getDoubleExtra("totalPrice", 0);
        
        ArrayList<String> selectedCombos = intent.getStringArrayListExtra("selectedCombos");
        ArrayList<String> selectedComboPrices = intent.getStringArrayListExtra("selectedComboPrices");
        double comboTotalPrice = intent.getDoubleExtra("comboTotalPrice", 0);
        double finalTotalPrice = intent.getDoubleExtra("finalTotalPrice", 0);

        // Hiển thị thông tin phim
        if (movieTitle != null) tvMovieTitle.setText(movieTitle);
        if (format != null) tvFormat.setText(format);
        if (cinemaName != null) tvCinemaName.setText(cinemaName);
        if (time != null && date != null) {
            // Định dạng lại ngày từ yyyy-MM-dd sang dd/MM/yyyy
            try {
                java.text.SimpleDateFormat sdfIn = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                java.text.SimpleDateFormat sdfOut = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                java.util.Date parsedDate = sdfIn.parse(date);
                if (parsedDate != null) {
                    date = sdfOut.format(parsedDate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvTimeAndDate.setText(time + " - " + date);
        }

        if (posterUrl != null) {
            Glide.with(this).load(posterUrl).into(ivPoster);
        }

        // Hiển thị thông tin ghế
        if (selectedSeats != null && !selectedSeats.isEmpty()) {
            tvSeatsDesc.setText(selectedSeats.size() + "x Ghế - " + TextUtils.join(", ", selectedSeats));
        }
        tvSeatsPrice.setText(PriceUtils.formatCurrency(seatTotalPrice));

        // Hiển thị thông tin Combo động
        layoutCombos.removeAllViews();
        if (selectedCombos != null && !selectedCombos.isEmpty()) {
            
            // Thêm đường kẻ đứt phân cách giữa Ghế và Combo
            View dashedTop = new View(this);
            dashedTop.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(2))); // Trả về 2dp để hiển thị được nét đứt
            dashedTop.setBackgroundResource(R.drawable.bg_dashed_line);
            dashedTop.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Bắt buộc để vẽ dash line
            LinearLayout.LayoutParams dParams = (LinearLayout.LayoutParams) dashedTop.getLayoutParams();
            dParams.setMargins(0, dpToPx(8), 0, dpToPx(8));
            layoutCombos.addView(dashedTop);

            for (int i = 0; i < selectedCombos.size(); i++) {
                String comboStr = selectedCombos.get(i);
                String comboPrice = (selectedComboPrices != null && i < selectedComboPrices.size()) ? selectedComboPrices.get(i) : "";

                // Dùng LinearLayout ngang để tự động đẩy giá sang phải và tên không bị đè
                LinearLayout comboRow = new LinearLayout(this);
                comboRow.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, dpToPx(8));
                comboRow.setLayoutParams(params);

                TextView tvName = new TextView(this);
                tvName.setText(comboStr);
                tvName.setTextColor(getResources().getColor(R.color.black));
                tvName.setTextSize(13);
                LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f); // weight = 1
                nameParams.setMarginEnd(dpToPx(16));
                tvName.setLayoutParams(nameParams);
                comboRow.addView(tvName);
                
                TextView tvPrice = new TextView(this);
                tvPrice.setText(comboPrice);
                tvPrice.setTextColor(getResources().getColor(R.color.black));
                tvPrice.setTextSize(13);
                comboRow.addView(tvPrice);
                
                layoutCombos.addView(comboRow);
            }
        }

        // Hiển thị Tổng cộng
        String formattedTotal = PriceUtils.formatCurrency(finalTotalPrice);
        tvSubTotal.setText(formattedTotal);
        tvFinalPrice.setText(formattedTotal);
    }
    
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
}
