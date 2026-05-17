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
import com.example.tournamentapp.data.model.EquipoDetalleResponse;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.FragmentEquipoDetalleBinding;
import com.example.tournamentapp.ui.adapter.JugadoresAdapter;
import com.example.tournamentapp.ui.adapter.PalmaresAdapter;
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

        binding.rvPalmaresEquipo.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
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

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show();
            }
        });

        // 2. Observar los datos del equipo
        viewModel.getEquipoData().observe(getViewLifecycleOwner(), equipo -> {

            binding.tvDetalleNombre.setText(equipo.nombre);

            if (equipo.soy_miembro) {
                binding.btnOpcionesEquipo.setVisibility(View.VISIBLE);
            } else {
                binding.btnOpcionesEquipo.setVisibility(View.GONE); // Si es un rival, ocultamos el menú completo
            }

            if (equipo.lider_goles != null) {
                binding.tvLiderGoles.setText(equipo.lider_goles.username + "\n" + equipo.lider_goles.goles + " G");
            }
            if (equipo.lider_amarillas != null) {
                binding.tvLiderAmarillas.setText(equipo.lider_amarillas.username + "\n" + equipo.lider_amarillas.amarillas + " 🟨");
            }
            if (equipo.lider_rojas != null) {
                binding.tvLiderRojas.setText(equipo.lider_rojas.username + "\n" + equipo.lider_rojas.rojas + " 🟥");
            }

            // Cargar Logo Equipo
            String urlLogo = "http://130.61.180.130:5000/uploads/equipos/" + equipo.logo;
            Glide.with(this).load(urlLogo).into(binding.ivDetalleLogo);


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

            // Palmares
            if (equipo.palmares != null && !equipo.palmares.isEmpty()) {
                // Si hay trofeos: ocultamos el texto de "vacio" y mostramos el RecyclerView
                binding.tvPalmaresVacio.setVisibility(View.GONE);
                binding.rvPalmaresEquipo.setVisibility(View.VISIBLE);

                PalmaresAdapter palmaresAdapter = new PalmaresAdapter(equipo.palmares);
                binding.rvPalmaresEquipo.setAdapter(palmaresAdapter);
            } else {
                // Si no hay nada: mostramos el mensaje por defecto
                binding.tvPalmaresVacio.setVisibility(View.VISIBLE);
                binding.rvPalmaresEquipo.setVisibility(View.GONE);
            }

            // Jugadores
            if (equipo.jugadores != null) {
                binding.rvJugadores.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                // Corregimos la inicialización asegurando que solo pase true si es miembro Y capitán
                boolean esCapitanReal = equipo.soy_miembro && equipo.es_capitan;

                JugadoresAdapter jugadoresAdapter = new JugadoresAdapter(
                        equipo.jugadores,
                        esCapitanReal, // Inyectamos el booleano blindado
                        equipo.id_capitan,
                        new JugadoresAdapter.OnJugadorClickListener() {
                            @Override
                            public void onJugadorClick(ItemSimple jugador) {
                                if (equipo.soy_miembro) {
                                    // Si soy del equipo (compañero o capitán), abro el diálogo de opciones
                                    abrirOpcionesJugador(jugador.id, jugador.nombre);
                                } else {
                                    // Si soy un rival de fuera, evitamos el diálogo y vamos DIRECTO a ver su perfil
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("userId", jugador.id);
                                    Navigation.findNavController(requireView())
                                            .navigate(R.id.action_global_perfilFr, bundle);
                                }
                            }

                            @Override
                            public void onAnadirJugadorClick() {
                                AnadirJugadorDialog dialog = new AnadirJugadorDialog();
                                dialog.setListener(username -> {
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

    private void abrirOpcionesJugador(int idJugador, String nombreJugador) {
        // 1. Recuperamos de forma segura el objeto completo del equipo actual
        EquipoDetalleResponse equipoActual = viewModel.getEquipoData().getValue();

        boolean soyCapitanDeEsteEquipo = false;

        if (equipoActual != null) {
            // CONDICIÓN CRUCIAL: Solo puedes tener privilegios de gestión si eres miembro Y el backend confirma que eres su capitán
            soyCapitanDeEsteEquipo = equipoActual.soy_miembro && equipoActual.es_capitan;
        }

        // 2. Le pasamos el resultado real de "soyCapitanDeEsteEquipo" al constructor del Dialog
        OpcionesJugadorDialog dialog = OpcionesJugadorDialog.newInstance(idJugador, nombreJugador, soyCapitanDeEsteEquipo);

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

            @Override
            public void onCederCapitania(int idJugador, String nombreJugador) {
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("¿Ceder capitanía?")
                        .setMessage("Dejarás de ser el capitán y " + nombreJugador + " tendrá el control total del equipo. Esta acción no se puede deshacer.")
                        .setPositiveButton("CEDER MANDO", (d, w) -> {
                            viewModel.cederCapitania(equipoId, idJugador);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        dialog.show(getChildFragmentManager(), "OpcionesJugador");
    }
}