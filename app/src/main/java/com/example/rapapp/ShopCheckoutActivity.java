package com.example.rapapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.adapters.CheckoutSummaryAdapter;
import com.example.rapapp.models.Cinema;
import com.example.rapapp.utils.CartManager;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ShopCheckoutActivity extends AppCompatActivity {

    private RecyclerView rvOrderSummary;
    private CheckoutSummaryAdapter summaryAdapter;
    private TextView tvDiscountAmount, tvTotalAmount, tvFinalPayment;
    private TextView tvSelectedCity, tvSelectedCinema, tvCinemaAddress, tvAvailablePoints;
    private View etVoucher, etStarPoints;
    private ImageView ivExpandSummary, ivExpandVoucher, ivExpandStarPoints;
    private View layoutZalo, layoutMomo, layoutOnePay;
    private RadioButton rbZalo, rbMomo, rbOnePay;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
    private boolean isSummaryExpanded = true;
    private boolean isVoucherExpanded = false;
    private boolean isStarPointsExpanded = false;

    private List<String> cities = new ArrayList<>();
    private List<Cinema> cinemasInCity = new ArrayList<>();
    private String selectedCity = "TP Hồ Chí Minh";
    private Cinema selectedCinema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_checkout);

        initViews();
        setupSummary();
        loadInitialData();
        updateAmounts();
        setupPaymentMethods();
        setupLocationPickers();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCancel).setOnClickListener(v -> finish());
        findViewById(R.id.btnSubmitOrder).setOnClickListener(v -> {
            if (!rbZalo.isChecked() && !rbMomo.isChecked() && !rbOnePay.isChecked()) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedCinema == null) {
                Toast.makeText(this, "Vui lòng chọn rạp nhận hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            showSuccessDialog();
        });

        setupExpandables();
    }

    private void initViews() {
        rvOrderSummary = findViewById(R.id.rvOrderSummary);
        tvDiscountAmount = findViewById(R.id.tvDiscountAmount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvFinalPayment = findViewById(R.id.tvFinalPayment);
        tvSelectedCity = findViewById(R.id.tvSelectedCity);
        tvSelectedCinema = findViewById(R.id.tvSelectedCinema);
        tvCinemaAddress = findViewById(R.id.tvCinemaAddress);
        tvAvailablePoints = findViewById(R.id.tvAvailablePoints);
        etVoucher = findViewById(R.id.etVoucher);
        etStarPoints = findViewById(R.id.etStarPoints);
        ivExpandSummary = findViewById(R.id.ivExpandSummary);
        ivExpandVoucher = findViewById(R.id.ivExpandVoucher);
        ivExpandStarPoints = findViewById(R.id.ivExpandStarPoints);

        layoutZalo = findViewById(R.id.layoutZalo);
        layoutMomo = findViewById(R.id.layoutMomo);
        layoutOnePay = findViewById(R.id.layoutOnePay);
        rbZalo = findViewById(R.id.rbZalo);
        rbMomo = findViewById(R.id.rbMomo);
        rbOnePay = findViewById(R.id.rbOnePay);
    }

    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_shop_success);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        dialog.findViewById(R.id.btnGoHome).setOnClickListener(v -> {
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        dialog.findViewById(R.id.btnContinue).setOnClickListener(v -> {
            CartManager.getInstance().clearCart();
            dialog.dismiss();
            
            // Quay về MainActivity và chọn tab Star Shop (thường là tab thứ 3, index 2)
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("SELECT_TAB", "STAR_SHOP");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        dialog.show();
    }

    private void loadInitialData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        
        // Load Cities
        db.collection("metadata").document("locations").get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                List<String> list = (List<String>) doc.get("list");
                if (list != null) {
                    cities.clear();
                    cities.addAll(list);
                }
            }
        });

        // Load Default Cinemas for initial city
        loadCinemasForCity(selectedCity);
    }

    private void loadCinemasForCity(String city) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cinemas").whereEqualTo("city", city).get().addOnSuccessListener(queryDocumentSnapshots -> {
            cinemasInCity.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                cinemasInCity.add(doc.toObject(Cinema.class));
            }
            
            if (!cinemasInCity.isEmpty()) {
                selectedCinema = cinemasInCity.get(0);
                tvSelectedCinema.setText(selectedCinema.getName());
                tvCinemaAddress.setText(selectedCinema.getAddress());
            } else {
                selectedCinema = null;
                tvSelectedCinema.setText("Không có rạp tại đây");
                tvCinemaAddress.setText("Vui lòng chọn thành phố khác");
            }
            tvSelectedCity.setText(city);
        });
    }

    private void setupLocationPickers() {
        findViewById(R.id.layoutCityPicker).setOnClickListener(v -> showCityPickerDialog());
        findViewById(R.id.layoutCinemaPicker).setOnClickListener(v -> {
            if (cinemasInCity.isEmpty()) {
                Toast.makeText(this, "Không có rạp nào để chọn", Toast.LENGTH_SHORT).show();
            } else {
                showCinemaPickerDialog();
            }
        });
    }

    private void showCityPickerDialog() {
        if (cities.isEmpty()) return;
        
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        dialog.setContentView(view);
        dialog.getBehavior().setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("Chọn Thành Phố");

        NumberPicker picker = view.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(cities.size() - 1);
        picker.setDisplayedValues(cities.toArray(new String[0]));
        
        int currentIdx = cities.indexOf(selectedCity);
        if (currentIdx != -1) picker.setValue(currentIdx);

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedCity = cities.get(picker.getValue());
            loadCinemasForCity(selectedCity);
            dialog.dismiss();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showCinemaPickerDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        dialog.setContentView(view);
        dialog.getBehavior().setPeekHeight(getResources().getDisplayMetrics().heightPixels / 2);

        TextView tvTitle = view.findViewById(R.id.tvTitle);
        tvTitle.setText("Chọn Rạp Nhận Hàng");

        List<String> cinemaNames = new ArrayList<>();
        for (Cinema c : cinemasInCity) cinemaNames.add(c.getName());

        NumberPicker picker = view.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(cinemaNames.size() - 1);
        picker.setDisplayedValues(cinemaNames.toArray(new String[0]));

        int currentIdx = cinemasInCity.indexOf(selectedCinema);
        if (currentIdx != -1) picker.setValue(currentIdx);

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedCinema = cinemasInCity.get(picker.getValue());
            tvSelectedCinema.setText(selectedCinema.getName());
            tvCinemaAddress.setText(selectedCinema.getAddress());
            dialog.dismiss();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void setupPaymentMethods() {
        View.OnClickListener listener = v -> {
            // Reset all
            layoutZalo.setSelected(false);
            layoutMomo.setSelected(false);
            layoutOnePay.setSelected(false);
            rbZalo.setChecked(false);
            rbMomo.setChecked(false);
            rbOnePay.setChecked(false);

            // Select clicked
            if (v.getId() == R.id.layoutZalo) {
                layoutZalo.setSelected(true);
                rbZalo.setChecked(true);
            } else if (v.getId() == R.id.layoutMomo) {
                layoutMomo.setSelected(true);
                rbMomo.setChecked(true);
            } else if (v.getId() == R.id.layoutOnePay) {
                layoutOnePay.setSelected(true);
                rbOnePay.setChecked(true);
            }
        };

        layoutZalo.setOnClickListener(listener);
        layoutMomo.setOnClickListener(listener);
        layoutOnePay.setOnClickListener(listener);
    }

    private void setupSummary() {
        summaryAdapter = new CheckoutSummaryAdapter(this, CartManager.getInstance().getCartItems());
        rvOrderSummary.setLayoutManager(new LinearLayoutManager(this));
        rvOrderSummary.setAdapter(summaryAdapter);
    }

    private void setupExpandables() {
        findViewById(R.id.layoutSummaryHeader).setOnClickListener(v -> {
            isSummaryExpanded = !isSummaryExpanded;
            TransitionManager.beginDelayedTransition((ViewGroup) rvOrderSummary.getParent());
            rvOrderSummary.setVisibility(isSummaryExpanded ? View.VISIBLE : View.GONE);
            ivExpandSummary.setRotation(isSummaryExpanded ? 0 : 180);
        });

        findViewById(R.id.layoutVoucherHeader).setOnClickListener(v -> {
            isVoucherExpanded = !isVoucherExpanded;
            TransitionManager.beginDelayedTransition((ViewGroup) etVoucher.getParent());
            etVoucher.setVisibility(isVoucherExpanded ? View.VISIBLE : View.GONE);
            ivExpandVoucher.setRotation(isVoucherExpanded ? 180 : 0);
        });

        findViewById(R.id.layoutStarPointsHeader).setOnClickListener(v -> {
            isStarPointsExpanded = !isStarPointsExpanded;
            TransitionManager.beginDelayedTransition((ViewGroup) etStarPoints.getParent());
            etStarPoints.setVisibility(isStarPointsExpanded ? View.VISIBLE : View.GONE);
            tvAvailablePoints.setVisibility(isStarPointsExpanded ? View.VISIBLE : View.GONE);
            ivExpandStarPoints.setRotation(isStarPointsExpanded ? 180 : 0);
        });
    }

    private void updateAmounts() {
        long total = CartManager.getInstance().getTotalAmount();
        tvTotalAmount.setText(decimalFormat.format(total) + " VND");
        tvDiscountAmount.setText("0 VND");
        tvFinalPayment.setText(decimalFormat.format(total) + " VND");
    }
}
