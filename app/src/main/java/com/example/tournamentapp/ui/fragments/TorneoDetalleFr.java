package com.example.tournamentapp.ui.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.utils.SessionManager;
import com.example.tournamentapp.databinding.FragmentTorneoDetalleBinding;
import com.example.tournamentapp.ui.adapter.ClasificacionAdapter;
import com.example.tournamentapp.ui.adapter.PartidosJornadaAdapter;
import com.example.tournamentapp.ui.viewmodel.TorneoDetalleViewModel;

public class TorneoDetalleFr extends Fragment {
    private FragmentTorneoDetalleBinding binding;
    private TorneoDetalleViewModel viewModel;
    private int torneoId;
    private String rolUsuario;
    private boolean mostrandoDetalles = false;
    private ClasificacionAdapter clasificacionAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) torneoId = getArguments().getInt("torneoId");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTorneoDetalleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TorneoDetalleViewModel.class);

        // Obtenemos el Rol
        SessionManager session = new SessionManager(requireContext());
        rolUsuario = session.getUserRole();

        binding.rvClasificacion.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvPartidosJornada.setLayoutManager(new LinearLayoutManager(getContext()));

        setupObservers();
        setupClickListeners();

        viewModel.cargarDetalle(torneoId);
    }

    private void setupObservers() {
        viewModel.getTorneoDetalle().observe(getViewLifecycleOwner(), response -> {

            // Dentro del observe de torneoDetalle
            if ("Finalizado".equals(response.info.estado)) {
                binding.btnFinalizarTorneo.setVisibility(View.GONE);
                binding.btnGenerarCalendario.setVisibility(View.GONE);
                binding.layoutAdminPanel.setVisibility(View.GONE);


                binding.tvTorneoFinalizado.setVisibility(View.VISIBLE);
                binding.tvTorneoFinalizado.setTextColor(Color.GREEN);
            }

            // 1. Mostrar Panel Admin si es necesario
            if ("Admin".equals(rolUsuario)) {
                binding.layoutAdminPanel.setVisibility(View.VISIBLE);

                // NOTA IMPORTANTE: Para que esto no falle, tienes que añadir la variable "codigo"
                // dentro de la clase "InfoBasica" de tu modelo TorneoDetalleResponse en Android.
                binding.tvAdminCodigo.setText(response.info.codigo != null ? response.info.codigo : "No disponible");

                // Si ya hay partidos creados, ocultamos el botón de generar calendario
                // para que no le den dos veces por error.
                if (response.partidos != null && !response.partidos.isEmpty()) {
                    binding.btnGenerarCalendario.setVisibility(View.GONE);
                }
            } else {
                binding.layoutAdminPanel.setVisibility(View.GONE);
            }

            // 2. Cabecera
            binding.tvDetalleTorneoNombre.setText(response.info.nombre);
            binding.tvDetalleTorneoDesc.setText(response.info.descripcion);
            String urlLogo = "http://130.61.180.130:5000/uploads/torneos/" + response.info.logo;
            Glide.with(this).load(urlLogo).into(binding.ivDetalleTorneoLogo);

            // 3. Clasificación
            clasificacionAdapter = new ClasificacionAdapter(response.clasificacion);
            clasificacionAdapter.setExpanded(mostrandoDetalles);
            binding.rvClasificacion.setAdapter(clasificacionAdapter);

            // 4. Partidos (Añadiendo clickeabilidad para Admin como pediste)
            binding.tvTituloJornada.setText("Jornada " + response.jornada_actual + ":");
            PartidosJornadaAdapter adapter = new PartidosJornadaAdapter(response.partidos, partido -> {
                if ("Admin".equals(rolUsuario)) {
                    // Dentro de tu TorneoDetalleFr, al hacer clic en un partido:
                    Bundle args = new Bundle();
                    args.putInt("partidoId", partido.id_partido);
                    Navigation.findNavController(requireView()).navigate(R.id.action_torneoDetalleFr_to_actaFr, args);
                }
            });
            binding.rvPartidosJornada.setAdapter(adapter);
        });

        viewModel.getTorneoEliminadoExito().observe(getViewLifecycleOwner(), eliminado -> {
            if (eliminado) {
                Toast.makeText(getContext(), "Torneo eliminado", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp(); // Volvemos atrás
            }
        });

        viewModel.getCalendarioGeneradoExito().observe(getViewLifecycleOwner(), generado -> {
            if (generado) {
                Toast.makeText(getContext(), "¡Calendario Listo!", Toast.LENGTH_SHORT).show();
                binding.btnGenerarCalendario.setVisibility(View.GONE);
                viewModel.cargarDetalle(torneoId); // Recargamos para ver los partidos
            }
        });

        viewModel.getTorneoFinalizadoExito().observe(getViewLifecycleOwner(), exito -> {
            if (exito) {
                Toast.makeText(getContext(), "¡Torneo Finalizado! Premios entregados.", Toast.LENGTH_LONG).show();
                // Recargamos el detalle para que el estado pase a 'Finalizado' y se actualice la UI
                viewModel.cargarDetalle(torneoId);
            }
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
    }

    private void setupClickListeners() {
        binding.btnGenerarCalendario.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Generar Calendario")
                    .setMessage("¿Estás seguro? Una vez generado el calendario, no se podrán inscribir más equipos a este torneo.")
                    .setPositiveButton("Generar", (dialog, which) -> {
                        viewModel.generarCalendario(torneoId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        binding.btnEliminarTorneo.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("¿Eliminar Torneo?")
                    .setMessage("Esta acción es irreversible. Se borrarán todos los partidos, inscripciones y la clasificación actual.")
                    .setPositiveButton("ELIMINAR", (dialog, which) -> {
                        // Llamamos al ViewModel para borrar
                        viewModel.eliminarTorneo(torneoId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });

        // Dentro de onViewCreated o setupClickListeners
        binding.btnToggleStats.setOnClickListener(v -> {
            // 1. Alternar el estado (puedes usar una variable booleana en el fragmento)
            mostrandoDetalles = !mostrandoDetalles;

            // 2. Cambiar visibilidad de las columnas de la cabecera
            binding.headerDetalle.setVisibility(mostrandoDetalles ? View.VISIBLE : View.GONE);

            // 3. Cambiar el icono del botón (+ o -)
            binding.btnToggleStats.setImageResource(mostrandoDetalles ?
                    android.R.drawable.ic_menu_close_clear_cancel : android.R.drawable.ic_menu_add);

            // 4. Avisar al adaptador para que actualice las filas
            if (clasificacionAdapter != null) {
                clasificacionAdapter.setExpanded(mostrandoDetalles);
            }
        });

        binding.btnFinalizarTorneo.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("¿Finalizar Torneo?")
                    .setMessage("Se calcularán los ganadores y se cerrará el torneo permanentemente. ¿Continuar?")
                    .setPositiveButton("SÍ, FINALIZAR", (dialog, which) -> {
                        viewModel.finalizarTorneo(torneoId);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}