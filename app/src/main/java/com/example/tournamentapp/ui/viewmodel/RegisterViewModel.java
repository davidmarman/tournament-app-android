package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.repository.RegisterRepository;

import java.io.File;

public class RegisterViewModel extends AndroidViewModel {
    private RegisterRepository repository;

    private MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        repository = new RegisterRepository();
    }

    public LiveData<Boolean> getRegisterSuccess() {
        return registerSuccess;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void realizarRegistro(String nombre, String apellido, String username, String email, String password, String rol, File imagen) {
        // Validación básica
        if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || rol.isEmpty()) {
            errorMessage.setValue("Por favor, rellena todos los campos");
            return;
        }

        if (password.length() < 3) {
            errorMessage.setValue("La contraseña debe ser más larga");
            return;
        }

        repository.register(nombre, apellido, username, email, password, rol, imagen, registerSuccess, errorMessage);
    }
}
