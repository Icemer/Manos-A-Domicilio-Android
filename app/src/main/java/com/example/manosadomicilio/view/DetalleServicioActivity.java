package com.example.manosadomicilio.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.BottomMenu;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Servicio;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetalleServicioActivity extends BottomMenu {

    private TextView tvDetalleNombreTrabajador, tvDetalleFecha, tvDetalleEstado, tvDetalleDescripcion;
    private Button btnCancelarServicio, btnMarcarComoPagado, btnEnviarCalificacion;
    private EditText etComentarios;
    private RatingBar rbCalificacion;
    private int servicioId;
    private Servicio servicioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_servicio);
        setupBottomMenu(); // Añadir el menú inferior

        tvDetalleNombreTrabajador = findViewById(R.id.tvDetalleNombreTrabajador);
        tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
        tvDetalleEstado = findViewById(R.id.tvDetalleEstado);
        tvDetalleDescripcion = findViewById(R.id.tvDetalleDescripcion);
        btnCancelarServicio = findViewById(R.id.btnCancelarServicio);
        btnMarcarComoPagado = findViewById(R.id.btnMarcarComoPagado);
        etComentarios = findViewById(R.id.etComentarios);
        rbCalificacion = findViewById(R.id.rbCalificacion);
        btnEnviarCalificacion = findViewById(R.id.btnEnviarCalificacion);

        servicioId = getIntent().getIntExtra("servicioId", -1);
        if (servicioId != -1) {
            cargarDetallesServicio(servicioId);
        }

        btnCancelarServicio.setOnClickListener(v -> cancelarServicio());
        btnMarcarComoPagado.setOnClickListener(v -> marcarComoPagado());
        btnEnviarCalificacion.setOnClickListener(v -> enviarCalificacion());

        tvDetalleNombreTrabajador.setOnClickListener(v -> irAPerfilTrabajador());
    }

    private void cargarDetallesServicio(int servicioId) {
        SupabaseClient.getServicioPorId(servicioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cargar los detalles del servicio");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Servicio> servicios = DataParser.parseServicios(responseData);
                        if (!servicios.isEmpty()) {
                            servicioActual = servicios.get(0);
                            mostrarDetalles(servicioActual);
                        } else {
                            mostrarError("No se encontró el servicio");
                        }
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

    private void mostrarDetalles(Servicio servicio) {
        runOnUiThread(() -> {
            tvDetalleNombreTrabajador.setText(servicio.getNombreTrabajador());
            tvDetalleFecha.setText("Fecha: " + servicio.getFecha());
            tvDetalleEstado.setText("Estado: " + servicio.getEstado());
            tvDetalleDescripcion.setText(servicio.getDescripcion());

            String estado = servicio.getEstado();
            if ("pendiente".equalsIgnoreCase(estado) || "aceptado".equalsIgnoreCase(estado)) {
                btnCancelarServicio.setVisibility(View.VISIBLE);
            }
            if ("finalizado".equalsIgnoreCase(estado)) {
                btnMarcarComoPagado.setVisibility(View.VISIBLE);
                etComentarios.setVisibility(View.VISIBLE);
                rbCalificacion.setVisibility(View.VISIBLE);
                btnEnviarCalificacion.setVisibility(View.VISIBLE);
            }
        });
    }

    private void cancelarServicio() {
        SupabaseClient.updateServicioEstado(servicioId, "cancelado", new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cancelar el servicio");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleServicioActivity.this, "Servicio cancelado", Toast.LENGTH_SHORT).show();
                        finish(); // Regresar a la pantalla anterior
                    });
                } else {
                    mostrarError("Error al cancelar el servicio");
                }
            }
        });
    }

    private void marcarComoPagado() {
        Toast.makeText(this, "Servicio marcado como pagado", Toast.LENGTH_SHORT).show();
    }

    private void enviarCalificacion() {
        float calificacion = rbCalificacion.getRating();
        String comentarios = etComentarios.getText().toString();

        SupabaseClient.updateServicioCalificacion(servicioId, (int) calificacion, comentarios, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al enviar la calificación");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleServicioActivity.this, "Calificación enviada", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                } else {
                    mostrarError("Error al enviar la calificación");
                }
            }
        });
    }

    private void irAPerfilTrabajador() {
        if (servicioActual != null) {
            Intent intent = new Intent(this, DetalleTrabajador.class);
            intent.putExtra("id", servicioActual.getTrabajadorId());
            intent.putExtra("nombre", servicioActual.getNombreTrabajador());
            // TODO: Se necesita la descripción y la categoría del trabajador. Esto se debe agregar al modelo y a la consulta.
            // intent.putExtra("descripcion", servicioActual.getDescripcionTrabajador());
            // intent.putExtra("categoriaId", servicioActual.getCategoriaId());
            startActivity(intent);
        } else {
            mostrarError("No se pueden cargar los detalles del trabajador");
        }
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(DetalleServicioActivity.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
