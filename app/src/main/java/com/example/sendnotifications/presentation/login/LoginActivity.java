package com.example.sendnotifications.presentation.login;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;

import com.example.sendnotifications.MainActivity;
import com.example.sendnotifications.R;
import com.example.sendnotifications.domain.repository.AuthRepositoryImpl;
import com.example.sendnotifications.domain.repository.AuthRepository;
import com.example.sendnotifications.domain.repository.AuthRepositoryImpl;
import com.example.sendnotifications.presentation.register.RegisterActivity;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1001;
    private LoginViewModel viewModel;
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private Button buttonRegister;
    private boolean isPermissionRequested = false; // Bandera para controlar solicitudes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login); // Asegúrate de tener este layout

        // Inicializar vistas
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);


        // Inicializar ViewModel
        AuthRepository authRepository = new AuthRepositoryImpl(this);

        viewModel = new ViewModelProvider(this, new LoginViewModelFactory(authRepository))
                .get(LoginViewModel.class);

        // Observadores del ViewModel
        setupObservers();

        // Click listener para el botón de login
        buttonLogin.setOnClickListener(v -> attemptLogin());


        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });

        checkNotificationPermission();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationPermission();
    }


    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {
                // Permission already granted
                enableButtons(true);
            } else if (!isPermissionRequested) {
                // First time requesting
                enableButtons(false);
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS
                );
                isPermissionRequested = true;
            } else {
                // Permission was previously denied
                enableButtons(false);
            }


        }else{
            enableButtons(true);
        }
        // En versiones anteriores no se requiere permiso explícito
    }











    private void setupObservers() {
        viewModel.loginResult.observe(this, user -> {
            // Redirigir a MainActivity si el login es exitoso
            Toast.makeText(this, "¡Bienvenido, " + user.getName() + "!", Toast.LENGTH_SHORT).show();
             startActivity(new Intent(this, MainActivity.class));
             finish();
        });

        viewModel.errorMessage.observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        viewModel.isLoading.observe(this, isLoading -> {
            buttonLogin.setEnabled(!isLoading); // Deshabilitar botón durante carga
        });
    }



    private void attemptLogin() {
        String name = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        viewModel.login(name, password);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableButtons(true);
            } else {
                enableButtons(false);
                Toast.makeText(this,
                        "Podrás activar los permisos más tarde en Configuración",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void enableButtons(boolean enable) {
        buttonLogin.setEnabled(enable);
        buttonRegister.setEnabled(enable);
    }



}