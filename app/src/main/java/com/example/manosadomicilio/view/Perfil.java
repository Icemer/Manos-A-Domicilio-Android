package com.example.manosadomicilio.view;

import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;

public class Perfil extends BottomMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);
        setupBottomMenu();


    }
}
