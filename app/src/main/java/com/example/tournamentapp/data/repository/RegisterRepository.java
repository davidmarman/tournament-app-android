package com.example.tournamentapp.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.RegisterRequest;
import com.example.tournamentapp.data.model.RegisterResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {
    private ApiService apiService;

    public RegisterRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void register(String nombre, String apellido, String username, String email, String password, String rol,
                         MutableLiveData<Boolean> isSuccess, MutableLiveData<String> errorMessage) {

        RegisterRequest request = new RegisterRequest(nombre, apellido, username, email, password, rol);

        apiService.register(request).enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito en el registro
                    isSuccess.postValue(true);
                } else {
                    // Error (Ej: El email ya existe)
                    // Para leer el cuerpo del error en Retrofit cuando la respuesta no es 2xx:
                    errorMessage.postValue("Error en el registro. Posiblemente el correo ya exista.");
                    isSuccess.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                // Error de red
                errorMessage.postValue("Error de conexión: " + t.getMessage());
                isSuccess.postValue(false);
            }
        });
    }
}
