package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.utils.SessionManager;
import com.example.tournamentapp.databinding.FragmentPerfilBinding;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
import com.example.tournamentapp.ui.viewmodel.PerfilViewModel;

public class PerfilFr extends Fragment {
    private FragmentPerfilBinding binding;
    private PerfilViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        // Configurar RecyclerViews
        binding.rvEquiposPerfil.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTorneosPerfil.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupObservers();
        setupClickListeners();
        viewModel.cargarDatos();
    }

    private void setupObservers() {
        viewModel.getPerfilData().observe(getViewLifecycleOwner(), p -> {
            binding.tvNombre.setText(p.nombre);
            binding.tvUsername.setText(p.username);
            binding.tvGoles.setText("⚽ " + p.stats.goles + " Goles");
            binding.tvFaltas.setText("🟨 " + p.stats.faltas + " Faltas");

            // Imagen con Glide
            String url = "http://130.61.180.130:5000/uploads/perfiles/" + p.imagen;
            Glide.with(this).load(url).circleCrop().placeholder(android.R.drawable.ic_menu_myplaces).into(binding.ivPerfilFoto);

            // Cargar Listas
            binding.rvEquiposPerfil.setAdapter(new PerfilAdapter(p.equipos,"equipos"));
            binding.rvTorneosPerfil.setAdapter(new PerfilAdapter(p.torneos,"torneos"));
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());
    }

    private void setupClickListeners() {
        binding.btnAjustes.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add(0, 1, 0, "Cerrar Sesión");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    new SessionManager(requireContext()).logout();
                    Navigation.findNavController(v).navigate(R.id.action_global_loginFr);
                }
                return true;
            });
            popup.show();
        });
    }
}