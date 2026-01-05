package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.adapter.FavoritosAdapter;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Trabajador;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Favoritos extends BottomMenu {

    private RecyclerView rvFavoritos;
    private FavoritosAdapter adapter;
    private List<Trabajador> favoritosList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favoritos);
        setupBottomMenu();

        rvFavoritos = findViewById(R.id.rvFavoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));

        favoritosList = new ArrayList<>();
        adapter = new FavoritosAdapter(this, favoritosList);
        rvFavoritos.setAdapter(adapter);

        cargarFavoritos();
    }

    private void cargarFavoritos() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseClient.getFavoritos(usuarioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cargar los favoritos");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Trabajador> nuevosFavoritos = DataParser.parseFavoritos(responseData);

                        runOnUiThread(() -> {
                            favoritosList.clear();
                            favoritosList.addAll(nuevosFavoritos);
                            adapter.notifyDataSetChanged();
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarError("Error al procesar los datos");
                    }
                } else {
                    mostrarError("Error del servidor");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(Favoritos.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
