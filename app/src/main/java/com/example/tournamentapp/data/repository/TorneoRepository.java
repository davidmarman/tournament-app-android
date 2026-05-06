package com.example.tournamentapp.data.repository;

import android.app.Application;

import com.example.tournamentapp.data.model.ItemSimple;
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

public class TorneoRepository {
    private ApiService apiService;
    private SessionManager sessionManager;

    public TorneoRepository(Application application) {
        apiService = RetrofitClient.getApiService();
        sessionManager = new SessionManager(application);
    }

    public void getMisTorneos(Callback<List<ItemSimple>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();
        apiService.getMisTorneos(token).enqueue(callback);
    }

    public void inscribirTorneo(String codigoAcceso, int idEquipo, Callback<Map<String, String>> callback) {
        String token = "Bearer " + sessionManager.fetchAuthToken();

        Map<String, Object> body = new HashMap<>();
        body.put("codigo_acceso", codigoAcceso);
        body.put("id_equipo", idEquipo);

        apiService.inscribirTorneo(token, body).enqueue(callback);
    }

    public void crearTorneo(String nombre, String tipo, String descripcion, String fechaInicio,
                            String diasJuego, String horariosJuego, File logoFile,
                            Callback<Map<String, Object>> callback) {

        String token = "Bearer " + sessionManager.fetchAuthToken();

        // Convertimos los textos a RequestBody
        RequestBody rbNombre = RequestBody.create(MediaType.parse("text/plain"), nombre);
        RequestBody rbTipo = RequestBody.create(MediaType.parse("text/plain"), tipo);
        RequestBody rbDesc = RequestBody.create(MediaType.parse("text/plain"), descripcion);
        RequestBody rbFecha = RequestBody.create(MediaType.parse("text/plain"), fechaInicio);
        RequestBody rbDias = RequestBody.create(MediaType.parse("text/plain"), diasJuego);
        RequestBody rbHorarios = RequestBody.create(MediaType.parse("text/plain"), horariosJuego);

        // Preparamos la imagen si existe
        MultipartBody.Part logoPart = null;
        if (logoFile != null) {
            RequestBody rbFile = RequestBody.create(MediaType.parse("image/*"), logoFile);
            logoPart = MultipartBody.Part.createFormData("logo", logoFile.getName(), rbFile);
        }

        apiService.crearTorneo(token, rbNombre, rbTipo, rbDesc, rbFecha, rbDias, rbHorarios, logoPart).enqueue(callback);
    }
}