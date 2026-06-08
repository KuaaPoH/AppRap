package com.example.rapapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.rapapp.LoginActivity;
import com.example.rapapp.R;
import com.example.rapapp.RegisterActivity;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        AppCompatButton btnProfileRegister = view.findViewById(R.id.btnProfileRegister);
        AppCompatButton btnProfileLogin = view.findViewById(R.id.btnProfileLogin);
        ImageView btnSettings = view.findViewById(R.id.btnSettings);
        View btnAdmin = view.findViewById(R.id.btnAdmin);

        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.example.rapapp.admin.activities.AdminDashboardActivity.class);
                startActivity(intent);
            });
        }

        if (btnSettings != null) {
            btnSettings.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), com.example.rapapp.SettingsActivity.class);
                startActivity(intent);
            });
        }

        if (btnProfileRegister != null) {
            btnProfileRegister.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            });
        }

        if (btnProfileLogin != null) {
            btnProfileLogin.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }
}

