package com.example.rapapp.fragments;

import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rapapp.R;
import com.example.rapapp.adapters.CinemaAdapter;
import com.example.rapapp.models.Cinema;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CinemaFragment extends Fragment {

    private RecyclerView rvCinemas;
    private CinemaAdapter cinemaAdapter;
    private List<Cinema> allCinemas;
    private List<Cinema> filteredCinemas;
    private TextView tvLocation;
    private String selectedLocation = "Toàn quốc";
    private final String[] locations = {
        "Toàn quốc", "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre", "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
    };

    public CinemaFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cinema, container, false);

        rvCinemas = view.findViewById(R.id.rvCinemas);
        tvLocation = view.findViewById(R.id.tvLocation);

        allCinemas = new ArrayList<>();
        filteredCinemas = new ArrayList<>();
        cinemaAdapter = new CinemaAdapter(getContext(), filteredCinemas);
        rvCinemas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCinemas.setAdapter(cinemaAdapter);

        tvLocation.setOnClickListener(v -> {
            v.animate().scaleX(0.9f).scaleY(0.9f).setDuration(100).withEndAction(() -> {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start();
                showLocationPickerDialog();
            }).start();
        });

        loadCinemasFromFirebase();

        return view;
    }

    private void showLocationPickerDialog() {
        if (getActivity() == null) return;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        bottomSheetDialog.setContentView(view);

        NumberPicker picker = view.findViewById(R.id.locationPicker);
        picker.setMinValue(0);
        picker.setMaxValue(locations.length - 1);
        picker.setDisplayedValues(locations);

        for (int i = 0; i < locations.length; i++) {
            if (locations[i].equals(selectedLocation)) {
                picker.setValue(i);
                break;
            }
        }

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedLocation = locations[picker.getValue()];
            tvLocation.setText(selectedLocation);
            bottomSheetDialog.dismiss();
            filterCinemas();
        });

        view.findViewById(R.id.btnClose).setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private void loadCinemasFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cinemas").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                allCinemas.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Cinema cinema = document.toObject(Cinema.class);
                    cinema.setId(document.getId());
                    allCinemas.add(cinema);
                }
                filterCinemas();
            } else {
                Log.e("CinemaFragment", "Error loading cinemas", task.getException());
            }
        });
    }

    private void filterCinemas() {
        filteredCinemas.clear();
        if (selectedLocation.equals("Toàn quốc")) {
            filteredCinemas.addAll(allCinemas);
        } else {
            for (Cinema cinema : allCinemas) {
                if (cinema.getCity() != null && cinema.getCity().equals(selectedLocation)) {
                    filteredCinemas.add(cinema);
                }
            }
        }
        cinemaAdapter.notifyDataSetChanged();
    }
}