package com.example.tournamentapp.data.repository;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.PartidoItem;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeRepository {

    private ApiService apiService;
    private SessionManager sessionManager;

    public HomeRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(context);
    }

    public void obtenerMisPartidos(MutableLiveData<List<PartidoItem>> listaPartidos, MutableLiveData<String> errorMessage) {

        String token = "Bearer " + sessionManager.fetchAuthToken();

        apiService.getMisPartidos(token).enqueue(new Callback<List<PartidoItem>>() {
            @Override
            public void onResponse(Call<List<PartidoItem>> call, Response<List<PartidoItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Éxito: Le pasamos la lista de partidos al ViewModel
                    listaPartidos.postValue(response.body());
                } else {
                    errorMessage.postValue("Error al cargar los partidos");
                }
            }

            @Override
            public void onFailure(Call<List<PartidoItem>> call, Throwable t) {
                errorMessage.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}