package com.example.manosadomicilio.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manosadomicilio.view.Home;
import com.example.manosadomicilio.R;
import com.example.manosadomicilio.view.Perfil;

public class BottomMenu extends AppCompatActivity {

    protected ImageView ivHome, ivPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupBottomMenu() {
        ivHome = findViewById(R.id.ivHome);
        ivPerfil = findViewById(R.id.ivPerfil);

        if (ivHome != null) {
            ivHome.setOnClickListener(v -> {
                Intent intent = new Intent(BottomMenu.this, Home.class);
                startActivity(intent);
            });
        }
        if (ivPerfil != null) {
            ivPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(BottomMenu.this, Perfil.class);
                startActivity(intent);
            });
        }
    }
}
