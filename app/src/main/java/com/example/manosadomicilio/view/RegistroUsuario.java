package com.example.manosadomicilio.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.manosadomicilio.R;
import com.example.manosadomicilio.controller.SupabaseClient;

import java.io.IOException;

import okhttp3.*;

public class RegistroUsuario extends AppCompatActivity {

    Button btnLogin, btnRegistrarse;
    EditText etUsuario,etEmail,etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnLogin = findViewById(R.id.btnLogin);
        etUsuario = findViewById(R.id.etUsuario);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        btnRegistrarse.setOnClickListener(v -> registerUser());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroUsuario.this, InicioSesion.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String name = etUsuario.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        SupabaseClient.insertUser(name, email, password).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(RegistroUsuario.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistroUsuario.this, InicioSesion.class));
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(RegistroUsuario.this, "Error al registrar", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}