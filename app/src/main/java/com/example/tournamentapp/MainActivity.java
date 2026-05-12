package com.example.tournamentapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.tournamentapp.databinding.ActivityMainBinding;
import com.example.tournamentapp.data.utils.SessionManager; // <-- ¡Añadido para leer el rol!

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        // Vincula el menú inicial por defecto
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);

        // LISTENER DE NAVEGACIÓN
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.loginFr || destination.getId() == R.id.registerFr) {
                binding.bottomNavigation.setVisibility(View.GONE);
            } else {
                binding.bottomNavigation.setVisibility(View.VISIBLE);

                // --- MAGIA DEL ENRUTAMIENTO POR ROLES ---
                String rol = sessionManager.getUserRole();

                if ("Admin".equals(rol)) {
                    // Si el menú actual es el de Admin lo ocultamos
                    binding.bottomNavigation.setVisibility(View.GONE);
                } else {
                    // Si el menú actual NO es el de usuario normal, lo cambiamos
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

                    finish(); // Salimos de la aplicación de inmediato

                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);
    }
}