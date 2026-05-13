package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.repository.EquipoRepository;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquipoDetalleViewModel extends AndroidViewModel {

    private EquipoRepository repository;
    private MutableLiveData<EquipoDetalleResponse> equipoData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<String> exitoAnadirMsg = new MutableLiveData<>();
    private MutableLiveData<String> mensajeExito = new MutableLiveData<>();
    private MutableLiveData<Boolean> salirExit_Status = new MutableLiveData<>();

    public EquipoDetalleViewModel(@NonNull Application application) {
        super(application);
        repository = new EquipoRepository(application); // Instanciamos el repositorio
    }

    public LiveData<EquipoDetalleResponse> getEquipoData() { return equipoData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<String> getExitoAnadirMsg() { return exitoAnadirMsg; }
    public LiveData<String> getMensajeExito() { return mensajeExito; }
    public LiveData<Boolean> getSalirExitStatus() { return salirExit_Status; }

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

    public void expulsarJugador(int idEquipo, int idJugador) {
        repository.expulsarJugador(idEquipo, idJugador, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeExito.postValue(response.body().get("msg"));
                    cargarDetalle(idEquipo); // ¡Recargamos la plantilla mágicamente!
                } else {
                    errorMsg.postValue("Error: No se pudo expulsar al jugador.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Error de conexión al servidor.");
            }
        });
    }

    public void salirDelEquipo(int idEquipo) {
        repository.salirEquipo(idEquipo, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    mensajeExito.postValue(response.body().get("msg"));
                    salirExit_Status.postValue(true); // Avisamos para volver a la pantalla anterior
                } else {
                    errorMsg.postValue("Error: No se pudo salir del equipo");
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }

    public void disolverEquipo(int idEquipo) {
        repository.disolverEquipo(idEquipo, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    mensajeExito.postValue(response.body().get("msg"));
                    salirExit_Status.postValue(true);
                } else {
                    errorMsg.postValue("Error al disolver");
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }

    public void cederCapitania(int idEquipo, int nuevoCapitanId) {
        Map<String, Integer> body = new HashMap<>();
        body.put("nuevo_capitan_id", nuevoCapitanId);

        repository.cederCapitania(idEquipo, body, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful()) {
                    mensajeExito.postValue("Has cedido la capitanía correctamente.");
                    // Recargamos el detalle para que la UI se actualice
                    // (ahora verás el botón de "Salir" en lugar de "Disolver")
                    cargarDetalle(idEquipo);
                } else {
                    errorMsg.postValue("Error al ceder la capitanía.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Error de conexión.");
            }
        });
    }

    // Convertir URI a File en este ViewModel
    private java.io.File uriToFile(android.net.Uri uri) {
        try {
            java.io.InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            java.io.File tempFile = java.io.File.createTempFile("logo_tmp", ".jpg", getApplication().getCacheDir());
            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(tempFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) outputStream.write(buffer, 0, length);
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void editarEquipo(int idEquipo, String nombre, android.net.Uri imageUri) {
        java.io.File file = null;
        if (imageUri != null) file = uriToFile(imageUri);

        repository.editarEquipo(idEquipo, nombre, file, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeExito.postValue(response.body().get("msg"));
                    cargarDetalle(idEquipo); // ¡Recargamos para ver el nuevo nombre/logo!
                } else {
                    errorMsg.postValue("Error al editar equipo");
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }
}