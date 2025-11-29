package com.example.manosadomicilio.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;

public class Home extends BottomMenu {

    public ImageView ivCerrajeria, ivLimpieza, ivPintura, ivFumigacion, ivJardineria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home);
        setupBottomMenu();

        ivCerrajeria = findViewById(R.id.ivCerrajeria);
        ivLimpieza = findViewById(R.id.ivLimpieza);
        ivPintura = findViewById(R.id.ivPintura);
        ivFumigacion = findViewById(R.id.ivFumigacion);
        ivJardineria = findViewById(R.id.ivJardineria);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ivCerrajeria.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CategoriaCerrajeria.class);
            startActivity(intent);
        });

        ivLimpieza.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CategoriaLimpieza.class);
            startActivity(intent);
        });

        ivPintura.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CategoriaPintura.class);
            startActivity(intent);
        });

        ivFumigacion.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CategoriaFumigacion.class);
            startActivity(intent);
        });

        ivJardineria.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, CategoriaJardineria.class);
            startActivity(intent);
        });
    }
}