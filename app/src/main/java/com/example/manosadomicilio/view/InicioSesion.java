package com.example.manosadomicilio.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.DataParser;
import com.example.manosadomicilio.controller.SupabaseClient;
import com.example.manosadomicilio.model.Usuario;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InicioSesion extends AppCompatActivity {

    private Button btnLogin, btnRegistrarse;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio_sesion);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etContraseña);

        btnLogin.setOnClickListener(v -> login());
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesion.this, RegistroUsuario.class);
            startActivity(intent);
        });
    }

    private void login() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, llene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Cargando...");

        SupabaseClient.loginUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                mostrarError("Error de conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseData = response.body().string();

                        Usuario usuario = DataParser.parseUsuario(responseData);

                        if (usuario != null) {
                            guardarSesionYContinuar(usuario);
                        } else {
                            mostrarError("Correo o contraseña incorrectos");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        mostrarError("Error procesando datos");
                    }
                } else {
                    mostrarError("Error en el servidor");
                }
            }
        });
    }

    private void guardarSesionYContinuar(Usuario usuario) {
        SharedPreferences prefs = getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("usuario_id", usuario.getId());
        editor.putString("usuario_nombre", usuario.getName());
        editor.putString("usuario_email", usuario.getEmail());
        editor.apply();

        runOnUiThread(() -> {
            Toast.makeText(InicioSesion.this, "Bienvenido " + usuario.getName(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(InicioSesion.this, Home.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarError(String mensaje) {
        runOnUiThread(() -> {
            btnLogin.setEnabled(true);
            btnLogin.setText("Iniciar Sesión");
            Toast.makeText(InicioSesion.this, mensaje, Toast.LENGTH_LONG).show();
        });
    }
}
