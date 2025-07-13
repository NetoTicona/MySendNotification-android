package com.example.sendnotifications.presentation.login;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.sendnotifications.domain.model.User;
import com.example.sendnotifications.domain.repository.AuthRepository;
import com.example.sendnotifications.domain.repository.Callback;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    // LiveData para observar el estado del login
    private final MutableLiveData<User> _loginResult = new MutableLiveData<>();
    public LiveData<User> loginResult = _loginResult;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;
    // Constructor que recibe el AuthRepository (inyección de dependencias)
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    // Método para iniciar sesión
    public void login(String name, String password) {
        if (name.isEmpty() || password.isEmpty()) {
            _errorMessage.postValue("name y contraseña son obligatorios");
            return;
        }

        _isLoading.postValue(true);

        authRepository.login(name, password, new Callback<User>() {

            @Override
            public void onSuccess(User user) {
                //registerFCMTokenAndCompleteLogin(user); // Paso clave
                updateFCMTokenAndCompleteLogin(user);

            }

            @Override
            public void onError(String error) {
                _isLoading.postValue(false);
                _errorMessage.postValue(error);
            }
        });

    }


    private void registerFCMTokenAndCompleteLogin(User user) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String token = task.getResult();
                        authRepository.registerFCMToken(token, new Callback<Boolean>() {

                            @Override
                            public void onSuccess(Boolean success) {
                                _isLoading.postValue(false);
                                _loginResult.postValue(user); // Login completo
                            }

                            @Override
                            public void onError(String error) {
                                _isLoading.postValue(false);
                                _errorMessage.postValue("Registro de notificaciones falló: " + error);
                            }
                        });
                    } else {
                        _isLoading.postValue(false);
                        _errorMessage.postValue("Error al obtener token FCM");
                    }
                });
    }

    //-------------//
    private void updateFCMTokenAndCompleteLogin(User user) {

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String token = task.getResult();
                        authRepository.updateFCMToken(token, new Callback<Boolean>() {

                            @Override
                            public void onSuccess(Boolean success) {

                                _isLoading.postValue(false);
                                _loginResult.postValue(user); // Login completo
                            }

                            @Override
                            public void onError(String error) {
                                _isLoading.postValue(false);
                                _errorMessage.postValue("Registro de notificaciones falló: " + error);
                            }
                        });
                    } else {
                        _isLoading.postValue(false);
                        _errorMessage.postValue("Error al obtener token FCM");
                    }
                });
    }



}