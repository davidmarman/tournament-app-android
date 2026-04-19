package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipoDetalleViewModel extends AndroidViewModel {
    private MutableLiveData<EquipoDetalleResponse> equipoData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private SessionManager sessionManager;

    public EquipoDetalleViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<EquipoDetalleResponse> getEquipoData() { return equipoData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void cargarDetalle(int id) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        RetrofitClient.getApiService().getDetalleEquipo(token, id).enqueue(new Callback<EquipoDetalleResponse>() {
            @Override
            public void onResponse(Call<EquipoDetalleResponse> call, Response<EquipoDetalleResponse> response) {
                if (response.isSuccessful()) equipoData.postValue(response.body());
                else errorMsg.postValue("Error al cargar detalles");
            }
            @Override
            public void onFailure(Call<EquipoDetalleResponse> call, Throwable t) {
                errorMsg.postValue("Error de conexión");
            }
        });
    }
}