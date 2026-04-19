package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.databinding.FragmentEquipoDetalleBinding;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
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
        viewModel.getEquipoData().observe(getViewLifecycleOwner(), equipo -> {
            binding.tvDetalleNombre.setText(equipo.nombre);

            // Cargar Logo Equipo
            String urlLogo = "http://130.61.180.130:5000/uploads/equipos/" + equipo.logo;
            Glide.with(this).load(urlLogo).into(binding.ivDetalleLogo);

            // Lógica del Botón de Capitán
            binding.btnAnadirJugador.setVisibility(equipo.es_capitan ? View.VISIBLE : View.GONE);

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
            binding.rvTorneosInscrito.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvTorneosInscrito.setAdapter(new PerfilAdapter(equipo.torneos, "torneos"));
        });
    }
}