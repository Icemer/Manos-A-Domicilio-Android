package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Usuario;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Perfil extends BottomMenu {

    private TextView tvNombrePerfil, tvEmailPerfil;
    private Button btnCerrarSesion, btnModoTrabajador, btnSoporte, btnEditarPerfilTrabajador;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        setupBottomMenu();

        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvEmailPerfil = findViewById(R.id.tvEmailPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnModoTrabajador = findViewById(R.id.btnModoTrabajador);
        btnSoporte = findViewById(R.id.btnSoporte);
        btnEditarPerfilTrabajador = findViewById(R.id.btnEditarPerfilTrabajador);

        cargarDatosUsuario();
        checkIfTrabajador();

        btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        btnModoTrabajador.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, DashboardTrabajador.class);
            startActivity(intent);
        });
        btnSoporte.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, Soporte.class);
            startActivity(intent);
        });
        btnEditarPerfilTrabajador.setOnClickListener(v -> {
            Intent intent = new Intent(Perfil.this, EditarPerfilTrabajador.class);
            startActivity(intent);
        });
    }

    private void cargarDatosUsuario() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);
        String nombre = prefs.getString("usuario_nombre", "Cargando...");
        String email = prefs.getString("usuario_email", "Cargando...");

        tvNombrePerfil.setText(nombre);
        tvEmailPerfil.setText(email);
    }

    private void checkIfTrabajador() {
        if (usuarioId != -1) {
            SupabaseClient.checkTrabajadorExiste(usuarioId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // No es necesario mostrar error
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseData = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseData);
                            if (jsonArray.length() > 0) {
                                runOnUiThread(() -> {
                                    btnModoTrabajador.setVisibility(View.VISIBLE);
                                    btnEditarPerfilTrabajador.setVisibility(View.VISIBLE);
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

    private void cerrarSesion() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(Perfil.this, InicioSesion.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
