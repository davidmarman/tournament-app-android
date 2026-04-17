package com.example.tournamentapp.ui.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.tournamentapp.R;
import com.example.tournamentapp.databinding.FragmentLoginBinding;
import com.example.tournamentapp.ui.viewmodel.LoginViewModel;

import org.jspecify.annotations.NonNull;


public class LoginFr extends Fragment {

    // Declaramos el Binding y el ViewModel
    private FragmentLoginBinding binding;
    private LoginViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflamos la vista con ViewBinding
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializamos el ViewModel
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Configuramos los clics y los observadores
        setupClickListeners();
        setupObservers();
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            // Mostramos la rueda de carga y bloqueamos el botón para que no hagan doble clic
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.btnLogin.setEnabled(false);

            // Le pasamos el trabajo al ViewModel
            viewModel.realizarLogin(email, password);
        });
    }

    private void setupObservers() {
        // Observamos si el login fue exitoso
        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), isSuccess -> {
            // Ocultamos la carga
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);

            if (isSuccess) {
                Toast.makeText(requireContext(), "¡Login Correcto!", Toast.LENGTH_SHORT).show();

                // NOTA: Descomenta esta línea cuando tengas el HomeFr y el Navigation Graph configurados
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_loginFr_to_homeFr);
            }
        });

        // Observamos si hubo algún error
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            // Ocultamos la carga y mostramos el error
            binding.progressBar.setVisibility(View.GONE);
            binding.btnLogin.setEnabled(true);

            Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 5. ¡Súper importante para evitar fugas de memoria con ViewBinding!
        binding = null;
    }
}