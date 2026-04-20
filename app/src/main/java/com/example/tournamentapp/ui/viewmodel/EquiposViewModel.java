package com.example.tournamentapp.ui.viewmodel; // Ajusta a tu paquete

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.data.repository.EquipoRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EquiposViewModel extends AndroidViewModel {

    private EquipoRepository repository;
    private MutableLiveData<List<EquipoResponse>> equiposData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> equipoCreado = new MutableLiveData<>();

    public EquiposViewModel(@NonNull Application application) {
        super(application);
        repository = new EquipoRepository(application); // Instanciamos el repositorio
    }

    public LiveData<List<EquipoResponse>> getEquiposData() { return equiposData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<Boolean> getEquipoCreado() { return equipoCreado; }

    public void cargarEquipos() {
        repository.getMisEquipos(new Callback<List<EquipoResponse>>() {
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

    public void crearNuevoEquipo(String nombre, Uri imagenUri) {
        File archivoOriginal = null;
        if (imagenUri != null) {
            archivoOriginal = uriToFile(imagenUri);
        }

        repository.crearEquipo(nombre, archivoOriginal, new Callback<EquipoResponse>() {
            @Override
            public void onResponse(Call<EquipoResponse> call, Response<EquipoResponse> response) {
                if (response.isSuccessful()) {
                    equipoCreado.postValue(true);
                    cargarEquipos();
                } else {
                    errorMsg.postValue("Error al crear el equipo");
                }
            }

            @Override
            public void onFailure(Call<EquipoResponse> call, Throwable t) {
                errorMsg.postValue("Error de red: " + t.getMessage());
            }
        });
    }

    // --- UTILIDAD: Convertir URI a File ---
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("logo_tmp", ".jpg", getApplication().getCacheDir());
            FileOutputStream outputStream = new FileOutputStream(tempFile);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}