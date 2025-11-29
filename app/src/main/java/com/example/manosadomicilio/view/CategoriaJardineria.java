package com.example.manosadomicilio.view;

import android.os.Bundle;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;

public class CategoriaJardineria extends BottomMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categoria_jardineria);
        setupBottomMenu();

    }
}