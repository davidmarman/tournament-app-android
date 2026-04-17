package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.repository.LoginRepository;

import org.jspecify.annotations.NonNull;

public class LoginViewModel extends AndroidViewModel {
    private LoginRepository repository;

    // Variables observables que la pantalla (Fragment) estará vigilando
    private MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application){
        super(application);
        // Le pasamos el contexto al repositorio para que pueda usar el SessionManager
        repository = new LoginRepository(application.getApplicationContext());
    }

    // Getters para que el fragmento pueda observar los cambios
    public LiveData<Boolean> getLoginSuccess(){
        return loginSuccess;
    }

    public LiveData<String> getErrorMessage(){
        return errorMessage;
    }

    // Metodo que llamaremos cuando el usuario pulse el boton "Iniciar Sesion"
    public void realizarLogin(String email, String password){
        if (email.isEmpty() || password.isEmpty()){
            errorMessage.setValue("Por favor, rellene todos los campos");
            return;
        }
        // El respositorio hace el trabajo sucio y actualiza los LiveData
        repository.login(email,password,loginSuccess,errorMessage);

    }
}
