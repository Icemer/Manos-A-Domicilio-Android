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
                runOnUiThread(() -> Toast.makeText(RegistroTrabajador.this, "Error de red al cargar categorías", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Categoria> listaCategorias = DataParser.parseCategorias(responseData);
                        runOnUiThread(() -> {
                            if (listaCategorias.isEmpty()) {
                                Toast.makeText(RegistroTrabajador.this, "No hay categorías disponibles", Toast.LENGTH_SHORT).show();
                            } else {
                                ArrayAdapter<Categoria> adapter = new ArrayAdapter<>(RegistroTrabajador.this, android.R.layout.simple_spinner_dropdown_item, listaCategorias);
                                spinnerCategorias.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(RegistroTrabajador.this, "Error al procesar categorías", Toast.LENGTH_SHORT).show());
                    }
                }
            }
        });
    }

    private void registrarTrabajador() {
        Object selectedItem = spinnerCategorias.getSelectedItem();
        if (!(selectedItem instanceof Categoria)) {
            Toast.makeText(this, "Por favor, seleccione una categoría válida", Toast.LENGTH_SHORT).show();
            return;
        }

        Categoria categoriaSeleccionada = (Categoria) selectedItem;
        int categoriaId = categoriaSeleccionada.getId();
        String descripcion = etDescripcion.getText().toString().trim();
        boolean isAvailable = spinnerDisponibilidad.getSelectedItem().toString().equals("Disponible");

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Sesión no válida.", Toast.LENGTH_LONG).show();
            return;
        }

        btnRegistrarse.setEnabled(false);
        btnRegistrarse.setText("Registrando...");

        SupabaseClient.insertTrabajador(usuarioId, categoriaId, isAvailable, descripcion, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al conectar con el servidor");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistroTrabajador.this, "¡Registro completado!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    mostrarError("Error: " + response.code());
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
