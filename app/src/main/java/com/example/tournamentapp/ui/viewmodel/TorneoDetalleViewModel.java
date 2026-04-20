package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.tournamentapp.data.model.TorneoDetalleResponse;
import com.example.tournamentapp.data.repository.TorneoDetalleRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorneoDetalleViewModel extends AndroidViewModel {
    private TorneoDetalleRepository repository;
    private MutableLiveData<TorneoDetalleResponse> torneoDetalle = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public TorneoDetalleViewModel(@NonNull Application application) {
        super(application);
        repository = new TorneoDetalleRepository(application);
    }

    public LiveData<TorneoDetalleResponse> getTorneoDetalle() { return torneoDetalle; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void cargarDetalle(int id) {
        repository.getDetalleTorneo(id, new Callback<TorneoDetalleResponse>() {
            @Override
            public void onResponse(Call<TorneoDetalleResponse> call, Response<TorneoDetalleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    torneoDetalle.postValue(response.body());
                } else {
                    errorMsg.postValue("Error al cargar la información del torneo");
                }
            }

            @Override
            public void onFailure(Call<TorneoDetalleResponse> call, Throwable t) {
                errorMsg.postValue("Error de conexión con el servidor");
            }
        });
    }
}