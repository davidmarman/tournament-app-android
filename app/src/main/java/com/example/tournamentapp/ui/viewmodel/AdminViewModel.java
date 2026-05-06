package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.AdminDashboardResponse;
import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.repository.AdminRepository;
import com.example.tournamentapp.data.repository.PerfilRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminViewModel extends AndroidViewModel {

    private AdminRepository adminRepository;
    private PerfilRepository perfilRepository;

    private MutableLiveData<AdminDashboardResponse> dashboardData = new MutableLiveData<>();
    private MutableLiveData<PerfilResponse> perfilData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        adminRepository = new AdminRepository(application);
        perfilRepository = new PerfilRepository(application);
    }

    public LiveData<AdminDashboardResponse> getDashboardData() { return dashboardData; }
    public LiveData<PerfilResponse> getPerfilData() { return perfilData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void cargarTodo() {
        // 1. Cargamos el Dashboard (Torneos y Partidos)
        adminRepository.fetchDashboardData(new Callback<AdminDashboardResponse>() {
            @Override
            public void onResponse(Call<AdminDashboardResponse> call, Response<AdminDashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    dashboardData.postValue(response.body());
                } else {
                    errorMsg.postValue("Error al cargar el dashboard");
                }
            }
            @Override
            public void onFailure(Call<AdminDashboardResponse> call, Throwable t) {
                errorMsg.postValue("Fallo de red al cargar dashboard");
            }
        });

        // 2. Cargamos el Perfil del Admin (pasando 0 coge el suyo propio)
        perfilRepository.fetchPerfil(0, perfilData, errorMsg);
    }
}