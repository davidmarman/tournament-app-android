package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.repository.PerfilRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilViewModel extends AndroidViewModel {
    private PerfilRepository repository;
    private MutableLiveData<PerfilResponse> perfilData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();
    private MutableLiveData<Boolean> perfilActualizado = new MutableLiveData<>();

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        repository = new PerfilRepository(application);
    }

    public LiveData<PerfilResponse> getPerfilData() { return perfilData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public LiveData<Boolean> getPerfilActualizado() { return perfilActualizado; }

    // ¡CORREGIDO! Ya no intentamos sacar el token aquí, el Repositorio se encarga.
    public void cargarDatos(int userId) {
        repository.fetchPerfil(userId, perfilData, errorMsg);
    }

    public void actualizarPerfil(String nombre, String apellido, Uri imageUri) {
        File file = null;
        if (imageUri != null) {
            file = uriToFile(imageUri);
        }

        repository.editarPerfil(nombre, apellido, file, new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    perfilActualizado.postValue(true);
                    cargarDatos(0); // ¡CORREGIDO! Le pasamos el 0 para recargar TU perfil tras editar.
                } else {
                    errorMsg.postValue("Error al actualizar perfil");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                errorMsg.postValue("Fallo de conexión");
            }
        });
    }

    // --- UTILIDAD: Convertir URI a File físico para enviarlo al servidor ---
    private File uriToFile(Uri uri) {
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(uri);
            File tempFile = File.createTempFile("avatar_tmp", ".jpg", getApplication().getCacheDir());
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