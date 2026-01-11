package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Home extends BottomMenu {

    private ImageView ivCerrajeria, ivLimpieza, ivPintura, ivFumigacion, ivJardineria;
    private TextView tvCerrajeria, tvLimpieza, tvPintura, tvFumigacion, tvJardineria, tvPregunta;
    private Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        setupBottomMenu();

        // Referencias a las vistas de categorías
        ivCerrajeria = findViewById(R.id.ivCerrajeria);
        ivLimpieza = findViewById(R.id.ivLimpieza);
        ivPintura = findViewById(R.id.ivPintura);
        ivFumigacion = findViewById(R.id.ivFumigacion);
        ivJardineria = findViewById(R.id.ivJardineria);
        tvCerrajeria = findViewById(R.id.tvCerrajeria);
        tvLimpieza = findViewById(R.id.tvLimpieza);
        tvPintura = findViewById(R.id.tvPintura);
        tvFumigacion = findViewById(R.id.tvFumigacion);
        tvJardineria = findViewById(R.id.tvJardineria);
        tvPregunta = findViewById(R.id.tvPregunta);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        verificarSiEsTrabajador();

        // Configuración de clics
        ivCerrajeria.setOnClickListener(v -> abrirCategoria(1));
        ivLimpieza.setOnClickListener(v -> abrirCategoria(2));
        ivPintura.setOnClickListener(v -> abrirCategoria(3));
        ivFumigacion.setOnClickListener(v -> abrirCategoria(4));
        ivJardineria.setOnClickListener(v -> abrirCategoria(5));

        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(Home.this, RegistroTrabajador.class);
            startActivity(intent);
        });
    }

    private void verificarSiEsTrabajador() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId != -1) {
            SupabaseClient.checkTrabajadorExiste(usuarioId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // En caso de error, preferimos no mostrar la invitación por precaución
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseData = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseData);
                            
                            // Si el array está vacío, significa que el usuario NO es trabajador
                            if (jsonArray.length() == 0) {
                                runOnUiThread(() -> {
                                    tvPregunta.setVisibility(View.VISIBLE);
                                    btnRegistrarse.setVisibility(View.VISIBLE);
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    private void abrirCategoria(int categoriaId) {
        Intent intent;
        switch (categoriaId) {
            case 1:
                intent = new Intent(this, CategoriaCerrajeria.class);
                break;
            case 2:
                intent = new Intent(this, CategoriaLimpieza.class);
                break;
            case 3:
                intent = new Intent(this, CategoriaPintura.class);
                break;
            case 4:
                intent = new Intent(this, CategoriaFumigacion.class);
                break;
            case 5:
                intent = new Intent(this, CategoriaJardineria.class);
                break;
            default:
                return;
        }
        startActivity(intent);
    }
}
