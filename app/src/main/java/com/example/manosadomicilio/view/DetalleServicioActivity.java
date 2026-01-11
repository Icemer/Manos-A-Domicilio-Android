package com.example.manosadomicilio.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

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

    private TextView tvDetalleNombreTrabajador, tvDetalleFecha, tvDetalleEstado, tvMontoAPagar, tvDetalleDescripcion;
    private TextView tvCalificacionDada, tvCalificacionRecibida;
    private Button btnCancelarServicio, btnPagar, btnEnviarCalificacion;
    private EditText etComentarios;
    private RatingBar rbCalificacion;
    private LinearLayout llFormularioCalificacion, llResumenCalificaciones;
    private int servicioId;
    private Servicio servicioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_servicio);
        setupBottomMenu();

        tvDetalleNombreTrabajador = findViewById(R.id.tvDetalleNombreTrabajador);
        tvDetalleFecha = findViewById(R.id.tvDetalleFecha);
        tvDetalleEstado = findViewById(R.id.tvDetalleEstado);
        tvMontoAPagar = findViewById(R.id.tvMontoAPagar);
        tvDetalleDescripcion = findViewById(R.id.tvDetalleDescripcion);
        
        tvCalificacionDada = findViewById(R.id.tvCalificacionDada);
        tvCalificacionRecibida = findViewById(R.id.tvCalificacionRecibida);
        
        btnCancelarServicio = findViewById(R.id.btnCancelarServicio);
        btnPagar = findViewById(R.id.btnPagar);
        
        llFormularioCalificacion = findViewById(R.id.llFormularioCalificacion);
        etComentarios = findViewById(R.id.etComentarios);
        rbCalificacion = findViewById(R.id.rbCalificacion);
        btnEnviarCalificacion = findViewById(R.id.btnEnviarCalificacion);
        
        llResumenCalificaciones = findViewById(R.id.llResumenCalificaciones);

        servicioId = getIntent().getIntExtra("servicioId", -1);
        if (servicioId != -1) {
            cargarDetallesServicio(servicioId);
        }

        btnCancelarServicio.setOnClickListener(v -> cancelarServicio());
        btnPagar.setOnClickListener(v -> mostrarDialogoMetodoPago());
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
            
            // Lógica de visibilidad de botones
            btnCancelarServicio.setVisibility(("pendiente".equalsIgnoreCase(estado) || "aceptado".equalsIgnoreCase(estado)) ? View.VISIBLE : View.GONE);
            
            if ("finalizado".equalsIgnoreCase(estado) && !servicio.isPagoRealizado()) {
                tvMontoAPagar.setVisibility(View.VISIBLE);
                tvMontoAPagar.setText("Monto a Pagar: $" + servicio.getPrecio());
                btnPagar.setVisibility(View.VISIBLE);
            } else {
                tvMontoAPagar.setVisibility(View.GONE);
                btnPagar.setVisibility(View.GONE);
            }

            // Lógica de calificación (Solo si está finalizado y pagado)
            if ("finalizado".equalsIgnoreCase(estado) && servicio.isPagoRealizado()) {
                if (servicio.getCalificacionTrabajador() == null) {
                    // Cliente aún no califica al trabajador
                    llFormularioCalificacion.setVisibility(View.VISIBLE);
                    llResumenCalificaciones.setVisibility(View.GONE);
                } else {
                    // Ya se calificó, mostrar resumen
                    llFormularioCalificacion.setVisibility(View.GONE);
                    llResumenCalificaciones.setVisibility(View.VISIBLE);
                    
                    tvCalificacionDada.setText("Tu calificación al trabajador: " + servicio.getCalificacionTrabajador() + " estrellas");
                    
                    if (servicio.getCalificacionCliente() != null) {
                        tvCalificacionRecibida.setText("Calificación del trabajador hacia ti: " + servicio.getCalificacionCliente() + " estrellas");
                        tvCalificacionRecibida.setVisibility(View.VISIBLE);
                    } else {
                        tvCalificacionRecibida.setText("El trabajador aún no te ha calificado.");
                        tvCalificacionRecibida.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                llFormularioCalificacion.setVisibility(View.GONE);
                llResumenCalificaciones.setVisibility(View.GONE);
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
                        finish();
                    });
                } else {
                    mostrarError("Error al cancelar el servicio");
                }
            }
        });
    }

    private void mostrarDialogoMetodoPago() {
        final String[] metodos = {"Efectivo", "Transferencia"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seleccione un método de pago");
        builder.setItems(metodos, (dialog, which) -> {
            String metodoSeleccionado = metodos[which];
            realizarPago(metodoSeleccionado);
        });
        builder.show();
    }

    private void realizarPago(String metodoPago) {
        SupabaseClient.insertarPago(servicioId, servicioActual.getPrecio(), metodoPago, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error al procesar el pago");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(DetalleServicioActivity.this, "Pago realizado con éxito", Toast.LENGTH_SHORT).show();
                        cargarDetallesServicio(servicioId); // Recargar para mostrar calificacion
                    });
                } else {
                    mostrarError("Error al procesar el pago");
                }
            }
        });
    }

    private void enviarCalificacion() {
        float calificacion = rbCalificacion.getRating();
        String comentarios = etComentarios.getText().toString();

        if (calificacion == 0) {
            Toast.makeText(this, "Por favor, seleccione una calificación", Toast.LENGTH_SHORT).show();
            return;
        }

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
                        cargarDetallesServicio(servicioId); // Recargar para mostrar resumen
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
            startActivity(intent);
        } else {
            mostrarError("No se pueden cargar los detalles del trabajador");
        }
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> Toast.makeText(DetalleServicioActivity.this, mensaje, Toast.LENGTH_SHORT).show());
    }
}
