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
import androidx.appcompat.app.AppCompatActivity;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetalleTrabajador extends BottomMenu {

    private TextView tvNombre, tvDescripcion, tvEstado;
    private Button btnSolicitar;
    private ImageView ivFavorito;
    private int categoriaId;
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

        btnSolicitar.setOnClickListener(v -> {
            Intent i = new Intent(DetalleTrabajador.this, SolicitudServicio.class);
            i.putExtra("idTrabajador", trabajadorId);
            i.putExtra("categoriaId", categoriaId);
            startActivity(i);
        });

        ivFavorito.setOnClickListener(v -> toggleFavorito());
    }

    private void checkIfFavorito() {
        SupabaseClient.isFavorito(usuarioId, trabajadorId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                // No es necesario mostrar un error, simplemente no se marcará como favorito
            }

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
