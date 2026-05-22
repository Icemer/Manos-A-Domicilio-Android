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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetalleTrabajador extends BottomMenu {

    private TextView tvNombre, tvDescripcion, tvEstado, tvCategoria, tvRating, tvRegistro;
    private Button btnSolicitar;
    private ImageView ivFavorito;
    private int categoriaId = -1; 
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
        tvRating = findViewById(R.id.tvDetalleRating);
        tvRegistro = findViewById(R.id.tvDetalleRegistro);
        btnSolicitar = findViewById(R.id.btnSolicitarServicio);
        ivFavorito = findViewById(R.id.ivFavorito);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        trabajadorId = intent.getIntExtra("id", -1);
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
            cargarPromedioCalificacion();
        }

        btnSolicitar.setOnClickListener(v -> {
            if (categoriaId == -1) {
                Toast.makeText(this, "Cargando información...", Toast.LENGTH_SHORT).show();
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
        // Añadimos created_at a la consulta
        String url = "https://ipofxhlkuqrvqhcpnveu.supabase.co/rest/v1/trabajadores?id=eq." + trabajadorId + "&select=categoria_id,created_at,categorias(nombre)";
        
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII")
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
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
                            categoriaId = obj.getInt("categoria_id");
                            
                            String createdAt = obj.optString("created_at", "");
                            if (!createdAt.isEmpty()) {
                                String fechaFormateada = formatearFecha(createdAt);
                                runOnUiThread(() -> tvRegistro.setText("Registrado desde: " + fechaFormateada));
                            }

                            if (obj.has("categorias")) {
                                String nombreCat = obj.getJSONObject("categorias").getString("nombre");
                                runOnUiThread(() -> tvCategoria.setText(nombreCat));
                            }
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
    }

    private String formatearFecha(String fechaIso) {
        try {
            // Supabase devuelve formato ISO 8601: 2024-05-20T15:30:00+00:00
            SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = sdfEntrada.parse(fechaIso);
            SimpleDateFormat sdfSalida = new SimpleDateFormat("dd 'de' MMMM 'de' yyyy", new Locale("es", "ES"));
            return sdfSalida.format(date);
        } catch (ParseException e) {
            return fechaIso;
        }
    }

    private void cargarPromedioCalificacion() {
        String url = "https://ipofxhlkuqrvqhcpnveu.supabase.co/rest/v1/servicios?trabajador_id=eq." + trabajadorId + "&select=calificacion_trabajador&calificacion_trabajador=not.is.null";
        
        okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .addHeader("apikey", "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII")
                .addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imlwb2Z4aGxrdXFydnFoY3BudmV1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQzNjY5NDAsImV4cCI6MjA3OTk0Mjk0MH0.UbLE9bg6Zq3L45FOW4lLLGYdCJQ8FJXn9d6Y5TCsrII")
                .build();

        SupabaseClient.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {}

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        JSONArray jsonArray = new JSONArray(responseData);
                        double suma = 0;
                        int contador = jsonArray.length();
                        
                        if (contador > 0) {
                            for (int i = 0; i < contador; i++) {
                                suma += jsonArray.getJSONObject(i).getDouble("calificacion_trabajador");
                            }
                            double promedio = suma / contador;
                            runOnUiThread(() -> tvRating.setText(String.format(Locale.getDefault(), "%.1f", promedio)));
                        } else {
                            runOnUiThread(() -> tvRating.setText("S/C")); 
                        }
                    } catch (JSONException e) { e.printStackTrace(); }
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
                    } catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void toggleFavorito() {
        if (usuarioId == -1) {
            Toast.makeText(this, "Inicia sesión para usar favoritos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isFavorito) {
            SupabaseClient.removeFavorito(usuarioId, trabajadorId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mostrarError("Error de conexión al quitar favorito");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        isFavorito = false;
                        updateFavoritoIcon();
                        runOnUiThread(() -> Toast.makeText(DetalleTrabajador.this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show());
                    } else {
                        mostrarError("No se pudo quitar de favoritos (código " + response.code() + ")");
                    }
                }
            });
        } else {
            SupabaseClient.addFavorito(usuarioId, trabajadorId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mostrarError("Error de conexión al agregar favorito");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        isFavorito = true;
                        updateFavoritoIcon();
                        runOnUiThread(() -> Toast.makeText(DetalleTrabajador.this, "Agregado a favoritos ❤️", Toast.LENGTH_SHORT).show());
                    } else {
                        mostrarError("No se pudo agregar a favoritos (código " + response.code() + ")");
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
