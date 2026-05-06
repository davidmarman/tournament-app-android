package com.example.tournamentapp.ui.fragments;

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
            binding.rvClasificacion.setAdapter(new ClasificacionAdapter(response.clasificacion));

            // 4. Partidos (Añadiendo clickeabilidad para Admin como pediste)
            binding.tvTituloJornada.setText("Jornada " + response.jornada_actual + ":");
            PartidosJornadaAdapter adapter = new PartidosJornadaAdapter(response.partidos, partido -> {
                if ("Admin".equals(rolUsuario)) {
                    Toast.makeText(getContext(), "Aquí abriremos el diálogo de Resultados", Toast.LENGTH_SHORT).show();
                    // Aquí llamaremos al diálogo de resultados en el siguiente paso
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

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());
    }

    private void setupClickListeners() {
        binding.btnGenerarCalendario.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Generar Calendario")
                    .setMessage("¿Estás seguro? Una vez generado el calendario, no se podrán inscribir más equipos a este torneo.")
                    .setPositiveButton("Generar", (dialog, which) -> {
                        Toast.makeText(getContext(), "Mandando orden a Flask...", Toast.LENGTH_SHORT).show();
                        // AQUÍ LLAMAREMOS A LA RUTA MÁGICA DE FLASK (Próximo paso)
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
    }
}