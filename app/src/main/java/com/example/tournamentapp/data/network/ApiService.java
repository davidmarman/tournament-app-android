package com.example.tournamentapp.data.network;

import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;
import com.example.tournamentapp.data.model.Partido;
import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.model.RegisterRequest;
import com.example.tournamentapp.data.model.RegisterResponse;
import com.example.tournamentapp.data.model.TorneoDetalleResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface ApiService {

    // Ruta para realizar Login
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

    // Ruta para obtener informacion del usuario
    @GET("usuario/perfil")
    Call<PerfilResponse> getPerfil(@Header("Authorization") String token);

    // Ruta para obtener los equipos del usuario
    @GET("equipos/mis-equipos")
    Call<List<EquipoResponse>> getMisEquipos(@Header("Authorization") String token);

    // Ruta para obtener el detalle de un equipo
    @GET("equipos/{id}")
    Call<EquipoDetalleResponse> getDetalleEquipo(
            @Header("Authorization") String token,
            @Path("id") int equipoId
    );


    // Ruta para crear un equipo
    @Multipart
    @POST("equipos/crear")
    Call<EquipoResponse> crearEquipo(
            @Header("Authorization") String token,
            @Part("nombre") RequestBody nombre,
            @Part MultipartBody.Part logo // La imagen (puede ser nula)
    );

    // Ruta para añadir un jugador a un equipo
    @POST("equipos/{id}/anadir-jugador")
    Call<Map<String, String>> anadirJugador(
            @Header("Authorization") String token,
            @Path("id") int equipoId,
            @Body Map<String, String> body // Enviamos {"username": "valor"}
    );

    // Ruta para obtener los torneos en los que participa el usuario
    @GET("torneos/mis-torneos")
    Call<List<ItemSimple>> getMisTorneos(@Header("Authorization") String token);

    // Ruta para inscribir un equipo a un torneo (solo si eres capitan)
    @POST("torneos/inscribir")
    Call<Map<String, String>> inscribirTorneo(
            @Header("Authorization") String token,
            @Body Map<String, Object> body // Enviamos {"codigo_acceso": "X", "id_equipo": 1}
    );

    // Ruta para obtener el detalle completo de un torneo (Info, Clasificación y Partidos)
    @GET("torneos/{id}/detalle")
    Call<TorneoDetalleResponse> getDetalleTorneo(
            @Header("Authorization") String token,
            @Path("id") int torneoId
    );

    // Ruta para editar informacion del usuario
    @Multipart
    @PUT("usuario/editar")
    Call<Map<String, Object>> editarPerfil(
            @Header("Authorization") String token,
            @Part("nombre") RequestBody nombre,
            @Part("apellido") RequestBody apellido,
            @Part MultipartBody.Part imagen // Puede ser null
    );
}
