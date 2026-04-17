package com.example.tournamentapp.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public LoginRepository(Context context){
        // Inicializamos Retrofit y el gestor de Sesion
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(context);
    }

    // Usamos LiveData para avisar a la pantalla si ha ido bien o mal
    public void login(String email, String password, MutableLiveData<Boolean> isSuccess, MutableLiveData<String> errorMessage) {
        LoginRequest request = new LoginRequest(email, password);

        apiService.login(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Guardamos el token y el rol en las preferencias
                    LoginResponse loginData = response.body();
                    sessionManager.saveAuthToken(loginData.getToken(), loginData.getRol(), loginData.getIdUsuario());

                    // Avisamos de que todo ha ido genial
                    isSuccess.postValue(true);
                } else {
                    // Error de credenciales (Ej: Error 401)
                    errorMessage.postValue("Email o contraseña incorrectos");
                    isSuccess.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Error de red (Ej: Servidor apagado)
                errorMessage.postValue("Error de conexión: " + t.getMessage());
                isSuccess.postValue(false);
            }
        });
    }
}
