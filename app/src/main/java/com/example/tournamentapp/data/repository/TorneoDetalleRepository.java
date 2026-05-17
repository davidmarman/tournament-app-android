package com.example.tournamentapp.data.repository;

import android.app.Application;

import com.example.tournamentapp.data.model.AdminUserResponse;
import com.example.tournamentapp.data.model.TorneoDetalleResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.util.List;
import java.util.Map;

import retrofit2.Callback;

public class TorneoDetalleRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public TorneoDetalleRepository(Application application) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(application);
    }

    public void getDetalleTorneo(int id, Callback<TorneoDetalleResponse> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getDetalleTorneo(token, id,null).enqueue(callback);
    }

    public void getDetalleTorneoConJornada(int id, int jornada, Callback<TorneoDetalleResponse> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getDetalleTorneo(token, id, jornada).enqueue(callback);
    }

    public void eliminarTorneo(int idTorneo, Callback<Map<String, String>> callback) {
        // Asegúrate de tener importado el SessionManager
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.eliminarTorneo(token, idTorneo).enqueue(callback);
    }

    public void generarCalendario(int idTorneo, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.generarCalendario(token, idTorneo).enqueue(callback);
    }

    public void finalizarTorneo(int idTorneo, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.finalizarTorneo(token, idTorneo).enqueue(callback);
    }

    public void getAdministradores(int idTorneo, Callback<List<AdminUserResponse>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getAdministradores(token, idTorneo).enqueue(callback);
    }

    public void anadirAdmin(int idTorneo, String username, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        Map<String, String> body = new java.util.HashMap<>();
        body.put("username", username);
        apiService.anadirAdmin(token, idTorneo, body).enqueue(callback);
    }

    public void eliminarAdmin(int idTorneo, int idUsuario, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.eliminarAdmin(token, idTorneo, idUsuario).enqueue(callback);
    }
}