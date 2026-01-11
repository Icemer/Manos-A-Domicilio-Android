package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Trabajador;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class EditarPerfilTrabajador extends BottomMenu {

    private EditText etEditarDescripcion;
    private Switch swDisponibilidad;
    private Button btnGuardarPerfil;
    private TextView tvGestionarZonas;
    private int usuarioId;
    private int trabajadorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil_trabajador);
        setupBottomMenu();

        etEditarDescripcion = findViewById(R.id.etEditarDescripcion);
        swDisponibilidad = findViewById(R.id.swDisponibilidad);
        btnGuardarPerfil = findViewById(R.id.btnGuardarPerfil);
        tvGestionarZonas = findViewById(R.id.tvGestionarZonas);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId != -1) {
            cargarDatosTrabajador();
        }

        btnGuardarPerfil.setOnClickListener(v -> guardarCambios());
        tvGestionarZonas.setOnClickListener(v -> {
            Intent intent = new Intent(EditarPerfilTrabajador.this, GestionarZonasActivity.class);
            intent.putExtra("trabajadorId", trabajadorId);
            startActivity(intent);
        });
    }

    private void cargarDatosTrabajador() {
        SupabaseClient.getTrabajadorPorUsuarioId(usuarioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cargar los datos del trabajador");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject trabajadorObj = jsonArray.getJSONObject(0);
                            trabajadorId = trabajadorObj.getInt("id");
                            String descripcion = trabajadorObj.getString("descripcion");
                            boolean disponibilidad = trabajadorObj.getBoolean("disponibilidad");

                            runOnUiThread(() -> {
                                etEditarDescripcion.setText(descripcion);
                                swDisponibilidad.setChecked(disponibilidad);
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarError("Error al procesar los datos del trabajador");
                    }
                } else {
                    mostrarError("Error del servidor al cargar los datos");
                }
            }
        });
    }

    private void guardarCambios() {
        String nuevaDescripcion = etEditarDescripcion.getText().toString();
        boolean nuevaDisponibilidad = swDisponibilidad.isChecked();

        SupabaseClient.updateTrabajador(trabajadorId, nuevaDescripcion, nuevaDisponibilidad, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al guardar los cambios");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(EditarPerfilTrabajador.this, "Perfil actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    mostrarError("Error al guardar los cambios");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(EditarPerfilTrabajador.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
