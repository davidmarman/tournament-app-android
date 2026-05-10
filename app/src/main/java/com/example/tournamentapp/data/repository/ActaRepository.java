package com.example.tournamentapp.data.repository;

import android.content.Context;

import com.example.tournamentapp.data.model.ActaResponse;
import com.example.tournamentapp.data.model.FinalizarPartidoRequest;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class ActaRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public ActaRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(context);
    }

    // Pedir los datos vacíos del partido
    public void getActaPartido(int idPartido, Callback<ActaResponse> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getActaPartido(token, idPartido).enqueue(callback);
    }

    // Enviar el resultado y los eventos de los jugadores
    public void finalizarPartido(int idPartido, FinalizarPartidoRequest request, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.finalizarPartido(token, idPartido, request).enqueue(callback);
    }
}