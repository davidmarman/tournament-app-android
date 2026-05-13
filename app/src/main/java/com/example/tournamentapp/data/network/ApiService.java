package com.example.tournamentapp.data.network;

import com.example.tournamentapp.data.model.ActaResponse;
import com.example.tournamentapp.data.model.AdminDashboardResponse;
import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.data.model.FinalizarPartidoRequest;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.data.model.LoginRequest;
import com.example.tournamentapp.data.model.LoginResponse;
import com.example.tournamentapp.data.model.PartidoItem;
import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.model.RegisterResponse;
import com.example.tournamentapp.data.model.TorneoDetalleResponse;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // Ruta para realizar Login
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    //Ruta para el registro de usuarios
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
    Call<List<PartidoItem>> getMisPartidos(@Header("Authorization") String token);

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
            @Path("id") int torneoId,
            @Query("jornada") Integer jornada
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

    // Ruta para eliminar un jugador de un equipo
    @DELETE("equipos/{id_equipo}/expulsar/{id_jugador}")
    Call<Map<String, String>> expulsarJugador(
            @Header("Authorization") String token,
            @Path("id_equipo") int idEquipo,
            @Path("id_jugador") int idJugador
    );

    // Obtener perfil por ID (si id es 0 o negativo, el servidor debería devolver el propio)
    @GET("usuario/perfil/{id}")
    Call<PerfilResponse> getPerfilAjeno(
            @Header("Authorization") String token,
            @Path("id") int userId
    );

    // Ruta para salir de un equipo
    @DELETE("equipos/{id_equipo}/salir")
    Call<Map<String, String>> salirEquipo(
            @Header("Authorization") String token,
            @Path("id_equipo") int idEquipo
    );

    // Ruta para eliminar un equipo, solo si eres capitan
    @DELETE("equipos/{id_equipo}/disolver")
    Call<Map<String, String>> disolverEquipo(
            @Header("Authorization") String token,
            @Path("id_equipo") int idEquipo
    );

    // Ruta para editar los equipos
    @Multipart
    @PUT("equipos/{id_equipo}/editar")
    Call<Map<String, String>> editarEquipo(
            @Header("Authorization") String token,
            @Path("id_equipo") int idEquipo,
            @Part("nombre") RequestBody nombre,
            @Part MultipartBody.Part logo // Puede ser null
    );

    // Ruta para crear torneos
    @Multipart
    @POST("torneos/crear")
    Call<Map<String, Object>> crearTorneo(
            @Header("Authorization") String token,
            @Part("nombre") okhttp3.RequestBody nombre,
            @Part("tipo") okhttp3.RequestBody tipo,
            @Part("descripcion") okhttp3.RequestBody descripcion,
            @Part("fecha_inicio") okhttp3.RequestBody fechaInicio,
            @Part("dias_juego") okhttp3.RequestBody diasJuego,
            @Part("horarios_juego") okhttp3.RequestBody horariosJuego,
            @Part okhttp3.MultipartBody.Part logo
    );

    // Ruta para el Dashboard del Administrador
    @GET("torneos/admin-dashboard")
    Call<AdminDashboardResponse> getAdminDashboard(@Header("Authorization") String token);

    // Ruta para eliminar torneos
    @DELETE("torneos/{id}")
    Call<Map<String, String>> eliminarTorneo(
            @Header("Authorization") String token,
            @Path("id") int idTorneo
    );

    // Ruta para generar el calendario de un torneo.
    @POST("torneos/{id}/generar-calendario")
    Call<Map<String, String>> generarCalendario(
            @Header("Authorization") String token,
            @Path("id") int idTorneo
    );

    // Ruta para obtener el acta vacía de un partido
    @GET("partidos/{id}/acta")
    Call<ActaResponse> getActaPartido(
            @Header("Authorization") String token,
            @Path("id") int idPartido
    );

    // Ruta para finalizar el partido y enviar resultados
    @POST("partidos/{id}/finalizar")
    Call<Map<String, String>> finalizarPartido(
            @Header("Authorization") String token,
            @Path("id") int idPartido,
            @Body FinalizarPartidoRequest request
    );

    // Ruta para finalizar el torneo y repartir premios
    @POST("torneos/{id}/finalizar")
    Call<Map<String, String>> finalizarTorneo(
            @Header("Authorization") String token,
            @Path("id") int idTorneo
    );

    // Ruta para ceder la capitania de un equipo
    @POST("equipos/{id}/ceder-capitania")
    Call<Map<String, String>> cederCapitania(
            @Header("Authorization") String token,
            @Path("id") int idEquipo,
            @Body Map<String, Integer> body
    );
}
