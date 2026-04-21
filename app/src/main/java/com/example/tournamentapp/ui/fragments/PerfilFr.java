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
import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.utils.SessionManager;
import com.example.tournamentapp.databinding.FragmentPerfilBinding;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
import com.example.tournamentapp.ui.dialogs.EditarPerfilDialog;
import com.example.tournamentapp.ui.viewmodel.PerfilViewModel;

public class PerfilFr extends Fragment {
    private FragmentPerfilBinding binding;
    private PerfilViewModel viewModel;
    private int userIdRecibido = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            userIdRecibido = getArguments().getInt("userId",0);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        binding.rvEquiposPerfil.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTorneosPerfil.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        setupObservers();
        setupClickListeners();
        viewModel.cargarDatos(userIdRecibido);
    }

    private void setupObservers() {
        viewModel.getPerfilData().observe(getViewLifecycleOwner(), p -> {

            // Lógica de visibilidad del botón ajustes
            SessionManager sm = new SessionManager(requireContext());
            int miId = sm.getUserId();

            // Si el perfil que veo NO es el mío, oculto el botón de ajustes
            if (userIdRecibido != 0 && userIdRecibido != miId) {
                binding.btnAjustes.setVisibility(View.GONE);
            } else {
                binding.btnAjustes.setVisibility(View.VISIBLE);
            }
            binding.tvNombre.setText(p.nombre+" "+p.apellido);
            binding.tvUsername.setText("@" + p.username);
            binding.tvGoles.setText("⚽ " + p.stats.goles + " Goles");
            binding.tvFaltas.setText("🟨 " + p.stats.faltas + " Faltas");

            // ¡ACTUALIZADO AL NUEVO NOMBRE DE VARIABLE!
            String url = "http://130.61.180.130:5000/uploads/perfiles/" + p.imagen;
            Glide.with(this).load(url).circleCrop().placeholder(android.R.drawable.ic_menu_myplaces).into(binding.ivPerfilFoto);

            binding.rvEquiposPerfil.setAdapter(new PerfilAdapter(p.equipos,"equipos"));
            binding.rvTorneosPerfil.setAdapter(new PerfilAdapter(p.torneos,"torneos"));
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), msg -> Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show());

        viewModel.getPerfilActualizado().observe(getViewLifecycleOwner(), actualizado -> {
            if (actualizado) {
                Toast.makeText(getContext(), "Perfil actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        binding.btnAjustes.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);

            // Añadimos las opciones
            popup.getMenu().add(0, 1, 0, "Editar Info.");
            popup.getMenu().add(0, 2, 0, "Cerrar Sesión");

            // ¡UN SOLO LISTENER PARA TODO EL MENÚ!
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    mostrarDialogoEditar();
                    return true;
                } else if (item.getItemId() == 2) {
                    new SessionManager(requireContext()).logout();
                    Navigation.findNavController(v).navigate(R.id.action_global_loginFr);
                    return true;
                }
                return false;
            });

            popup.show();
        });
    }

    private void mostrarDialogoEditar() {
        PerfilResponse p = viewModel.getPerfilData().getValue();
        if (p == null) return;

        EditarPerfilDialog dialog = EditarPerfilDialog.newInstance(p.nombre, p.apellido, p.imagen);
        dialog.setListener((nombre, apellido, uri) -> {
            viewModel.actualizarPerfil(nombre, apellido, uri);
        });
        dialog.show(getChildFragmentManager(), "EditarPerfil");
    }
}