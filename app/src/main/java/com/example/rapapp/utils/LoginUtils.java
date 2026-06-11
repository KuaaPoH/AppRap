package com.example.rapapp.utils;

import android.content.Context;
import android.content.Intent;

import com.example.rapapp.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginUtils {
    public static boolean isUserLoggedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public static void redirectToLogin(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
