package com.example.tournamentapp.ui.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.databinding.DialogEditarPerfilBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditarPerfilDialog extends BottomSheetDialogFragment {

    private DialogEditarPerfilBinding binding;
    private Uri nuevaImagenUri;
    private OnPerfilEditListener listener;

    // Datos actuales
    private String nombreActual, apellidoActual, fotoActual;

    public interface OnPerfilEditListener {
        void onEdit(String nombre, String apellido, Uri imageUri);
    }

    public static EditarPerfilDialog newInstance(String nombre, String apellido, String foto) {
        EditarPerfilDialog fragment = new EditarPerfilDialog();
        fragment.nombreActual = nombre;
        fragment.apellidoActual = apellido;
        fragment.fotoActual = foto;
        return fragment;
    }

    public void setListener(OnPerfilEditListener listener) { this.listener = listener; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditarPerfilBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Rellenar con datos actuales
        binding.etEditarNombre.setText(nombreActual);
        binding.etEditarApellido.setText(apellidoActual);
        String url = "http://130.61.180.130:5000/uploads/perfiles/" + fotoActual;
        Glide.with(this).load(url).into(binding.ivEditarFoto);

        // Selector de imagen
        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        nuevaImagenUri = result.getData().getData();
                        binding.ivEditarFoto.setImageURI(nuevaImagenUri);
                    }
                }
        );

        binding.ivEditarFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        binding.btnGuardarPerfil.setOnClickListener(v -> {
            String n = binding.etEditarNombre.getText().toString().trim();
            String a = binding.etEditarApellido.getText().toString().trim();
            if (listener != null) listener.onEdit(n, a, nuevaImagenUri);
            dismiss();
        });
    }
}