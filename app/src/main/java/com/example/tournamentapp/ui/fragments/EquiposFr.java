package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.databinding.FragmentEquiposBinding;
import com.example.tournamentapp.ui.adapter.EquiposAdapter;
import com.example.tournamentapp.ui.dialogs.CrearEquipoDialog;
import com.example.tournamentapp.ui.viewmodel.EquiposViewModel;

import java.util.ArrayList;

public class EquiposFr extends Fragment implements EquiposAdapter.OnEquipoClickListener {

    private FragmentEquiposBinding binding;
    private EquiposViewModel viewModel;
    private EquiposAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEquiposBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1. Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(EquiposViewModel.class);

        // 2. Configurar RecyclerView en Cuadrícula de 2 columnas
        binding.rvEquipos.setLayoutManager(new GridLayoutManager(getContext(), 2));

        // 3. Inicializar Adaptador con una lista vacía y escuchar los clicks (this)
        adapter = new EquiposAdapter(new ArrayList<>(), this);
        binding.rvEquipos.setAdapter(adapter);

        // 4. Observar los datos
        setupObservers();

        // 5. Disparar la carga
        viewModel.cargarEquipos();
    }

    private void setupObservers() {
        viewModel.getEquiposData().observe(getViewLifecycleOwner(), listaEquipos -> {
            // Cuando llegan los datos del servidor, actualizamos la lista
            adapter.actualizarLista(listaEquipos);
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        viewModel.getEquipoCreado().observe(getViewLifecycleOwner(), creado -> {
            if (creado){
                Toast.makeText(getContext(), "Equipo creado con exito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- MANEJO DE CLICKS (Vienen de la interfaz del Adapter) ---

    @Override
    public void onEquipoClick(EquipoResponse equipo) {
        Bundle bundle = new Bundle();
        bundle.putInt("equipoId", equipo.id);

        Navigation.findNavController(requireView())
                .navigate(R.id.action_equipoFr_to_equipoDetalleFr, bundle);
    }

    @Override
    public void onCrearEquipoClick() {
        // Instanciamos el diálogo
        CrearEquipoDialog dialog = new CrearEquipoDialog();

        // Le decimos qué hacer cuando el usuario pulse el botón "Crear" del pop-up
        dialog.setListener((nombreEquipo, imagenUri) -> {
            // Le pasamos los datos al ViewModel para que haga la magia de red
            viewModel.crearNuevoEquipo(nombreEquipo, imagenUri);
        });

        // Mostramos el diálogo en pantalla
        dialog.show(getParentFragmentManager(), "CrearEquipoDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}