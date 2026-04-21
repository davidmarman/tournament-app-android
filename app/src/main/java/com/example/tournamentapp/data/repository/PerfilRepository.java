package com.example.tournamentapp.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// REPOSITORY
public class PerfilRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public PerfilRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(context);
    }

    public void fetchPerfil(MutableLiveData<PerfilResponse> data, MutableLiveData<String> error) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getPerfil(token).enqueue(new Callback<PerfilResponse>() {
            @Override
            public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                if (response.isSuccessful()) data.postValue(response.body());
                else error.postValue("Error de servidor");
            }
            @Override
            public void onFailure(Call<PerfilResponse> call, Throwable t) { error.postValue(t.getMessage()); }
        });
    }

    public void editarPerfil(String nombre, String apellido, File imageFile, Callback<Map<String, Object>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();

        RequestBody rbNombre = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody rbApellido = RequestBody.create(MediaType.parse("text/plain"), apellido);

        MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            RequestBody rbFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
            imagePart = MultipartBody.Part.createFormData("imagen_perfil", imageFile.getName(), rbFile);
        }

        apiService.editarPerfil(token, rbNombre, rbApellido, imagePart).enqueue(callback);
    }
}

