package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.Partido;
import com.example.tournamentapp.databinding.FragmentHomeBinding;
import com.example.tournamentapp.ui.adapter.PartidosAdapter;
import com.example.tournamentapp.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFr extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializamos el ViewModel
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // 2. Preparamos el RecyclerView (vacío por ahora)
        binding.rvPartidos.setLayoutManager(new LinearLayoutManager(getContext()));

        // 3. Empezamos a observar los datos
        setupObservers();

        // 4. Le decimos al ViewModel que arranque a buscar datos en el servidor
        viewModel.cargarMisPartidos();
    }

    private void setupObservers() {
        // Cuando lleguen los partidos de la base de datos...
        viewModel.getPartidosLiveData().observe(getViewLifecycleOwner(), partidos -> {
            // ... creamos el adapter con los datos reales y lo enchufamos al RecyclerView
            PartidosAdapter adapter = new PartidosAdapter(partidos);
            binding.rvPartidos.setAdapter(adapter);
        });

        // Si ocurre algún error en la red o en el servidor...
        viewModel.getErrorLiveData().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}