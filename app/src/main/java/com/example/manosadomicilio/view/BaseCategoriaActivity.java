package com.example.manosadomicilio.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
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

public abstract class BaseCategoriaActivity extends BottomMenu {

    private LinearLayout contenedorTrabajadores;

    // Listas para filtrado local sin llamadas extra al servidor
    private final List<Trabajador> todasLasTrabajadores = new ArrayList<>();
    private final List<View> todasLasVistas = new ArrayList<>();

    protected abstract int getCategoriaId();
    protected abstract int getLayoutId();
    protected abstract String getNoTrabajadoresMessage();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        setupBottomMenu();
        contenedorTrabajadores = findViewById(R.id.contenedorTrabajadores);

        // Conectar barra de búsqueda funcional
        EditText etBuscar = findViewById(R.id.etBuscar);
        if (etBuscar != null) {
            etBuscar.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    filtrarTrabajadores(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });
        }

        cargarTrabajadores();
    }

    /** Filtra los trabajadores ya cargados según el texto ingresado — sin red. */
    private void filtrarTrabajadores(String query) {
        String q = query.toLowerCase().trim();
        for (int i = 0; i < todasLasTrabajadores.size(); i++) {
            Trabajador t = todasLasTrabajadores.get(i);
            View v = todasLasVistas.get(i);

            String nombre = t.getNombreUsuario() != null ? t.getNombreUsuario().toLowerCase() : "";
            String desc   = t.getDescripcion()    != null ? t.getDescripcion().toLowerCase()    : "";

            boolean match = q.isEmpty() || nombre.contains(q) || desc.contains(q);
            v.setVisibility(match ? View.VISIBLE : View.GONE);
        }
    }

    private void cargarTrabajadores() {
        SupabaseClient.getTrabajadoresPorCategoria(getCategoriaId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(BaseCategoriaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Trabajador> listaTrabajadores = DataParser.parseTrabajadores(responseData);
                        runOnUiThread(() -> mostrarTrabajadoresEnUI(listaTrabajadores));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(BaseCategoriaActivity.this, "Error procesando los datos", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(BaseCategoriaActivity.this, "Error Servidor: " + response.code(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }

    private void mostrarTrabajadoresEnUI(List<Trabajador> trabajadores) {
        contenedorTrabajadores.removeAllViews();
        todasLasTrabajadores.clear();
        todasLasVistas.clear();

        if (trabajadores.isEmpty()) {
            Toast.makeText(this, getNoTrabajadoresMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        for (Trabajador trabajador : trabajadores) {
            View view = inflater.inflate(R.layout.item_trabajador, contenedorTrabajadores, false);
            TextView tvNombre = view.findViewById(R.id.tvNombreItem);
            TextView tvDescripcion = view.findViewById(R.id.tvDescripcionItem);

            tvNombre.setText(trabajador.getNombreUsuario());
            tvDescripcion.setText(trabajador.getDescripcion());

            view.setOnClickListener(v -> {
                Intent intent = new Intent(BaseCategoriaActivity.this, DetalleTrabajador.class);
                intent.putExtra("id", trabajador.getId());
                intent.putExtra("nombre", trabajador.getNombreUsuario());
                intent.putExtra("descripcion", trabajador.getDescripcion());
                intent.putExtra("categoriaId", trabajador.getCategoriaId());
                startActivity(intent);
            });

            todasLasTrabajadores.add(trabajador);
            todasLasVistas.add(view);
            contenedorTrabajadores.addView(view);
        }
    }
}
