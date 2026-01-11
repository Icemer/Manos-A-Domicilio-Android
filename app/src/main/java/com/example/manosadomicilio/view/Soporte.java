package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.SupabaseClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Soporte extends AppCompatActivity {

    private Spinner spinnerServicios;
    private EditText etDescripcionProblema;
    private Button btnEnviarTicket;
    private List<JSONObject> serviciosFinalizados;
    private int servicioIdSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soporte);

        spinnerServicios = findViewById(R.id.spinnerServiciosFinalizados);
        etDescripcionProblema = findViewById(R.id.etDescripcionProblema);
        btnEnviarTicket = findViewById(R.id.btnEnviarTicket);

        cargarServiciosFinalizados();

        btnEnviarTicket.setOnClickListener(v -> enviarTicket());
    }

    private void cargarServiciosFinalizados() {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        int usuarioId = prefs.getInt("usuario_id", -1);

        if (usuarioId != -1) {
            SupabaseClient.getServiciosFinalizadosPorUsuario(usuarioId, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    mostrarError("Error de conexión");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String responseData = response.body().string();
                            JSONArray jsonArray = new JSONArray(responseData);
                            serviciosFinalizados = new ArrayList<>();
                            List<String> descripciones = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                serviciosFinalizados.add(obj);
                                descripciones.add("Servicio #" + obj.getInt("id") + ": " + obj.optString("descripcion", "Sin descripción"));
                            }

                            actualizarSpinner(descripciones);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mostrarError("Error al leer servicios");
                        }
                    }
                }
            });
        }
    }

    private void actualizarSpinner(List<String> descripciones) {
        runOnUiThread(() -> {
            if (descripciones.isEmpty()) {
                descripciones.add("No tienes servicios finalizados");
                btnEnviarTicket.setEnabled(false);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, descripciones);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerServicios.setAdapter(adapter);
            
            spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (serviciosFinalizados != null && !serviciosFinalizados.isEmpty()) {
                        try {
                            servicioIdSeleccionado = serviciosFinalizados.get(position).getInt("id");
                        } catch (JSONException e) {
                            servicioIdSeleccionado = -1;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    servicioIdSeleccionado = -1;
                }
            });
        });
    }

    private void enviarTicket() {
        if (servicioIdSeleccionado == -1) {
            Toast.makeText(this, "Selecciona un servicio válido", Toast.LENGTH_SHORT).show();
            return;
        }

        String descripcion = etDescripcionProblema.getText().toString().trim();
        if (descripcion.isEmpty()) {
            Toast.makeText(this, "Describe el problema para poder ayudarte", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEnviarTicket.setEnabled(false);
        SupabaseClient.insertarTicket(servicioIdSeleccionado, descripcion, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al enviar el reporte");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(Soporte.this, "¡Reporte enviado con éxito!", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    mostrarError("Error del servidor");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> {
            btnEnviarTicket.setEnabled(true);
            Toast.makeText(Soporte.this, mensaje, Toast.LENGTH_SHORT).show();
        });
    }
}
