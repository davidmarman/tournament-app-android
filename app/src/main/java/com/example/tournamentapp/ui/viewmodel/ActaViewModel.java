package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.ActaResponse;
import com.example.tournamentapp.data.model.FinalizarPartidoRequest;
import com.example.tournamentapp.data.repository.ActaRepository;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActaViewModel extends AndroidViewModel {
    private ActaRepository repository;

    private MutableLiveData<ActaResponse> actaData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> partidoFinalizadoExito = new MutableLiveData<>();

    public ActaViewModel(@NonNull Application application) {
        super(application);
        repository = new ActaRepository(application);
    }

    public LiveData<ActaResponse> getActaData() { return actaData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<Boolean> getPartidoFinalizadoExito() { return partidoFinalizadoExito; }

    // 1. Método para cargar el acta al abrir la pantalla
    public void cargarActa(int idPartido) {
        repository.getActaPartido(idPartido, new Callback<ActaResponse>() {
            @Override
            public void onResponse(Call<ActaResponse> call, Response<ActaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    actaData.postValue(response.body());
                } else {
                    errorMsg.postValue("Error al cargar el acta del partido");
                }
            }

            @Override
            public void onFailure(Call<ActaResponse> call, Throwable t) {
                errorMsg.postValue("Error de conexión al cargar el acta");
            }
        });
    }

    // 2. Método para enviar el acta final a Flask
    public void enviarResultadoFinal(int idPartido, FinalizarPartidoRequest request) {
        repository.finalizarPartido(idPartido, request, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    partidoFinalizadoExito.postValue(true);
                } else {
                    errorMsg.postValue("Error al finalizar el partido. Revisa los datos.");
                    partidoFinalizadoExito.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Error de conexión al finalizar el partido");
                partidoFinalizadoExito.postValue(false);
            }
        });
    }
}