package com.example.tournamentapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.network.RetrofitClient;
import com.example.tournamentapp.databinding.ActivityMainBinding;
import com.example.tournamentapp.data.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
        );

        // Cargamos el tema de la aplicacion
        SharedPreferences prefs = getSharedPreferences("GlowAppPrefs", Context.MODE_PRIVATE);
        String temaElegido = prefs.getString("tema_color", "BLUE"); // Por defecto Azul

        switch (temaElegido) {
            case "CYAN":
                setTheme(R.style.Theme_TournamentApp_Cyan);
                break;
            case "GREEN":
                setTheme(R.style.Theme_TournamentApp_Green);
                break;
            default:
                setTheme(R.style.Theme_TournamentApp_Blue);
                break;
        }

        // Inicializamos las vistas nativas
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Lógica de Token de session
        String token = sessionManager.fetchAuthToken();
        if (token != null) {
            RetrofitClient.getApiService().getPerfil("Bearer " + token).enqueue(new Callback<PerfilResponse>() {
                @Override
                public void onResponse(Call<PerfilResponse> call, Response<PerfilResponse> response) {
                    if (!response.isSuccessful()) {
                        sessionManager.logout();
                        navController.navigate(R.id.loginFr);
                    } else {
                        String rol = sessionManager.getUserRole();
                        int startDestination = "Admin".equals(rol) ? R.id.adminTorneosFr : R.id.homeFr;

                        navController.getGraph().setStartDestination(startDestination);
                        navController.navigate(startDestination);
                    }
                }

                @Override
                public void onFailure(Call<PerfilResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Sin conexión con el servidor", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Vincula el menú inicial por defecto
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // LISTENER DE NAVEGACIÓN
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFr || destination.getId() == R.id.registerFr) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);

                String rol = sessionManager.getUserRole();
                if ("Admin".equals(rol)) {
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else {
                    if (binding.bottomNavigation.getMenu().findItem(R.id.homeFr) == null) {
                        binding.bottomNavigation.getMenu().clear();
                        binding.bottomNavigation.inflateMenu(R.menu.bottom_nav_menu);
                    }
                }
            }
        });

        // INTERCEPTOR DEL BOTÓN ATRÁS
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int currentId = navController.getCurrentDestination() != null ?
                        navController.getCurrentDestination().getId() : 0;

                if (currentId == R.id.homeFr || currentId == R.id.equipoFr ||
                        currentId == R.id.torneosFr ||
                        currentId == R.id.adminTorneosFr) {
                    finish();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // METODO PARA CAMBIAR EL COLOR DE ÉNFASIS DESDE CUALQUIER FRAGMENTO
    public void cambiarTemaDinamico(String nuevoTema) {
        SharedPreferences prefs = getSharedPreferences("GlowAppPrefs", Context.MODE_PRIVATE);
        prefs.edit().putString("tema_color", nuevoTema).apply();

        // Recreamos la actividad principal para aplicar la metamorfosis estética al instante
        recreate();
    }
}