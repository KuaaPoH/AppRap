package com.example.rapapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import java.util.Calendar;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ImageView btnClose = findViewById(R.id.btnClose);
        AppCompatButton btnGoToLogin = findViewById(R.id.btnGoToLogin);
        RelativeLayout rlDob = findViewById(R.id.rlDob);
        TextView tvDob = findViewById(R.id.tvDob);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> finish());
        }
        
        if (btnGoToLogin != null) {
            btnGoToLogin.setOnClickListener(v -> {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            });
        }

        if (rlDob != null && tvDob != null) {
            rlDob.setOnClickListener(v -> {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        RegisterActivity.this,
                        (view, selectedYear, selectedMonth, selectedDay) -> {
                            String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            tvDob.setText(date);
                            tvDob.setTextColor(ContextCompat.getColor(this, R.color.black));
                        },
                        year, month, day);
                datePickerDialog.show();
            });
        }
    }
}
