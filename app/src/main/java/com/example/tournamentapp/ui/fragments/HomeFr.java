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
import com.example.tournamentapp.databinding.FragmentHomeBinding;
import com.example.tournamentapp.ui.adapter.PartidosAdapter;
import com.example.tournamentapp.ui.viewmodel.HomeViewModel;

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
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding.rvPartidos.setLayoutManager(new LinearLayoutManager(getContext()));

        setupObservers();
        viewModel.cargarMisPartidos();
    }

    private void setupObservers() {
        viewModel.getPartidosLiveData().observe(getViewLifecycleOwner(), partidos -> {

            // LÓGICA DE EMPTY STATE
            if (partidos == null || partidos.isEmpty()) {
                binding.rvPartidos.setVisibility(View.GONE);
                binding.tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                binding.rvPartidos.setVisibility(View.VISIBLE);
                binding.tvEmptyState.setVisibility(View.GONE);

                PartidosAdapter adapter = new PartidosAdapter(partidos);
                binding.rvPartidos.setAdapter(adapter);
            }
        });

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