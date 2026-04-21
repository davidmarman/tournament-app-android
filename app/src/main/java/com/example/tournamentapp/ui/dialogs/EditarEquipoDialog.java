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
import com.example.tournamentapp.databinding.DialogEditarEquipoBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class EditarEquipoDialog extends BottomSheetDialogFragment {

    private DialogEditarEquipoBinding binding;
    private Uri nuevaImagenUri;
    private OnEquipoEditListener listener;

    private String nombreActual, logoActual;

    public interface OnEquipoEditListener {
        void onEdit(String nombre, Uri imageUri);
    }

    public static EditarEquipoDialog newInstance(String nombre, String logo) {
        EditarEquipoDialog fragment = new EditarEquipoDialog();
        fragment.nombreActual = nombre;
        fragment.logoActual = logo;
        return fragment;
    }

    public void setListener(OnEquipoEditListener listener) { this.listener = listener; }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogEditarEquipoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.etEditarNombreEquipo.setText(nombreActual);
        String url = "http://130.61.180.130:5000/uploads/equipos/" + logoActual;
        Glide.with(this).load(url).into(binding.ivEditarLogoEquipo);

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        nuevaImagenUri = result.getData().getData();
                        binding.ivEditarLogoEquipo.setImageURI(nuevaImagenUri);
                    }
                }
        );

        binding.ivEditarLogoEquipo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intent);
        });

        binding.btnGuardarEquipo.setOnClickListener(v -> {
            String n = binding.etEditarNombreEquipo.getText().toString().trim();
            if (listener != null) listener.onEdit(n, nuevaImagenUri);
            dismiss();
        });
    }
}