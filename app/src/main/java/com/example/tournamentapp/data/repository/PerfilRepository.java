package com.example.tournamentapp.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

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
}

