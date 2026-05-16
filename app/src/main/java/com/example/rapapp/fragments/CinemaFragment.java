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
    private List<String> locations = new ArrayList<>();

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
        loadLocationsFromFirebase();

        return view;
    }

    private void loadLocationsFromFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("metadata").document("locations").get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<String> locList = (List<String>) documentSnapshot.get("list");
                if (locList != null) {
                    locations.clear();
                    locations.addAll(locList);
                }
            }
        });
    }

    private void showLocationPickerDialog() {
        if (getActivity() == null) return;
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        View view = getLayoutInflater().inflate(R.layout.dialog_location_picker, null);
        bottomSheetDialog.setContentView(view);

        NumberPicker picker = view.findViewById(R.id.locationPicker);
        if (locations.isEmpty()) {
            locations.add("Toàn quốc");
        }
        picker.setMinValue(0);
        picker.setMaxValue(locations.size() - 1);
        picker.setDisplayedValues(locations.toArray(new String[0]));

        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).equals(selectedLocation)) {
                picker.setValue(i);
                break;
            }
        }

        view.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            selectedLocation = locations.get(picker.getValue());
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