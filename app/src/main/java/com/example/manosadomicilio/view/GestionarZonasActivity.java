package com.example.manosadomicilio.view;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.adapter.ZonasAdapter;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Zona;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GestionarZonasActivity extends AppCompatActivity {

    private RecyclerView rvZonas;
    private Button btnGuardarZonas;
    private ZonasAdapter adapter;
    private List<Zona> todasLasZonas;
    private int trabajadorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_zonas);

        rvZonas = findViewById(R.id.rvZonas);
        btnGuardarZonas = findViewById(R.id.btnGuardarZonas);
        rvZonas.setLayoutManager(new LinearLayoutManager(this));

        trabajadorId = getIntent().getIntExtra("trabajadorId", -1);

        cargarZonas();

        btnGuardarZonas.setOnClickListener(v -> eliminarEInsertarZonas());
    }

    private void cargarZonas() {
        SupabaseClient.getZonas(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cargar zonas");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        todasLasZonas = DataParser.parseZonas(responseData);
                        marcarZonasDelTrabajador();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarError("Error al procesar zonas");
                    }
                } else {
                    mostrarError("Error del servidor al cargar zonas");
                }
            }
        });
    }

    private void marcarZonasDelTrabajador() {
        SupabaseClient.getZonasTrabajador(trabajadorId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarZonasEnUI();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        List<Integer> idsZonasTrabajador = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            idsZonasTrabajador.add(jsonArray.getJSONObject(i).getInt("zona_id"));
                        }

                        for (Zona zona : todasLasZonas) {
                            if (idsZonasTrabajador.contains(zona.getId())) {
                                zona.setSelected(true);
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                mostrarZonasEnUI();
            }
        });
    }

    private void mostrarZonasEnUI() {
        runOnUiThread(() -> {
            adapter = new ZonasAdapter(todasLasZonas);
            rvZonas.setAdapter(adapter);
        });
    }

    private void eliminarEInsertarZonas() {
        // 1. Borrar todas las zonas actuales del trabajador
        SupabaseClient.deleteZonasTrabajador(trabajadorId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al limpiar zonas anteriores");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    // 2. Si se borraron con éxito, insertar las nuevas seleccionadas
                    guardarNuevasZonas();
                } else {
                    mostrarError("Error al actualizar zonas");
                }
            }
        });
    }

    private void guardarNuevasZonas() {
        List<Integer> idsSeleccionadas = adapter.getZonaList().stream()
                .filter(Zona::isSelected)
                .map(Zona::getId)
                .collect(Collectors.toList());

        if (idsSeleccionadas.isEmpty()) {
            runOnUiThread(() -> {
                Toast.makeText(GestionarZonasActivity.this, "Zonas actualizadas (ninguna seleccionada)", Toast.LENGTH_SHORT).show();
                finish();
            });
            return;
        }

        SupabaseClient.setZonasTrabajador(trabajadorId, idsSeleccionadas, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al guardar las nuevas zonas");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(GestionarZonasActivity.this, "Zonas actualizadas con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    mostrarError("Error al guardar las nuevas zonas");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(GestionarZonasActivity.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
