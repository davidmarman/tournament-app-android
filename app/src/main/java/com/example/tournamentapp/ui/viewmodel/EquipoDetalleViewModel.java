package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.repository.EquipoRepository;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipoDetalleViewModel extends AndroidViewModel {

    private EquipoRepository repository;
    private MutableLiveData<EquipoDetalleResponse> equipoData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<String> exitoAnadirMsg = new MutableLiveData<>();

    public EquipoDetalleViewModel(@NonNull Application application) {
        super(application);
        repository = new EquipoRepository(application); // Instanciamos el repositorio
    }

    public LiveData<EquipoDetalleResponse> getEquipoData() { return equipoData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<String> getExitoAnadirMsg() { return exitoAnadirMsg; }

    public void cargarDetalle(int id) {
        repository.getDetalleEquipo(id, new Callback<EquipoDetalleResponse>() {
            @Override
            public void onResponse(Call<EquipoDetalleResponse> call, Response<EquipoDetalleResponse> response) {
                if (response.isSuccessful()) {
                    equipoData.postValue(response.body());
                } else {
                    errorMsg.postValue("Error al cargar detalles");
                }
            }

            @Override
            public void onFailure(Call<EquipoDetalleResponse> call, Throwable t) {
                errorMsg.postValue("Error de conexión");
            }
        });
    }

    public void anadirJugador(int equipoId, String username) {
        repository.anadirJugador(equipoId, username, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    exitoAnadirMsg.postValue(response.body().get("msg"));
                    cargarDetalle(equipoId);
                } else {
                    errorMsg.postValue("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }
}