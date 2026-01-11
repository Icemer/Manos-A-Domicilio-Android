package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.adapter.ServiciosAsignadosAdapter;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Servicio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DashboardTrabajador extends BottomMenu {

    private RecyclerView rvServiciosAsignados;
    private ServiciosAsignadosAdapter adapter;
    private List<Servicio> servicioList;
    private int usuarioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_trabajador);
        setupBottomMenu();

        rvServiciosAsignados = findViewById(R.id.rvServiciosAsignados);
        rvServiciosAsignados.setLayoutManager(new LinearLayoutManager(this));

        servicioList = new ArrayList<>();
        adapter = new ServiciosAsignadosAdapter(this, servicioList);
        rvServiciosAsignados.setAdapter(adapter);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId != -1) {
            getTrabajadorIdYServicios();
        }
    }

    private void getTrabajadorIdYServicios() {
        SupabaseClient.checkTrabajadorExiste(usuarioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al verificar el trabajador");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject trabajadorObj = jsonArray.getJSONObject(0);
                            int trabajadorId = trabajadorObj.getInt("id");
                            cargarServiciosAsignados(trabajadorId);
                        } else {
                            mostrarError("No se encontró el perfil de trabajador");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarError("Error al procesar datos del trabajador");
                    }
                } else {
                    mostrarError("Error del servidor al verificar trabajador");
                }
            }
        });
    }

    private void cargarServiciosAsignados(int trabajadorId) {
        SupabaseClient.getServiciosPorTrabajador(trabajadorId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cargar los servicios");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Servicio> nuevosServicios = DataParser.parseServicios(responseData);

                        runOnUiThread(() -> {
                            servicioList.clear();
                            servicioList.addAll(nuevosServicios);
                            adapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarError("Error al procesar los servicios");
                    }
                } else {
                    mostrarError("Error del servidor al cargar servicios");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(DashboardTrabajador.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
