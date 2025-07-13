package com.example.sendnotifications.presentation.register;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.example.sendnotifications.domain.repository.AuthRepository;

public class RegisterViewModelFactory implements ViewModelProvider.Factory {
    private final AuthRepository authRepository;

    public RegisterViewModelFactory(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
            return (T) new RegisterViewModel(authRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}