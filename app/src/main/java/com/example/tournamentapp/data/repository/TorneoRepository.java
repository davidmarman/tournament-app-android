package com.example.tournamentapp.data.repository;

import android.app.Application;

import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class TorneoRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public TorneoRepository(Application application) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(application);
    }

    public void getMisTorneos(Callback<List<ItemSimple>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getMisTorneos(token).enqueue(callback);
    }

    public void inscribirTorneo(String codigoAcceso, int idEquipo, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();

        Map<String, Object> body = new HashMap<>();
        body.put("codigo_acceso", codigoAcceso);
        body.put("id_equipo", idEquipo);

        apiService.inscribirTorneo(token, body).enqueue(callback);
    }
}