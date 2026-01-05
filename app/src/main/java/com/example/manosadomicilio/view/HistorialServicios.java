package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.adapter.HistorialServiciosAdapter;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Servicio;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HistorialServicios extends BottomMenu {

    private RecyclerView rvHistorialServicios;
    private HistorialServiciosAdapter adapter;
    private List<Servicio> serviceList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial_servicios);
        setupBottomMenu();

        rvHistorialServicios = findViewById(R.id.rvHistorialServicios);
        rvHistorialServicios.setLayoutManager(new LinearLayoutManager(this));

        serviceList = new ArrayList<>();
        adapter = new HistorialServiciosAdapter(this, serviceList);
        rvHistorialServicios.setAdapter(adapter);

        cargarHistorialServicios();
    }

    private void cargarHistorialServicios() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseClient.getServiciosPorUsuario(usuarioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(HistorialServicios.this, "Error al cargar el historial", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Servicio> nuevosServicios = DataParser.parseServicios(responseData);

                        runOnUiThread(() -> {
                            serviceList.clear();
                            serviceList.addAll(nuevosServicios);
                            adapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(HistorialServicios.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(HistorialServicios.this, "Error del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
