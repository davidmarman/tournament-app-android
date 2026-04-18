package com.example.tournamentapp.data.network;

import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;
import com.example.tournamentapp.data.model.Partido;
import com.example.tournamentapp.data.model.RegisterRequest;
import com.example.tournamentapp.data.model.RegisterResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    //Ruta para el registro de usuarios
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // Obtener lista de proximos partidos
    @GET("partidos/mis-proximos")
    Call<List<Partido>> getMisPartidos(@Header("Authorization") String token);
}
