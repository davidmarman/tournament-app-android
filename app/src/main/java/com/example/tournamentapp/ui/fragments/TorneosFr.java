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
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.FragmentTorneosBinding;
import com.example.tournamentapp.ui.adapter.PerfilAdapter;
import com.example.tournamentapp.ui.adapter.TorneosAdapter;
import com.example.tournamentapp.ui.dialogs.InscribirTorneoDialog;
import com.example.tournamentapp.ui.viewmodel.TorneosViewModel;

public class TorneosFr extends Fragment {

    private FragmentTorneosBinding binding;
    private TorneosViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTorneosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TorneosViewModel.class);

        // Configuramos la lista en cuadrícula (2 columnas)
        binding.rvTorneos.setLayoutManager(new GridLayoutManager(getContext(), 2));

        setupObservers();

        // Cargamos los datos iniciales
        viewModel.cargarTorneos();
        viewModel.cargarEquiposParaDialog(); // Pre-cargamos los equipos para cuando abra el Pop-up
    }

    private void setupObservers() {
        // 1. Mostrar la lista con el botón incluido
        viewModel.getTorneosData().observe(getViewLifecycleOwner(), torneos -> {

            TorneosAdapter adapter = new TorneosAdapter(torneos, new TorneosAdapter.OnTorneoClickListener() {
                @Override
                public void onTorneoClick(ItemSimple torneo) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("torneoId", torneo.id);

                    Navigation.findNavController(requireView())
                            .navigate(R.id.action_torneosFr_to_torneoDetalleFr, bundle);
                }

                @Override
                public void onInscribirClick() {
                    abrirDialogoInscripcion();
                }
            });

            binding.rvTorneos.setAdapter(adapter);
        });

        // 2. Mensajes de éxito
        viewModel.getMensajeExito().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });

        // 3. Mensajes de error
        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void abrirDialogoInscripcion() {
        // Obtenemos los equipos que cargamos previamente
        if (viewModel.getMisEquipos().getValue() == null) {
            Toast.makeText(getContext(), "Cargando tus equipos, espera un segundo...", Toast.LENGTH_SHORT).show();
            return;
        }

        InscribirTorneoDialog dialog = new InscribirTorneoDialog();
        // Le pasamos la lista para que el Spinner la dibuje
        dialog.setEquipos(viewModel.getMisEquipos().getValue());

        // Escuchamos cuando pulse "Inscribirse" en el pop-up
        dialog.setListener((codigoAcceso, idEquipo) -> {
            viewModel.inscribirEquipo(codigoAcceso, idEquipo);
        });

        dialog.show(getChildFragmentManager(), "InscribirDialog");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}