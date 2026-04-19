package com.example.tournamentapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.tournamentapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Dentro de onCreate...
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Esto vincula la barra con el controlador de navegación
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);


        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFr || destination.getId() == R.id.registerFr) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);
            }
        });
        // INTERCEPTOR DEL BOTÓN ATRÁS
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Miramos en qué pantalla estamos actualmente
                int currentId = navController.getCurrentDestination().getId();

                // Si estamos en CUALQUIERA de las 4 pestañas principales...
                if (currentId == R.id.homeFr || currentId == R.id.equipoFr ||
                        currentId == R.id.torneosFr || currentId == R.id.perfilFr) {

                    finish(); // ¡Salimos de la aplicación de inmediato!

                } else {
                    // Si estamos en un sub-menú, apagamos nuestro interceptor un segundo,
                    // dejamos que Android vuelva a la pantalla anterior, y lo volvemos a encender.
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };

        // Añadimos el interceptor a la actividad
        getOnBackPressedDispatcher().addCallback(this,callback);
    }

}