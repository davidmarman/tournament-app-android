package com.example.tournamentapp.data.repository;

import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.RegisterRequest;
import com.example.tournamentapp.data.model.RegisterResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRepository {
    private ApiService apiService;

    public RegisterRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public void register(String nombre, String apellido, String username, String email,
                         String password, String rol, File imageFile,
                         MutableLiveData<Boolean> isSuccess, MutableLiveData<String> errorMessage) {

        // Convertimos los textos en RequestBody
        RequestBody rbNombre = RequestBody.create(okhttp3.MediaType.parse("text/plain"), nombre);
        RequestBody rbApellido = RequestBody.create(okhttp3.MediaType.parse("text/plain"), apellido);
        RequestBody rbUsername = RequestBody.create(okhttp3.MediaType.parse("text/plain"), username);
        RequestBody rbEmail = RequestBody.create(okhttp3.MediaType.parse("text/plain"), email);
        RequestBody rbPassword = RequestBody.create(okhttp3.MediaType.parse("text/plain"), password);
        RequestBody rbRol = RequestBody.create(okhttp3.MediaType.parse("text/plain"), rol);

        // Preparamos la imagen (si hay una)
        MultipartBody.Part bodyImagen = null;
        if (imageFile != null) {
            RequestBody rbImagen = RequestBody.create(okhttp3.MediaType.parse("image/*"), imageFile);
            bodyImagen = MultipartBody.Part.createFormData("imagen_perfil", imageFile.getName(), rbImagen);
        }

        // Enviamos
        apiService.registerWithImage(rbNombre, rbApellido, rbUsername, rbEmail, rbPassword, rbRol, bodyImagen)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful()) isSuccess.postValue(true);
                        else errorMessage.postValue("Error en el registro.");
                    }
                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        errorMessage.postValue("Error: " + t.getMessage());
                    }
                });
    }
}
