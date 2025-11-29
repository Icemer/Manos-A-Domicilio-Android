package com.example.manosadomicilio.view;

import android.os.Bundle;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;

public class CategoriaCerrajeria extends BottomMenu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categoria_cerrajeria);
        setupBottomMenu();

    }
}
