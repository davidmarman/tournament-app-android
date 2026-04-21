package com.example.tournamentapp.ui.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.tournamentapp.databinding.DialogOpcionesJugadorBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class OpcionesJugadorDialog extends BottomSheetDialogFragment {

    private DialogOpcionesJugadorBinding binding;
    private String nombreJugador;
    private int idJugador;
    private boolean esCapitan;
    private OnJugadorOpcionesListener listener;

    public interface OnJugadorOpcionesListener {
        void onVerPerfil(int idJugador);
        void onExpulsar(int idJugador, String nombre);
    }

    public static OpcionesJugadorDialog newInstance(int idJugador, String nombreJugador, boolean esCapitan) {
        OpcionesJugadorDialog dialog = new OpcionesJugadorDialog();
        dialog.idJugador = idJugador;
        dialog.nombreJugador = nombreJugador;
        dialog.esCapitan = esCapitan;
        return dialog;
    }

    public void setListener(OnJugadorOpcionesListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogOpcionesJugadorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.tvNombreJugadorOpciones.setText(nombreJugador);

        // Si no eres el capitán, ocultamos el botón de expulsar por completo
        if (!esCapitan) {
            binding.btnExpulsar.setVisibility(View.GONE);
        }

        binding.btnVerPerfil.setOnClickListener(v -> {
            if (listener != null) listener.onVerPerfil(idJugador);
            dismiss();
        });

        binding.btnExpulsar.setOnClickListener(v -> {
            if (listener != null) listener.onExpulsar(idJugador, nombreJugador);
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}