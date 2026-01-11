package com.example.manosadomicilio.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.math.BigDecimal;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class DetalleServicioAsignado extends BottomMenu {

    private TextView tvDetalleClienteNombre, tvDetalleDireccion, tvDetalleFechaHora, tvDetalleDescripcionServicio, tvCalificacionCliente, tvCalificacionDadaAlCliente;
    private Button btnAceptar, btnRechazar, btnMarcarFinalizado, btnEnviarCalificacionCliente;
    private EditText etPrecioFinal, etComentariosTrabajador;
    private LinearLayout llCalificarCliente, llResumenCalificacionesTrabajador;
    private RatingBar rbCalificarCliente;
    private int servicioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detalle_servicio_asignado);
        setupBottomMenu();

        tvDetalleClienteNombre = findViewById(R.id.tvDetalleClienteNombre);
        tvDetalleDireccion = findViewById(R.id.tvDetalleDireccion);
        tvDetalleFechaHora = findViewById(R.id.tvDetalleFechaHora);
        tvDetalleDescripcionServicio = findViewById(R.id.tvDetalleDescripcionServicio);
        
        tvCalificacionCliente = findViewById(R.id.tvCalificacionCliente); // Calificacion recibida
        tvCalificacionDadaAlCliente = findViewById(R.id.tvCalificacionDadaAlCliente); // Calificacion enviada
        
        btnAceptar = findViewById(R.id.btnAceptarServicio);
        btnRechazar = findViewById(R.id.btnRechazarServicio);
        btnMarcarFinalizado = findViewById(R.id.btnMarcarFinalizado);
        etPrecioFinal = findViewById(R.id.etPrecioFinal);

        llCalificarCliente = findViewById(R.id.llCalificarCliente);
        rbCalificarCliente = findViewById(R.id.rbCalificarCliente);
        etComentariosTrabajador = findViewById(R.id.etComentariosTrabajador);
        btnEnviarCalificacionCliente = findViewById(R.id.btnEnviarCalificacionCliente);
        
        llResumenCalificacionesTrabajador = findViewById(R.id.llResumenCalificacionesTrabajador);

        servicioId = getIntent().getIntExtra("servicioId", -1);
        if (servicioId != -1) {
            cargarDetallesServicioAsignado(servicioId);
        }

        btnAceptar.setOnClickListener(v -> updateEstadoServicio("aceptado"));
        btnRechazar.setOnClickListener(v -> updateEstadoServicio("rechazado"));
        btnMarcarFinalizado.setOnClickListener(v -> marcarComoFinalizado());
        btnEnviarCalificacionCliente.setOnClickListener(v -> enviarCalificacionAlCliente());
    }

    private void cargarDetallesServicioAsignado(int servicioId) {
        SupabaseClient.getServicioPorId(servicioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al cargar los detalles");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Servicio> servicios = DataParser.parseServicios(responseData);
                        if (!servicios.isEmpty()) {
                            mostrarDetalles(servicios.get(0));
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
            tvDetalleClienteNombre.setText(servicio.getNombreCliente());
            tvDetalleDireccion.setText("Dirección: " + servicio.getDireccionCompleta()); 
            tvDetalleFechaHora.setText("Fecha y Hora: " + servicio.getFecha() + " " + servicio.getHoraInicial());
            tvDetalleDescripcionServicio.setText("Descripción: " + servicio.getDescripcion());

            String estado = servicio.getEstado();
            
            // Lógica de visibilidad de botones
            btnAceptar.setVisibility("pendiente".equalsIgnoreCase(estado) ? View.VISIBLE : View.GONE);
            btnRechazar.setVisibility("pendiente".equalsIgnoreCase(estado) ? View.VISIBLE : View.GONE);

            if ("aceptado".equalsIgnoreCase(estado)) {
                btnMarcarFinalizado.setVisibility(View.VISIBLE);
                etPrecioFinal.setVisibility(View.VISIBLE);
            } else {
                btnMarcarFinalizado.setVisibility(View.GONE);
                etPrecioFinal.setVisibility(View.GONE);
            }

            // Lógica de calificación del trabajador al cliente
            if ("finalizado".equalsIgnoreCase(estado) && servicio.isPagoRealizado()) {
                if (servicio.getCalificacionCliente() == null) {
                    // El trabajador aún no califica al cliente
                    llCalificarCliente.setVisibility(View.VISIBLE);
                    llResumenCalificacionesTrabajador.setVisibility(View.GONE);
                } else {
                    // Ya se calificó, mostrar resumen
                    llCalificarCliente.setVisibility(View.GONE);
                    llResumenCalificacionesTrabajador.setVisibility(View.VISIBLE);
                    
                    tvCalificacionDadaAlCliente.setText("Calificación que diste al cliente: " + servicio.getCalificacionCliente() + " estrellas");
                    
                    if (servicio.getCalificacionTrabajador() != null) {
                        tvCalificacionCliente.setText("Calificación recibida del cliente: " + servicio.getCalificacionTrabajador() + " estrellas");
                        tvCalificacionCliente.setVisibility(View.VISIBLE);
                    } else {
                        tvCalificacionCliente.setText("El cliente aún no te ha calificado.");
                        tvCalificacionCliente.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                llCalificarCliente.setVisibility(View.GONE);
                llResumenCalificacionesTrabajador.setVisibility(View.GONE);
            }
        });
    }

    private void enviarCalificacionAlCliente() {
        int calificacion = (int) rbCalificarCliente.getRating();
        String comentarios = etComentariosTrabajador.getText().toString().trim();

        if (calificacion == 0) {
            Toast.makeText(this, "Por favor, selecciona una calificación", Toast.LENGTH_SHORT).show();
            return;
        }

        SupabaseClient.updateCalificacionCliente(servicioId, calificacion, comentarios, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al enviar calificación");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleServicioAsignado.this, "¡Calificación enviada!", Toast.LENGTH_SHORT).show();
                        cargarDetallesServicioAsignado(servicioId); // Recargar para mostrar resumen
                    });
                } else {
                    mostrarError("Error al procesar la calificación");
                }
            }
        });
    }

    private void updateEstadoServicio(String nuevoEstado) {
        SupabaseClient.updateServicioEstado(servicioId, nuevoEstado, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al actualizar el estado");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleServicioAsignado.this, "Servicio " + nuevoEstado, Toast.LENGTH_SHORT).show();
                        cargarDetallesServicioAsignado(servicioId);
                    });
                } else {
                    mostrarError("Error al actualizar el estado");
                }
            }
        });
    }

    private void marcarComoFinalizado() {
        String precioStr = etPrecioFinal.getText().toString();
        if (precioStr.isEmpty()) {
            mostrarError("Por favor, ingrese un precio final");
            return;
        }

        BigDecimal precioFinal = new BigDecimal(precioStr);

        SupabaseClient.updateServicioFinalizado(servicioId, precioFinal, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al finalizar el servicio");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleServicioAsignado.this, "Servicio finalizado. Esperando pago del cliente.", Toast.LENGTH_LONG).show();
                        cargarDetallesServicioAsignado(servicioId);
                    });
                } else {
                    mostrarError("Error al finalizar el servicio");
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(DetalleServicioAsignado.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
