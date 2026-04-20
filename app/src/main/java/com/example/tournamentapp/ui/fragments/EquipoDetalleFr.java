package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.FragmentEquipoDetalleBinding;
import com.example.tournamentapp.ui.adapter.JugadoresAdapter;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
import com.example.tournamentapp.ui.dialogs.AnadirJugadorDialog;
import com.example.tournamentapp.ui.viewmodel.EquipoDetalleViewModel;

public class EquipoDetalleFr extends Fragment {
    private FragmentEquipoDetalleBinding binding;
    private EquipoDetalleViewModel viewModel;
    private int equipoId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Recuperamos el ID que enviamos desde la lista de equipos
        if (getArguments() != null) {
            equipoId = getArguments().getInt("equipoId");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEquipoDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(EquipoDetalleViewModel.class);

        setupObservers();
        viewModel.cargarDetalle(equipoId);
    }

    private void setupObservers() {
        // 1. Observar mensajes de éxito (¡Primero los mensajes rápidos!)
        viewModel.getExitoAnadirMsg().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        // 2. Observar los datos del equipo (¡Todo en un solo bloque!)
        viewModel.getEquipoData().observe(getViewLifecycleOwner(), equipo -> {

            binding.tvDetalleNombre.setText(equipo.nombre);

            // Cargar Logo Equipo
            String urlLogo = "http://130.61.180.130:5000/uploads/equipos/" + equipo.logo;
            Glide.with(this).load(urlLogo).into(binding.ivDetalleLogo);

            // Próximo Partido
            if (equipo.proximo_partido != null) {
                binding.cardProximoPartido.setVisibility(View.VISIBLE);
                binding.tvRivalNombre.setText("vs " + equipo.proximo_partido.rival_nombre);
                binding.tvTorneoFecha.setText(equipo.proximo_partido.torneo_nombre + "\n" + equipo.proximo_partido.fecha);

                String urlRival = "http://130.61.180.130:5000/uploads/equipos/" + equipo.proximo_partido.rival_logo;
                Glide.with(this).load(urlRival).into(binding.ivRivalLogo);
            } else {
                binding.cardProximoPartido.setVisibility(View.GONE);
            }

            // Torneos
            binding.rvTorneosInscrito.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvTorneosInscrito.setAdapter(new PerfilAdapter(equipo.torneos, "torneos"));

            // Jugadores
            if (equipo.jugadores != null) {
                binding.rvJugadores.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                // Creamos EL ÚNICO adaptador
                JugadoresAdapter jugadoresAdapter = new JugadoresAdapter(
                        equipo.jugadores,
                        equipo.es_capitan,
                        new JugadoresAdapter.OnJugadorClickListener() {
                            @Override
                            public void onJugadorClick(ItemSimple jugador) {
                                Toast.makeText(getContext(), "Viendo perfil de: " + jugador.nombre, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAnadirJugadorClick() {
                                // ¡AQUÍ ESTÁ LA MAGIA CORREGIDA! Abrimos el diálogo directamente
                                AnadirJugadorDialog dialog = new AnadirJugadorDialog();
                                dialog.setListener(username -> {
                                    // Llamamos al ViewModel para añadir al usuario
                                    viewModel.anadirJugador(equipoId, username);
                                });
                                dialog.show(getChildFragmentManager(), "AnadirJugador");
                            }
                        }
                );

                binding.rvJugadores.setAdapter(jugadoresAdapter);
            }
        });
    }
}