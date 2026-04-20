package com.example.tournamentapp.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import com.example.tournamentapp.databinding.DialogAnadirJugadorBinding;

public class AnadirJugadorDialog extends DialogFragment {

    private DialogAnadirJugadorBinding binding;
    private OnJugadorBusquedaListener listener;

    public interface OnJugadorBusquedaListener {
        void onAnadir(String username);
    }

    public void setListener(OnJugadorBusquedaListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        binding = DialogAnadirJugadorBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());

        binding.btnCancelarAnadir.setOnClickListener(v -> dismiss());

        binding.btnConfirmarAnadir.setOnClickListener(v -> {
            String username = binding.etUsernameBusqueda.getText().toString().trim();
            if (username.isEmpty()) {
                Toast.makeText(getContext(), "Escribe un nombre de usuario", Toast.LENGTH_SHORT).show();
            } else {
                if (listener != null) listener.onAnadir(username);
                dismiss();
            }
        });

        return builder.create();
    }
}