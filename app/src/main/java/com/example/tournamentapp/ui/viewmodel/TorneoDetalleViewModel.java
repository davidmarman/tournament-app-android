package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.AdminUserResponse;
import com.example.tournamentapp.data.model.PartidoItem;
import com.example.tournamentapp.data.model.TorneoDetalleResponse;
import com.example.tournamentapp.data.repository.TorneoDetalleRepository;

import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorneoDetalleViewModel extends AndroidViewModel {
    private TorneoDetalleRepository repository;
    private MutableLiveData<TorneoDetalleResponse> torneoDetalle = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> calendarioGeneradoExito = new MutableLiveData<>();

    private MutableLiveData<Boolean> torneoEliminadoExito = new MutableLiveData<>();
    private MutableLiveData<Boolean> torneoFinalizadoExito = new MutableLiveData<>();
    private MutableLiveData<List<PartidoItem>> partidosJornada = new MutableLiveData<>();
    private MutableLiveData<List<AdminUserResponse>> adminsTorneo = new MutableLiveData<>();
    private MutableLiveData<String> adminAccionExito = new MutableLiveData<>();

    public TorneoDetalleViewModel(@NonNull Application application) {
        super(application);
        repository = new TorneoDetalleRepository(application);
    }

    public LiveData<TorneoDetalleResponse> getTorneoDetalle() { return torneoDetalle; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<Boolean> getCalendarioGeneradoExito() { return calendarioGeneradoExito; }

    public LiveData<Boolean> getTorneoEliminadoExito() { return torneoEliminadoExito; }
    public LiveData<Boolean> getTorneoFinalizadoExito() { return torneoFinalizadoExito; }
    public LiveData<List<PartidoItem>> getPartidosJornada() { return partidosJornada; }
    public LiveData<List<AdminUserResponse>> getAdminsTorneo(){ return adminsTorneo; }
    public LiveData<String> getAdminAccionExito(){ return adminAccionExito; }

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

    public void generarCalendario(int id) {
        repository.generarCalendario(id, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    calendarioGeneradoExito.postValue(true);
                } else {
                    errorMsg.postValue("Error: Faltan equipos o ya está generado.");
                    calendarioGeneradoExito.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Error de conexión");
                calendarioGeneradoExito.postValue(false);
            }
        });
    }

    public void finalizarTorneo(int id) {
        repository.finalizarTorneo(id, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    torneoFinalizadoExito.postValue(true);
                } else {
                    errorMsg.postValue("Error al finalizar el torneo. Revisa que haya partidos jugados.");
                    torneoFinalizadoExito.postValue(false);
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Error de conexión al finalizar");
                torneoFinalizadoExito.postValue(false);
            }
        });
    }

    public void cargarJornadaEspecifica(int idTorneo, int numJornada) {
        repository.getDetalleTorneoConJornada(idTorneo, numJornada, new Callback<TorneoDetalleResponse>() {
            @Override
            public void onResponse(Call<TorneoDetalleResponse> call, Response<TorneoDetalleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Actualizamos solo la lista de partidos
                    partidosJornada.postValue(response.body().partidos);
                }
            }
            @Override
            public void onFailure(Call<TorneoDetalleResponse> call, Throwable t) { /* error */ }
        });
    }

    public void cargarAdministradores(int idTorneo) {
        repository.getAdministradores(idTorneo, new Callback<List<com.example.tournamentapp.data.model.AdminUserResponse>>() {
            @Override
            public void onResponse(Call<List<com.example.tournamentapp.data.model.AdminUserResponse>> call, Response<List<com.example.tournamentapp.data.model.AdminUserResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adminsTorneo.postValue(response.body());
                }
            }
            @Override
            public void onFailure(Call<List<com.example.tournamentapp.data.model.AdminUserResponse>> call, Throwable t) {
                errorMsg.postValue("Error al cargar administradores");
            }
        });
    }

    public void anadirAdministrador(int idTorneo, String username) {
        repository.anadirAdmin(idTorneo, username, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adminAccionExito.postValue(response.body().get("msg"));
                    cargarAdministradores(idTorneo); // Recargamos la lista horizontal automáticamente
                } else {
                    errorMsg.postValue("Error: El usuario debe ser Admin global y no estar ya incluido");
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }

    public void eliminarAdministrador(int idTorneo, int idUsuario) {
        repository.eliminarAdmin(idTorneo, idUsuario, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adminAccionExito.postValue(response.body().get("msg"));
                    cargarAdministradores(idTorneo); // Recargamos para ver el cambio
                } else {
                    errorMsg.postValue("Error: Debe quedar al menos un administrador");
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }

}