package com.example.tournamentapp.ui.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.repository.PerfilRepository;

// VIEWMODEL
public class PerfilViewModel extends AndroidViewModel {
    private PerfilRepository repository;
    private MutableLiveData<PerfilResponse> perfilData = new MutableLiveData<>();
    private MutableLiveData<String> errorMsg = new MutableLiveData<>();

    public PerfilViewModel(@NonNull Application application) {
        super(application);
        repository = new PerfilRepository(application);
    }

    public LiveData<PerfilResponse> getPerfilData() { return perfilData; }
    public LiveData<String> getErrorMsg() { return errorMsg; }
    public void cargarDatos() { repository.fetchPerfil(perfilData, errorMsg); }
}
