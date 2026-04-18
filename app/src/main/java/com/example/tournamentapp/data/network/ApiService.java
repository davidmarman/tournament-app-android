package com.example.tournamentapp.data.network;

import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;
import com.example.tournamentapp.data.model.RegisterRequest;
import com.example.tournamentapp.data.model.RegisterResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    //Ruta para el registro de usuarios
    @POST("auth/register")
    Call<RegisterResponse> register(@Body RegisterRequest request);
}
