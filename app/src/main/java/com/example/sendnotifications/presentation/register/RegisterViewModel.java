package com.example.sendnotifications.presentation.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.sendnotifications.domain.model.User;
import com.example.sendnotifications.domain.repository.AuthRepository;
import com.example.sendnotifications.domain.repository.Callback;

public class RegisterViewModel extends ViewModel {
    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> _registerResult = new MutableLiveData<>();
    public LiveData<Boolean> registerResult = _registerResult;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    public RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void register(User user) {
        if (user.getName().isEmpty()  || user.getPassword().isEmpty()  ) {
            _errorMessage.postValue("Complete todos lo campos porfavor");
            return;
        }

        _isLoading.setValue(true);

        authRepository.register(user, new Callback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                _isLoading.setValue(false);
                _registerResult.setValue(success);
            }

            @Override
            public void onError(String error) {
                _isLoading.setValue(false);
                _errorMessage.setValue(error);
            }
        });
    }
}