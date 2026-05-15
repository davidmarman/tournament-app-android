package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.data.repository.EquipoRepository;
import com.example.tournamentapp.data.repository.TorneoRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TorneosViewModel extends AndroidViewModel {

    private TorneoRepository torneoRepository;
    private EquipoRepository equipoRepository; // Para pedir los equipos para el Pop-up

    private MutableLiveData<List<ItemSimple>> torneosData = new MutableLiveData<>();
    private MutableLiveData<List<EquipoResponse>> misEquipos = new MutableLiveData<>();
    private MutableLiveData<String> mensajeExito = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public TorneosViewModel(@NonNull Application application) {
        super(application);
        torneoRepository = new TorneoRepository(application);
        equipoRepository = new EquipoRepository(application);
    }

    public LiveData<List<ItemSimple>> getTorneosData() { return torneosData; }
    public LiveData<List<EquipoResponse>> getMisEquipos() { return misEquipos; }
    public LiveData<String> getMensajeExito() { return mensajeExito; }
    public LiveData<String> getErrorMsg() { return errorMsg; }

    public void cargarTorneos() {
        torneoRepository.getMisTorneos(new Callback<List<ItemSimple>>() {
            @Override
            public void onResponse(Call<List<ItemSimple>> call, Response<List<ItemSimple>> response) {
                if (response.isSuccessful()) torneosData.postValue(response.body());
                else errorMsg.postValue("Error al cargar torneos");
            }
            @Override
            public void onFailure(Call<List<ItemSimple>> call, Throwable t) {
                errorMsg.postValue("Fallo de red");
            }
        });
    }

    public void cargarEquiposParaDialog() {
        equipoRepository.getMisEquipos(new Callback<List<EquipoResponse>>() {
            @Override
            public void onResponse(Call<List<EquipoResponse>> call, Response<List<EquipoResponse>> response) {
                if (response.isSuccessful()) misEquipos.postValue(response.body());
            }
            @Override
            public void onFailure(Call<List<EquipoResponse>> call, Throwable t) {}
        });
    }

    public void inscribirEquipo(String codigo, int idEquipo) {
        torneoRepository.inscribirTorneo(codigo, idEquipo, new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeExito.postValue(response.body().get("msg"));
                    cargarTorneos(); // Refrescamos la lista de torneos
                } else {
                    errorMsg.postValue("Código inválido o equipo ya inscrito");
                }
            }
            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                errorMsg.postValue("Fallo de red");
            }
        });
    }

    public void crearTorneo(String nombre, String descripcion, String fechaInicio,
                            String diasJuego, String horariosJuego, String formato, Uri logoUri) {
        File file = null;
        if (logoUri != null) {
            file = uriToFile(logoUri);
        }

        // Por defecto lo crearemos de tipo "Liga"
        torneoRepository.crearTorneo(nombre, "Liga", descripcion, fechaInicio, diasJuego, horariosJuego, formato, file, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mensajeExito.postValue((String) response.body().get("msg"));
                } else {
                    errorMsg.postValue("Error al crear el torneo");
                }
            }
            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                errorMsg.postValue("Error de conexión");
            }
        });
    }

    // Utilidad: Convertir URI a File físico
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("torneo_tmp", ".jpg", getApplication().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);
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
}