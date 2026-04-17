package com.example.tournamentapp.data.network;

import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Mas adelante añadiremos el POST("auth/register") y los de torneos
}
