package com.example.tournamentapp.data.repository;

import android.content.Context;
import com.example.tournamentapp.data.model.AdminDashboardResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;

public class AdminRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public AdminRepository(Context context) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(context);
    }

    public void fetchDashboardData(Callback<AdminDashboardResponse> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getAdminDashboard(token).enqueue(callback);
    }
}