package com.example.manosadomicilio.view;

import android.app.DatePickerDialog;import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.DireccionCliente;
import com.example.manosadomicilio.model.Zona;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SolicitudServicio extends AppCompatActivity {

    private Spinner spinnerDirecciones, spinnerZonas;
    private EditText etFecha, etHora;
    private EditText etDescripcion, etCalle, etNumero, etColonia, etMunicipio, etEstado, etPais, etReferencias;
    private Button btnConfirmarSolicitud;
    private LinearLayout containerNuevaDireccion;

    private int idTrabajador, categoriaId, usuarioId;
    private Button btnToggleDireccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solicitud_servicio);

        idTrabajador = getIntent().getIntExtra("idTrabajador", -1);
        categoriaId = getIntent().getIntExtra("categoriaId", -1);
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        usuarioId = prefs.getInt("usuario_id", -1);

        bindUI();
        setupListeners();
        cargarDirecciones();
        cargarZonas();
    }

    private void bindUI() {
        spinnerDirecciones = findViewById(R.id.spinnerDirecciones);
        spinnerZonas = findViewById(R.id.spinnerZonas);

        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etDescripcion = findViewById(R.id.etDescServicio);
        etCalle = findViewById(R.id.etCalle);
        etNumero = findViewById(R.id.etNumero);
        etColonia = findViewById(R.id.etColonia);
        etMunicipio = findViewById(R.id.etMunicipio);
        etEstado = findViewById(R.id.etEstado);
        etPais = findViewById(R.id.etPais);
        etReferencias = findViewById(R.id.etReferencias);
        btnConfirmarSolicitud = findViewById(R.id.btnConfirmarSolicitud);
        containerNuevaDireccion = findViewById(R.id.containerNuevaDireccion);

        btnToggleDireccion = findViewById(R.id.btnToggleDireccion);
    }

    private void setupListeners() {
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        etHora.setOnClickListener(v -> mostrarTimePicker());
        btnConfirmarSolicitud.setOnClickListener(v -> confirmarSolicitud());

        if (btnToggleDireccion != null) {
            btnToggleDireccion.setOnClickListener(v -> toggleNuevaDireccion());
        }
    }

    private void cargarDirecciones() {
        if (usuarioId == -1) return;
        SupabaseClient.getDireccionesUsuario(usuarioId, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { /* ... */ }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<DireccionCliente> direcciones = DataParser.parseDirecciones(responseData);
                        runOnUiThread(() -> {
                            ArrayAdapter<DireccionCliente> adapter = new ArrayAdapter<>(SolicitudServicio.this, android.R.layout.simple_spinner_dropdown_item, direcciones);
                            spinnerDirecciones.setAdapter(adapter);
                        });
                    } catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void cargarZonas() {
        SupabaseClient.getZonas(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { /* ... */ }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();
                        List<Zona> zonas = DataParser.parseZonas(responseData);
                        runOnUiThread(() -> {
                            ArrayAdapter<Zona> adapter = new ArrayAdapter<>(SolicitudServicio.this, android.R.layout.simple_spinner_dropdown_item, zonas);
                            spinnerZonas.setAdapter(adapter);
                        });
                    } catch (JSONException e) { e.printStackTrace(); }
                }
            }
        });
    }

    private void confirmarSolicitud() {
        btnConfirmarSolicitud.setEnabled(false);
        btnConfirmarSolicitud.setText("Procesando...");

        if (containerNuevaDireccion.getVisibility() == View.VISIBLE) {
            guardarNuevaDireccionYCrearServicio();
        } else {
            DireccionCliente direccionSeleccionada = (DireccionCliente) spinnerDirecciones.getSelectedItem();
            if (direccionSeleccionada == null) {
                mostrarError("Por favor, seleccione o agregue una dirección");
                return;
            }
            crearServicio(direccionSeleccionada.getId());
        }
    }

    private void guardarNuevaDireccionYCrearServicio() {

        Zona zonaSeleccionada = (Zona) spinnerZonas.getSelectedItem();
        if (zonaSeleccionada == null) {
            mostrarError("Debe seleccionar una zona para la nueva dirección");
            return;
        }
        int zonaId = zonaSeleccionada.getId();

        String calle = etCalle.getText().toString();
        String numero = etNumero.getText().toString();
        String colonia = etColonia.getText().toString();
        String municipio = etMunicipio.getText().toString();
        String estado = etEstado.getText().toString();
        String pais = etPais.getText().toString();
        String referencias = etReferencias.getText().toString();

        SupabaseClient.insertDireccion(usuarioId, zonaId, calle, numero, colonia, municipio, estado, pais, referencias, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { mostrarError("Error guardando dirección"); }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        if (jsonArray.length() > 0) {
                            JSONObject newAddress = jsonArray.getJSONObject(0);
                            int nuevaDireccionId = newAddress.getInt("id");
                            crearServicio(nuevaDireccionId);
                        }
                    } catch (JSONException e) { mostrarError("Error procesando respuesta de dirección"); }
                } else { mostrarError("Fallo al guardar dirección: " + response.code()); }
            }
        });
    }

    private void crearServicio(int direccionId) {
        String fecha = etFecha.getText().toString();
        String hora = etHora.getText().toString();
        String descripcion = etDescripcion.getText().toString();

        if (fecha.isEmpty() || hora.isEmpty() || descripcion.isEmpty()) {
            mostrarError("Complete todos los detalles del servicio (fecha, hora y descripción)");
            return;
        }

        SupabaseClient.insertServicio(usuarioId, idTrabajador, categoriaId, direccionId, fecha, hora, descripcion, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) { mostrarError("Error creando el servicio"); }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(SolicitudServicio.this, "Servicio solicitado con éxito", Toast.LENGTH_LONG).show();
                        finish();
                    });
                } else {
                    mostrarError("No se pudo crear el servicio: " + response.code() + " " + response.message());
                }
            }
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> {
            btnConfirmarSolicitud.setEnabled(true);
            btnConfirmarSolicitud.setText("Confirmar Solicitud");
            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
        });
    }

    private void toggleNuevaDireccion() {
        if (containerNuevaDireccion.getVisibility() == View.GONE) {
            containerNuevaDireccion.setVisibility(View.VISIBLE);
            if(btnToggleDireccion != null) btnToggleDireccion.setText("Usar dirección guardada");
            spinnerDirecciones.setVisibility(View.GONE);
        } else {
            containerNuevaDireccion.setVisibility(View.GONE);
            if(btnToggleDireccion != null) btnToggleDireccion.setText("Agregar nueva dirección");
            spinnerDirecciones.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarDatePicker() {
        Calendar cal = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fechaSeleccionada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            etFecha.setText(fechaSeleccionada);
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void mostrarTimePicker() {
        Calendar cal = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String horaSeleccionada = String.format(Locale.getDefault(), "%02d:%02d:00", hourOfDay, minute);
            etHora.setText(horaSeleccionada);
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }
}
