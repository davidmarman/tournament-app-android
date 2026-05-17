package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.data.utils.SessionManager;
import com.example.tournamentapp.databinding.FragmentAdminTorneosBinding;
import com.example.tournamentapp.ui.adapter.PartidosAdapter;
import com.example.tournamentapp.ui.adapter.TorneosAdapter;
import com.example.tournamentapp.ui.dialogs.EditarPerfilDialog;
import com.example.tournamentapp.ui.viewmodel.AdminViewModel;
import com.example.tournamentapp.ui.viewmodel.PerfilViewModel;

public class AdminTorneosFr extends Fragment {

    private FragmentAdminTorneosBinding binding;
    private AdminViewModel viewModel;

    // Instanciamos también el PerfilViewModel para poder aprovechar su función de editar perfil
    private PerfilViewModel perfilViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminTorneosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        perfilViewModel = new ViewModelProvider(this).get(PerfilViewModel.class);

        // Configuramos las listas
        binding.rvAdminTorneos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAdminPartidos.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        setupObservers();
        setupClickListeners();

        // Pedimos al servidor que cargue todo
        viewModel.cargarTodo();
    }

    private void setupObservers() {
        // 1. PINTAR CABECERA DEL PERFIL
        viewModel.getPerfilData().observe(getViewLifecycleOwner(), p -> {
            binding.tvAdminNombre.setText(p.nombre + " " + p.apellido);
            binding.tvAdminUsername.setText("Admin | @" + p.username);
            String url = "http://130.61.180.130:5000/uploads/perfiles/" + p.imagen;
            Glide.with(this).load(url).circleCrop().placeholder(android.R.drawable.ic_menu_myplaces).into(binding.ivAdminFoto);
        });

        // 2. PINTAR LISTAS DEL DASHBOARD
        viewModel.getDashboardData().observe(getViewLifecycleOwner(), dashboard -> {

            // Adaptador de Torneos (isAdmin = true)
            TorneosAdapter torneosAdapter = new TorneosAdapter(dashboard.torneos, true, new TorneosAdapter.OnTorneoClickListener() {
                @Override
                public void onTorneoClick(ItemSimple torneo) {
                    // Al pulsar un torneo, vamos al detalle pasándole el ID (Añadiremos la action en nav_graph después)
                    Bundle args = new Bundle();
                    args.putInt("torneoId", torneo.id);
                    Navigation.findNavController(requireView()).navigate(R.id.action_adminTorneosFr_to_torneoDetalleFr, args);
                }

                @Override
                public void onInscribirClick() {
                    // En el caso del admin, este botón es "Crear Torneo"
                    Navigation.findNavController(requireView()).navigate(R.id.action_adminTorneosFr_to_crearTorneoFr);
                }
            });
            binding.rvAdminTorneos.setAdapter(torneosAdapter);

            // Adaptador de Partidos Pendientes
            PartidosAdapter partidosAdapter = new PartidosAdapter(dashboard.proximos_partidos);
            binding.rvAdminPartidos.setAdapter(partidosAdapter);
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show()
        );
    }

    private void setupClickListeners() {
        // Botón de Ajustes (Copilado casi exacto de PerfilFr)
        binding.btnAdminAjustes.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add(0, 1, 0, "Editar Info.");
            popup.getMenu().add(0, 2, 0, "Cerrar Sesión");
            popup.getMenu().add(0, 3, 0, "Tema: Azul Glow");
            popup.getMenu().add(0, 4, 0, "Tema: Cian Glow");
            popup.getMenu().add(0, 5, 0, "Tema: Verde Glow");

            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 1) {
                    if (viewModel.getPerfilData().getValue() != null) {
                        EditarPerfilDialog dialog = EditarPerfilDialog.newInstance(
                                viewModel.getPerfilData().getValue().nombre,
                                viewModel.getPerfilData().getValue().apellido,
                                viewModel.getPerfilData().getValue().imagen
                        );
                        dialog.setListener((nombre, apellido, uri) -> {
                            perfilViewModel.actualizarPerfil(nombre, apellido, uri);
                            viewModel.cargarTodo(); // Recargamos tras actualizar
                        });
                        dialog.show(getChildFragmentManager(), "EditarPerfil");
                    }
                    return true;
                } else if (item.getItemId() == 2) {
                    new SessionManager(requireContext()).logout();
                    Navigation.findNavController(v).navigate(R.id.action_global_loginFr);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}