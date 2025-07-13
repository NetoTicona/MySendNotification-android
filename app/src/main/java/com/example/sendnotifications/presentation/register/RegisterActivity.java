package com.example.sendnotifications.presentation.register;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.sendnotifications.R;
import com.example.sendnotifications.domain.repository.AuthRepositoryImpl;
import com.example.sendnotifications.domain.model.User;
import com.example.sendnotifications.domain.repository.AuthRepository;

public class RegisterActivity extends AppCompatActivity {

    private RegisterViewModel viewModel;
    private EditText editTextName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private Button buttonRegister, buttonBackToLogin ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicializar vistas
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        //String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonBackToLogin = findViewById(R.id.buttonBackToLogin);

        // Inicializar ViewModel
        AuthRepository authRepository = new AuthRepositoryImpl(this);
        viewModel = new ViewModelProvider(this, new RegisterViewModelFactory(authRepository))
                .get(RegisterViewModel.class);

        // Observadores
        setupObservers();
        buttonBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra esta Activity y regresa a la anterior (Login)
                finish();
            }
        });

        // Click listener
        buttonRegister.setOnClickListener(v -> attemptRegister());
    }

    private void setupObservers() {

        viewModel.registerResult.observe(this, isSuccess -> {
            if (isSuccess) {
                Toast.makeText(this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                finish(); // Volver a LoginActivity
            }
        });

        viewModel.errorMessage.observe(this, error -> {
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        });

        viewModel.isLoading.observe(this, isLoading -> {
            buttonRegister.setEnabled(!isLoading);
        });
    }

    private void attemptRegister() {
        String name = editTextName.getText().toString().trim();
        //String confirmPassword = editTextConfirmPassword.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validación básica en la Activity (opcional, puede moverse al ViewModel)
        /*if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            return;
        }*/


        User newUser = new User("0", name, password); // ID temporal "0"


        viewModel.register(newUser);
    }
}