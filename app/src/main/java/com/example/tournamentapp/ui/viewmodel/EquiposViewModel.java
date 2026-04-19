package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiposViewModel extends AndroidViewModel {

    private MutableLiveData<List<EquipoResponse>> equiposData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private SessionManager sessionManager;

    public EquiposViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
    }

    public LiveData<List<EquipoResponse>> getEquiposData() { return equiposData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void cargarEquipos() {
        String token = "Bearer " + sessionManager.fetchAuthToken();

        RetrofitClient.getApiService().getMisEquipos(token).enqueue(new Callback<List<EquipoResponse>>() {
            @Override
            public void onResponse(Call<List<EquipoResponse>> call, Response<List<EquipoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    equiposData.postValue(response.body());
                } else {
                    errorMsg.postValue("Error al cargar los equipos");
                }
            }

            @Override
            public void onFailure(Call<List<EquipoResponse>> call, Throwable t) {
                errorMsg.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
}