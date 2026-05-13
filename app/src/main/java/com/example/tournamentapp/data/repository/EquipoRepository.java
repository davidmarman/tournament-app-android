package com.example.tournamentapp.data.repository;

import android.app.Application;

import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.data.network.ApiService;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.data.utils.SessionManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Callback;

public class EquipoRepository {

    private ApiService apiService;
    private SessionManager sessionManager;

    public EquipoRepository(Application application) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(application);
    }

    public void getMisEquipos(Callback<List<EquipoResponse>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getMisEquipos(token).enqueue(callback);
    }

    public void crearEquipo(String nombre, File logoFile, Callback<EquipoResponse> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();

        RequestBody nombreBody = RequestBody.create(MediaType.parse("text/plain"), nombre);
        MultipartBody.Part logoPart = null;

        if (logoFile != null) {
            RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), logoFile);
            logoPart = MultipartBody.Part.createFormData("logo", logoFile.getName(), fileBody);
        }

        apiService.crearEquipo(token, nombreBody, logoPart).enqueue(callback);
    }

    public void getDetalleEquipo(int id, Callback<EquipoDetalleResponse> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getDetalleEquipo(token, id).enqueue(callback);
    }

    public void anadirJugador(int equipoId, String username, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        Map<String, String> body = new HashMap<>();
        body.put("username", username);

        apiService.anadirJugador(token, equipoId, body).enqueue(callback);
    }

    public void expulsarJugador(int idEquipo, int idJugador, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.expulsarJugador(token, idEquipo, idJugador).enqueue(callback);
    }

    public void salirEquipo(int idEquipo, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.salirEquipo(token, idEquipo).enqueue(callback);
    }

    public void disolverEquipo(int idEquipo, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.disolverEquipo(token, idEquipo).enqueue(callback);
    }

    public void editarEquipo(int idEquipo, String nombre, java.io.File imageFile, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();

        okhttp3.RequestBody rbNombre = okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), nombre);

        okhttp3.MultipartBody.Part imagePart = null;
        if (imageFile != null) {
            okhttp3.RequestBody rbFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), imageFile);
            imagePart = okhttp3.MultipartBody.Part.createFormData("logo", imageFile.getName(), rbFile);
        }

        apiService.editarEquipo(token, idEquipo, rbNombre, imagePart).enqueue(callback);
    }

    public void cederCapitania(int idEquipo, Map<String, Integer> body, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.cederCapitania(token, idEquipo, body).enqueue(callback);
    }
}