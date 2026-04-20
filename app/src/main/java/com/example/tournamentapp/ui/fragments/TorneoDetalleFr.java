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
import com.example.tournamentapp.databinding.FragmentTorneoDetalleBinding;
import com.example.tournamentapp.ui.adapter.ClasificacionAdapter;
import com.example.tournamentapp.ui.adapter.PartidosJornadaAdapter;
import com.example.tournamentapp.ui.viewmodel.TorneoDetalleViewModel;

public class TorneoDetalleFr extends Fragment {
    private FragmentTorneoDetalleBinding binding;
    private TorneoDetalleViewModel viewModel;
    private int torneoId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) torneoId = getArguments().getInt("torneoId");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTorneoDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TorneoDetalleViewModel.class);

        binding.rvClasificacion.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPartidosJornada.setLayoutManager(new LinearLayoutManager(getContext()));

        setupObservers();
        viewModel.cargarDetalle(torneoId);
    }

    private void setupObservers() {
        viewModel.getTorneoDetalle().observe(getViewLifecycleOwner(), response -> {
            // 1. Cabecera
            binding.tvDetalleTorneoNombre.setText(response.info.nombre);
            binding.tvDetalleTorneoDesc.setText(response.info.descripcion);
            String urlLogo = "http://130.61.180.130:5000/uploads/torneos/" + response.info.logo;
            Glide.with(this).load(urlLogo).into(binding.ivDetalleTorneoLogo);

            // 2. Clasificación
            binding.rvClasificacion.setAdapter(new ClasificacionAdapter(response.clasificacion));

            // 3. Partidos
            binding.tvTituloJornada.setText("Jornada " + response.jornada_actual + ":");
            binding.rvPartidosJornada.setAdapter(new PartidosJornadaAdapter(response.partidos));
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
    }
}