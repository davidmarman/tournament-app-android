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
import com.example.tournamentapp.data.model.ActaResponse;
import com.example.tournamentapp.data.model.EventoJugador;
import com.example.tournamentapp.data.model.FinalizarPartidoRequest;
import com.example.tournamentapp.data.model.JugadorActa;
import com.example.tournamentapp.databinding.FragmentActaBinding;
import com.example.tournamentapp.ui.adapter.ActaJugadorAdapter;
import com.example.tournamentapp.ui.viewmodel.ActaViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ActaFr extends Fragment {

    private FragmentActaBinding binding;
    private ActaViewModel viewModel;
    private ActaJugadorAdapter adapter;
    private ActaResponse actaActual; // Guardamos el acta completa aquí para cambiar de pestaña
    private int partidoId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            partidoId = getArguments().getInt("partidoId", 0);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentActaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ActaViewModel.class);
        binding.rvActaJugadores.setLayoutManager(new LinearLayoutManager(getContext()));

        setupObservers();
        setupClickListeners();

        // Pedimos los datos del partido al servidor
        viewModel.cargarActa(partidoId);
    }

    private void setupObservers() {
        viewModel.getActaData().observe(getViewLifecycleOwner(), acta -> {
            this.actaActual = acta;

            // 1. Cargar Logos y Marcador Inicial (0-0)
            String baseUrl = "http://130.61.180.130:5000/uploads/equipos/";
            Glide.with(this).load(baseUrl + acta.equipo_local.logo).into(binding.ivActaLocalLogo);
            Glide.with(this).load(baseUrl + acta.equipo_visitante.logo).into(binding.ivActaVisitanteLogo);
            actualizarMarcadorGlobal();

            // 2. Configurar las Pestañas (Tabs) con los nombres reales
            binding.tabLayoutActa.removeAllTabs();
            binding.tabLayoutActa.addTab(binding.tabLayoutActa.newTab().setText(acta.equipo_local.nombre));
            binding.tabLayoutActa.addTab(binding.tabLayoutActa.newTab().setText(acta.equipo_visitante.nombre));

            // 3. Inicializar el Adaptador con el equipo local (Pestaña 0)
            adapter = new ActaJugadorAdapter(acta.equipo_local.jugadores, this::actualizarMarcadorGlobal);
            binding.rvActaJugadores.setAdapter(adapter);

            // 4. Lógica para cambiar de pestaña
            binding.tabLayoutActa.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 0) {
                        adapter.setJugadores(actaActual.equipo_local.jugadores);
                    } else {
                        adapter.setJugadores(actaActual.equipo_visitante.jugadores);
                    }
                }
                @Override
                public void onTabUnselected(TabLayout.Tab tab) {}
                @Override
                public void onTabReselected(TabLayout.Tab tab) {}
            });
        });

        // Escuchar si el partido se guardó con éxito
        viewModel.getPartidoFinalizadoExito().observe(getViewLifecycleOwner(), exito -> {
            if (exito) {
                Toast.makeText(getContext(), "¡Acta enviada correctamente!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp(); // Volvemos a la pantalla anterior
            }
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show()
        );
    }

    private void setupClickListeners() {
        binding.btnTerminarPartido.setOnClickListener(v -> {
            if (actaActual == null) return;

            new AlertDialog.Builder(requireContext())
                    .setTitle("Terminar Partido")
                    .setMessage("¿Estás seguro de enviar esta acta? Se actualizará la clasificación automáticamente.")
                    .setPositiveButton("SÍ, TERMINAR", (dialog, which) -> recopilarYEnviarActa())
                    .setNegativeButton("REVISAR", null)
                    .show();
        });
    }

    // --- MÉTODOS MÁGICOS ---

    // Este método suma todos los goles de los jugadores para ponerlos en el texto gigante de arriba
    private void actualizarMarcadorGlobal() {
        if (actaActual == null) return;

        int golesLocal = 0;
        for (JugadorActa j : actaActual.equipo_local.jugadores) golesLocal += j.goles;

        int golesVisit = 0;
        for (JugadorActa j : actaActual.equipo_visitante.jugadores) golesVisit += j.goles;

        binding.tvActaMarcador.setText(golesLocal + " - " + golesVisit);
    }

    // Este método recoge lo que hizo cada jugador y lo empaqueta para Flask
    private void recopilarYEnviarActa() {
        List<EventoJugador> listaEventos = new ArrayList<>();
        int golesLocal = 0;
        int golesVisitante = 0;

        // Revisamos el equipo local
        for (JugadorActa j : actaActual.equipo_local.jugadores) {
            golesLocal += j.goles;
            if (j.goles > 0 || j.amarillas > 0 || j.rojas > 0) {
                listaEventos.add(new EventoJugador(j.id_usuario, j.goles, j.amarillas, j.rojas));
            }
        }

        // Revisamos el equipo visitante
        for (JugadorActa j : actaActual.equipo_visitante.jugadores) {
            golesVisitante += j.goles;
            if (j.goles > 0 || j.amarillas > 0 || j.rojas > 0) {
                listaEventos.add(new EventoJugador(j.id_usuario, j.goles, j.amarillas, j.rojas));
            }
        }

        // Creamos el paquete final y lo enviamos
        FinalizarPartidoRequest request = new FinalizarPartidoRequest(golesLocal, golesVisitante, listaEventos);
        viewModel.enviarResultadoFinal(partidoId, request);
    }
}