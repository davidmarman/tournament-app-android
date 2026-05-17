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
import com.example.tournamentapp.data.model.PalmaresItem;
import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.data.utils.SessionManager;
import com.example.tournamentapp.databinding.FragmentPerfilBinding;
import com.example.tournamentapp.ui.adapter.PalmaresAdapter;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
import com.example.tournamentapp.ui.dialogs.EditarPerfilDialog;
import com.example.tournamentapp.ui.viewmodel.PerfilViewModel;

import java.util.ArrayList;
import java.util.List;

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

            SessionManager sm = new SessionManager(requireContext());
            int miId = sm.getUserId();
            String miRol = sm.getUserRole(); // ¡Leemos el rol!

            // Lógica de visibilidad del botón ajustes
            if (userIdRecibido != 0 && userIdRecibido != miId) {
                binding.btnAjustes.setVisibility(View.GONE);
            } else {
                binding.btnAjustes.setVisibility(View.VISIBLE);
            }

            // --- LÓGICA DE LIMPIEZA PARA EL ADMINISTRADOR ---
            if ("Admin".equals(miRol) && (userIdRecibido == 0 || userIdRecibido == miId)) {
                // Ocultamos las 3 tarjetas enteras
                binding.cvEquipos.setVisibility(View.GONE);
                binding.cvTorneos.setVisibility(View.GONE);
                binding.cvEstadisticas.setVisibility(View.GONE);

                // Le ponemos un distintivo de Admin
                binding.tvUsername.setText("Admin | @" + p.username);
            } else {
                binding.tvUsername.setText("@" + p.username);
            }

            binding.tvNombre.setText(p.nombre+" "+p.apellido);
            binding.tvGoles.setText("⚽ " + p.stats.goles + " Goles");
            binding.tvAmarillas.setText("🟨 " + p.stats.amarillas);
            binding.tvRojas.setText("🟥 " + p.stats.rojas);

            String url = "http://130.61.180.130:5000/uploads/perfiles/" + p.imagen;
            Glide.with(this).load(url).circleCrop().placeholder(android.R.drawable.ic_menu_myplaces).into(binding.ivPerfilFoto);

            binding.rvEquiposPerfil.setAdapter(new PerfilAdapter(p.equipos,"equipos", p.id));
            binding.rvTorneosPerfil.setAdapter(new PerfilAdapter(p.torneos,"torneos", p.id));

            // Palmares
            if (p.palmares != null && !p.palmares.isEmpty()) {
                List<PalmaresItem> individuales = new ArrayList<>();
                List<PalmaresItem> equiposLogros = new ArrayList<>();

                // Separamos según el booleano que envía Flask
                for (PalmaresItem palmaresItem : p.palmares) {
                    if (palmaresItem.es_individual) {
                        individuales.add(palmaresItem);
                    } else {
                        equiposLogros.add(palmaresItem);
                    }
                }

                // Configurar Vitrina de Equipos
                if (!equiposLogros.isEmpty()) {
                    binding.cvPalmaresEquipo.setVisibility(View.VISIBLE);
                    binding.rvPalmaresEquipo.setAdapter(new PalmaresAdapter(equiposLogros));
                }

                // Configurar Logros Individuales
                if (!individuales.isEmpty()) {
                    binding.cvPalmaresIndividual.setVisibility(View.VISIBLE);
                    binding.rvPalmaresIndividual.setAdapter(new PalmaresAdapter(individuales));
                }
            }
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
            // Envolvemos el contexto para forzar que el Popup sea oscuro y use tus colores
            android.view.ContextThemeWrapper wrapper = new android.view.ContextThemeWrapper(
                    requireContext(),
                    R.style.Base_Theme_TournamentApp
            );

            PopupMenu popup = new PopupMenu(wrapper, v); // Usamos el wrapper en lugar de requireContext()

            popup.getMenu().add(0, 1, 0, "Editar Info.");
            popup.getMenu().add(0, 2, 0, "Cerrar Sesión");
            popup.getMenu().add(0, 3, 0, "Tema: Azul Glow");
            popup.getMenu().add(0, 4, 0, "Tema: Cian Glow");
            popup.getMenu().add(0, 5, 0, "Tema: Verde Glow");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    mostrarDialogoEditar();
                    return true;
                } else if (item.getItemId() == 2) {
                    new SessionManager(requireContext()).logout();
                    Navigation.findNavController(requireView()).navigate(
                            R.id.action_global_loginFr,
                            null,
                            new androidx.navigation.NavOptions.Builder()
                                    .setPopUpTo(R.id.nav_graph,true)
                                    .build());
                    return true;
                }
                // CONTROL DE CAMBIO DE TEMAS DESDE EL POPUP
                else if (item.getItemId() == 3) {
                    if (getActivity() instanceof com.example.tournamentapp.MainActivity) {
                        ((com.example.tournamentapp.MainActivity) getActivity()).cambiarTemaDinamico("BLUE");
                    }
                    return true;
                } else if (item.getItemId() == 4) {
                    if (getActivity() instanceof com.example.tournamentapp.MainActivity) {
                        ((com.example.tournamentapp.MainActivity) getActivity()).cambiarTemaDinamico("CYAN");
                    }
                    return true;
                } else if (item.getItemId() == 5) {
                    if (getActivity() instanceof com.example.tournamentapp.MainActivity) {
                        ((com.example.tournamentapp.MainActivity) getActivity()).cambiarTemaDinamico("GREEN");
                    }
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