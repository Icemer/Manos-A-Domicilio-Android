package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetalleTrabajador extends BottomMenu {

    private TextView tvNombre, tvDescripcion, tvEstado, tvCategoria;
    private Button btnSolicitar;
    private ImageView ivFavorito;
    private int categoriaId = -1; // Inicializamos en -1
    private int trabajadorId;
    private int usuarioId;
    private boolean isFavorito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_trabajador);

        setupBottomMenu();

        tvNombre = findViewById(R.id.tvDetalleNombre);
        tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        tvEstado = findViewById(R.id.tvDetalleEstado);
        tvCategoria = findViewById(R.id.tvDetalleCategoria);
        btnSolicitar = findViewById(R.id.btnSolicitarServicio);
        ivFavorito = findViewById(R.id.ivFavorito);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        trabajadorId = intent.getIntExtra("id", -1);
        
        // Intentamos obtenerlo del intent, pero lo validaremos con la DB
        categoriaId = intent.getIntExtra("categoriaId", -1);

        tvNombre.setText(nombre);
        tvDescripcion.setText(descripcion);

        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId != -1 && trabajadorId != -1) {
            checkIfFavorito();
        }

        if (trabajadorId != -1) {
            cargarDatosTrabajador();
        }

        btnSolicitar.setOnClickListener(v -> {
            if (categoriaId == -1) {
                Toast.makeText(this, "Cargando información de categoría...", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(DetalleTrabajador.this, SolicitudServicio.class);
            i.putExtra("idTrabajador", trabajadorId);
            i.putExtra("categoriaId", categoriaId);
            startActivity(i);
        });

        ivFavorito.setOnClickListener(v -> toggleFavorito());
    }

    private void cargarDatosTrabajador() {
        // Obtenemos el trabajador con el nombre de su categoría y el ID real
        String url = "https://ipofxhlkuqrvqhcpnveu.supabase.co/rest/v1/trabajadores?id=eq." + trabajadorId + "&select=categoria_id,categorias(nombre)";
        
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII")
                .build();

        new okhttp3.OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        if (jsonArray.length() > 0) {
                            JSONObject obj = jsonArray.getJSONObject(0);
                            
                            // Actualizamos el categoriaId real desde la base de datos
                            categoriaId = obj.getInt("categoria_id");
                            
                            if (obj.has("categorias")) {
                                String nombreCat = obj.getJSONObject("categorias").getString("nombre");
                                runOnUiThread(() -> tvCategoria.setText(nombreCat));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void checkIfFavorito() {
        SupabaseClient.isFavorito(usuarioId, trabajadorId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        isFavorito = jsonArray.length() > 0;
                        updateFavoritoIcon();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void toggleFavorito() {
        if (isFavorito) {
            SupabaseClient.removeFavorito(usuarioId, trabajadorId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mostrarError("Error al quitar de favoritos");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        isFavorito = false;
                        updateFavoritoIcon();
                        runOnUiThread(() -> Toast.makeText(DetalleTrabajador.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } else {
            SupabaseClient.addFavorito(usuarioId, trabajadorId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mostrarError("Error al agregar a favoritos");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        isFavorito = true;
                        updateFavoritoIcon();
                        runOnUiThread(() -> Toast.makeText(DetalleTrabajador.this, "Agregado a favoritos", Toast.LENGTH_SHORT).show());
                    }
                }
            });
        }
    }

    private void updateFavoritoIcon() {
        runOnUiThread(() -> {
            if (isFavorito) {
                ivFavorito.setImageResource(R.drawable.ic_favorite);
            } else {
                ivFavorito.setImageResource(R.drawable.ic_favorite_border);
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(DetalleTrabajador.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
