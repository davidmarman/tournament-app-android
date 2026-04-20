package com.example.tournamentapp.ui.dialogs; // Ajusta a tu paquete

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.tournamentapp.databinding.DialogCrearEquipoBinding;

public class CrearEquipoDialog extends DialogFragment {

    private DialogCrearEquipoBinding binding;
    private OnEquipoCreadoListener listener;
    private Uri imagenSeleccionada = null; // Guardará la foto que elija el usuario

    // 1. ACTUALIZAMOS LA INTERFAZ para pasar también la Uri de la imagen
    public interface OnEquipoCreadoListener {
        void onCrear(String nombreEquipo, Uri imagenUri);
    }

    public void setListener(OnEquipoCreadoListener listener) {
        this.listener = listener;
    }

    // 2. LANZADOR DE LA GALERÍA (La forma moderna de pedir fotos en Android)
    private final ActivityResultLauncher<String> selectorImagen = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    imagenSeleccionada = uri;
                    // Mostramos la imagen elegida en el recuadro
                    binding.ivNuevoLogo.setImageURI(uri);
                }
            }
    );

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        binding = DialogCrearEquipoBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());

        // 3. EVENTO DE CLICK EN LA IMAGEN
        binding.ivNuevoLogo.setOnClickListener(v -> {
            // Abrimos la galería buscando solo imágenes
            selectorImagen.launch("image/*");
        });

        binding.btnCancelar.setOnClickListener(v -> dismiss());

        binding.btnCrear.setOnClickListener(v -> {
            String nombre = binding.etNombreEquipo.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(getContext(), "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
            } else {
                if (listener != null) {
                    // Pasamos el nombre y la imagen (puede ser null si no eligió ninguna)
                    listener.onCrear(nombre, imagenSeleccionada);
                }
                dismiss();
            }
        });

        Dialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}