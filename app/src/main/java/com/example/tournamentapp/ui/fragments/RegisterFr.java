package com.example.tournamentapp.ui.fragments;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tournamentapp.R;
import com.example.tournamentapp.data.utils.FileUtil;
import com.example.tournamentapp.databinding.FragmentRegisterBinding;
import com.example.tournamentapp.ui.viewmodel.RegisterViewModel;

import java.io.File;


public class RegisterFr extends Fragment {

    private FragmentRegisterBinding binding;
    private RegisterViewModel viewModel;
    private Uri selectedImageUri = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        setupClickListeners();
        setupObservers();
    }

    private void setupClickListeners() {
        binding.ivProfilePic.setOnClickListener(v -> mGetContent.launch("image/*"));
        binding.btnRegister.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();
            String apellido = binding.etApellido.getText().toString().trim();
            String username = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Determinar el rol según el RadioButton seleccionado
            String rol = binding.rbAdmin.isChecked() ? "Admin" : "User";

            // Convertimos la Uri en un File
            File imageFile = null;
            if(selectedImageUri != null){
                imageFile = FileUtil.getFileFromUri(requireContext(), selectedImageUri);
            }

            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnRegister.setEnabled(false);

            viewModel.realizarRegistro(nombre, apellido, username, email, password, rol, imageFile);
        });
    }

    private void setupObservers() {
        viewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);

            if (isSuccess) {
                Toast.makeText(requireContext(), "Cuenta creada. Por favor, inicia sesión.", Toast.LENGTH_LONG).show();
                // Volvemos a la pantalla de Login usando el NavController
                Navigation.findNavController(binding.getRoot()).popBackStack();
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            binding.progressBar.setVisibility(View.GONE);
            binding.btnRegister.setEnabled(true);
            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        });
    }

    // El "lanzador" de la galería
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    binding.ivProfilePic.setImageURI(uri); // Mostramos la foto elegida en pantalla
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}