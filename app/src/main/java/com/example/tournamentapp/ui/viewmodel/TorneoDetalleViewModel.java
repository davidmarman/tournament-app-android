package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.tournamentapp.data.model.TorneoDetalleResponse;
import com.example.tournamentapp.data.repository.TorneoDetalleRepository;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorneoDetalleViewModel extends AndroidViewModel {
    private TorneoDetalleRepository repository;
    private MutableLiveData<TorneoDetalleResponse> torneoDetalle = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    // ¡NUEVO! Variable para vigilar si el borrado ha ido bien
    private MutableLiveData<Boolean> torneoEliminadoExito = new MutableLiveData<>();

    public TorneoDetalleViewModel(@NonNull Application application) {
        super(application);
        repository = new TorneoDetalleRepository(application);
    }

    public LiveData<TorneoDetalleResponse> getTorneoDetalle() { return torneoDetalle; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    // ¡NUEVO! Getter para la vista
    public LiveData<Boolean> getTorneoEliminadoExito() { return torneoEliminadoExito; }

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

    // ¡NUEVO! Método que llamamos al darle al botón rojo
    public void eliminarTorneo(int id) {
        repository.eliminarTorneo(id, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    torneoEliminadoExito.postValue(true);
                } else {
                    errorMsg.postValue("Error al eliminar el torneo");
                    torneoEliminadoExito.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Error de conexión al eliminar");
                torneoEliminadoExito.postValue(false);
            }
        });
    }
}