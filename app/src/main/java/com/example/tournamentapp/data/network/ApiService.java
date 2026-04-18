package com.example.tournamentapp.data.network;

import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;
import com.example.tournamentapp.data.model.Partido;
import com.example.tournamentapp.data.model.RegisterRequest;
import com.example.tournamentapp.data.model.RegisterResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    //Ruta para el registro de usuarios
    // Fíjate que usamos @Multipart en lugar de pasar un objeto en el @Body
    @Multipart
    @POST("auth/register")
    Call<RegisterResponse> registerWithImage(
            @Part("nombre") RequestBody nombre,
            @Part("apellido") RequestBody apellido,
            @Part("username") RequestBody username,
            @Part("email") RequestBody email,
            @Part("password") RequestBody password,
            @Part("rol") RequestBody rol,
            @Part MultipartBody.Part imagen // <-- Esta es la caja especial para la foto
    );

    // Obtener lista de proximos partidos
    @GET("partidos/mis-proximos")
    Call<List<Partido>> getMisPartidos(@Header("Authorization") String token);
}
