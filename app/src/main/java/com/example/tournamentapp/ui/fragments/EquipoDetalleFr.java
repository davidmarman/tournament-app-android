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
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.FragmentEquipoDetalleBinding;
import com.example.tournamentapp.ui.adapter.JugadoresAdapter;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
import com.example.tournamentapp.ui.dialogs.AnadirJugadorDialog;
import com.example.tournamentapp.ui.dialogs.EditarEquipoDialog;
import com.example.tournamentapp.ui.dialogs.OpcionesJugadorDialog;
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

        viewModel.getMensajeExito().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        });

        // Observamos si hemos salido o disuelto el equipo para cerrar la pantalla
        viewModel.getSalirExitStatus().observe(getViewLifecycleOwner(), debeSalir -> {
            if (debeSalir) {
                androidx.navigation.Navigation.findNavController(requireView()).navigateUp();
            }
        });

        // 2. Observar los datos del equipo (¡Todo en un solo bloque!)
        viewModel.getEquipoData().observe(getViewLifecycleOwner(), equipo -> {

            binding.tvDetalleNombre.setText(equipo.nombre);

            // Cargar Logo Equipo
            String urlLogo = "http://130.61.180.130:5000/uploads/equipos/" + equipo.logo;
            Glide.with(this).load(urlLogo).into(binding.ivDetalleLogo);

            // Dentro de tu Observer de EquipoData en EquipoDetalleFr.java:

            binding.btnOpcionesEquipo.setOnClickListener(v -> {
                android.widget.PopupMenu popup = new android.widget.PopupMenu(requireContext(), v);

                // El menú cambia según tu rango
                if (equipo.es_capitan) {
                    popup.getMenu().add(0, 1, 0, "Editar Info");
                    popup.getMenu().add(0, 2, 0, "Disolver Equipo");
                } else {
                    popup.getMenu().add(0, 3, 0, "Salir del Equipo");
                }

                popup.setOnMenuItemClickListener(item -> {
                    if (item.getItemId() == 1) {
                        // Abrimos el menú de edición pasando los datos actuales
                        EditarEquipoDialog dialog = EditarEquipoDialog.newInstance(equipo.nombre, equipo.logo);
                        dialog.setListener((nombreNuevo, uriNueva) -> {
                            viewModel.editarEquipo(equipoId, nombreNuevo, uriNueva);
                        });
                        dialog.show(getChildFragmentManager(), "EditarEquipo");
                        return true;
                    }
                    else if (item.getItemId() == 2) {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("¿Disolver " + equipo.nombre + "?")
                                .setMessage("Esta acción es irreversible. Se borrarán todos los datos y jugadores.")
                                .setPositiveButton("Disolver", (dialog, which) -> viewModel.disolverEquipo(equipoId))
                                .setNegativeButton("Cancelar", null)
                                .show();
                        return true;
                    }
                    else if (item.getItemId() == 3) {
                        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                                .setTitle("¿Salir de " + equipo.nombre + "?")
                                .setMessage("Dejarás de pertenecer a este equipo.")
                                .setPositiveButton("Salir", (dialog, which) -> viewModel.salirDelEquipo(equipoId))
                                .setNegativeButton("Cancelar", null)
                                .show();
                        return true;
                    }
                    return false;
                });

                popup.show();
            });

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
            binding.rvTorneosInscrito.setAdapter(new PerfilAdapter(equipo.torneos, "torneos",0));

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
                                abrirOpcionesJugador(jugador.id, jugador.nombre);
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

    // Añade esta función en EquipoDetalleFr.java
    private void abrirOpcionesJugador(int idJugador, String nombreJugador) {
        // Necesitamos saber si TU eres el capitán de este equipo
        // Asumiendo que tu ViewModel ya tiene cargado el detalle del equipo:
        boolean soyCapitan = viewModel.getEquipoData().getValue() != null &&
                viewModel.getEquipoData().getValue().es_capitan;

        OpcionesJugadorDialog dialog = OpcionesJugadorDialog.newInstance(idJugador, nombreJugador, soyCapitan);

        dialog.setListener(new OpcionesJugadorDialog.OnJugadorOpcionesListener() {
            @Override
            public void onVerPerfil(int idJugador) {
                Bundle bundle = new Bundle();
                bundle.putInt("userId", idJugador);

                Navigation.findNavController(requireView())
                        .navigate(R.id.action_global_perfilFr, bundle);
            }

            @Override
            public void onExpulsar(int idJugador, String nombreJugador) {
                // Confirmación extra de seguridad para no expulsar por error
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("¿Expulsar a " + nombreJugador + "?")
                        .setMessage("No podrá volver a unirse a menos que le des el código de nuevo.")
                        .setPositiveButton("Expulsar", (d, w) -> {
                            if (viewModel.getEquipoData().getValue() != null) {
                                int idEquipo = viewModel.getEquipoData().getValue().id;
                                viewModel.expulsarJugador(idEquipo, idJugador);
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        dialog.show(getChildFragmentManager(), "OpcionesJugador");
    }
}