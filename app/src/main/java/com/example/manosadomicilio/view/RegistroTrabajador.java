package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Categoria;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegistroTrabajador extends BottomMenu {

    private Spinner spinnerCategorias, spinnerDisponibilidad;
    private EditText etDescripcion;
    private Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_trabajador);

        spinnerCategorias = findViewById(R.id.spinnerCategorias);
        spinnerDisponibilidad = findViewById(R.id.spinnerDisponibilidad);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);

        setupBottomMenu();
        cargarDisponibilidad();
        cargarCategorias();

        btnRegistrarse.setOnClickListener(v -> registrarTrabajador());
    }

    private void cargarDisponibilidad() {
        String[] opciones = {"Disponible", "No Disponible"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, opciones);
        spinnerDisponibilidad.setAdapter(adapter);
    }

    private void cargarCategorias() {
        SupabaseClient.getCategorias(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(RegistroTrabajador.this, "Error al cargar categorías", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Categoria> listaCategorias = DataParser.parseCategorias(responseData);
                        runOnUiThread(() -> mostrarCategoriasEnSpinner(listaCategorias));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void mostrarCategoriasEnSpinner(List<Categoria> categorias) {
        ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categorias);
        spinnerCategorias.setAdapter(adapter);
    }

    private void registrarTrabajador() {
        Categoria categoriaSeleccionada = (Categoria) spinnerCategorias.getSelectedItem();
        if (categoriaSeleccionada == null) {
            Toast.makeText(this, "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        int categoriaId = categoriaSeleccionada.getId();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean isAvailable = spinnerDisponibilidad.getSelectedItem().toString().equals("Disponible");

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado.", Toast.LENGTH_LONG).show();
            return;
        }

        btnRegistrarse.setEnabled(false);
        btnRegistrarse.setText("Verificando...");

        SupabaseClient.checkTrabajadorExiste(usuarioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error de conexión al verificar");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            mostrarError("¡Ya estás registrado como trabajador!");
                        } else {
                            performRegistration(usuarioId, categoriaId, isAvailable, descripcion);
                        }
                    } catch (JSONException e) {
                        mostrarError("Error procesando verificación");
                    }
                } else {
                    mostrarError("Error del servidor al verificar");
                }
            }
        });
    }

    private void performRegistration(int usuarioId, int categoriaId, boolean isAvailable, String descripcion) {
        runOnUiThread(() -> btnRegistrarse.setText("Registrando..."));
        SupabaseClient.insertTrabajador(usuarioId, categoriaId, isAvailable, descripcion, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error de conexión al registrar");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistroTrabajador.this, "¡Trabajador registrado con éxito!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    mostrarError("Error al registrar.");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> {
            btnRegistrarse.setEnabled(true);
            btnRegistrarse.setText("Registrarse");
            Toast.makeText(RegistroTrabajador.this, mensaje, Toast.LENGTH_LONG).show();
        });
    }
}
