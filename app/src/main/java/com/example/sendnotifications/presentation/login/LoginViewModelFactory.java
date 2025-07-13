package com.example.sendnotifications.presentation.login;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.sendnotifications.domain.repository.AuthRepository;

public class LoginViewModelFactory implements ViewModelProvider.Factory {
    private final AuthRepository authRepository;

    public LoginViewModelFactory(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}