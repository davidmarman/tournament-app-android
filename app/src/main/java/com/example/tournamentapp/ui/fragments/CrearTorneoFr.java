package com.example.tournamentapp.ui.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.tournamentapp.R;
import com.example.tournamentapp.databinding.FragmentCrearTorneoBinding;
import com.example.tournamentapp.ui.viewmodel.TorneosViewModel;

import java.util.Calendar;

public class CrearTorneoFr extends Fragment {

    private FragmentCrearTorneoBinding binding;
    private TorneosViewModel viewModel;
    private String fechaSeleccionada = ""; // Se guardará como YYYY-MM-DD
    private Uri imagenUri;

    // Lanzador para la galería
    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    imagenUri = result.getData().getData();
                    binding.ivCrearTorneoLogo.setImageURI(imagenUri);
                }
            }
    );

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCrearTorneoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(TorneosViewModel.class);

        setupObservers();

        // 1. Selector de Imagen
        binding.ivCrearTorneoLogo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // 2. Selector de Fecha de Inicio
        binding.btnElegirFechaInicio.setOnClickListener(v -> mostrarDatePicker());

        // 3. Botón dinámico "+" para añadir tramos horarios
        binding.btnAgregarHorario.setOnClickListener(v -> agregarNuevaFilaHorario());

        // 4. Crear Torneo
        binding.btnCrearTorneoFinal.setOnClickListener(v -> recolectarDatosYEnviar());
    }

    private void setupObservers() {
        viewModel.getMensajeExito().observe(getViewLifecycleOwner(), msg -> {
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
            // Si todo sale bien, volvemos a la pantalla anterior
            Navigation.findNavController(requireView()).navigateUp();
        });

        viewModel.getErrorMsg().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });
    }

    private void mostrarDatePicker() {
        Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            // Validamos
            Calendar fechaSelecc = Calendar.getInstance();
            fechaSelecc.set(year,month,dayOfMonth);

            Calendar hoy = Calendar.getInstance();
            hoy.set(Calendar.HOUR_OF_DAY,0); // Solo queremos comparar el dia
            hoy.set(Calendar.MINUTE,0);

            if (fechaSelecc.before(hoy)) {
                Toast.makeText(getContext(), "No puedes elegir una fecha pasada", Toast.LENGTH_SHORT).show();
            } else {
                fechaSeleccionada = String.format("%04d-%02d-%02d", year, (month + 1), dayOfMonth);
                binding.btnElegirFechaInicio.setText("Inicio: " + fechaSeleccionada);
            }
        }, anio, mes, dia);

        // Impide físicamente clicar días pasados en el calendario
        datePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePicker.show();
    }

    private void agregarNuevaFilaHorario() {
        View fila = getLayoutInflater().inflate(R.layout.item_horario_dinamico, null);

        Button btnInicio = fila.findViewById(R.id.btnHoraInicio);
        Button btnFin = fila.findViewById(R.id.btnHoraFin);
        ImageButton btnEliminar = fila.findViewById(R.id.btnEliminarTramo);

        btnInicio.setOnClickListener(v -> mostrarTimePicker(btnInicio));
        btnFin.setOnClickListener(v -> mostrarTimePicker(btnFin));
        btnEliminar.setOnClickListener(v -> binding.containerHorarios.removeView(fila));

        binding.containerHorarios.addView(fila);
    }

    private void mostrarTimePicker(Button btn) {
        TimePickerDialog timePicker = new TimePickerDialog(getContext(), (view, hour, minute) -> {
            btn.setText(String.format("%02d:%02d", hour, minute));
        }, 18, 0, true);
        timePicker.show();
    }

    private void recolectarDatosYEnviar() {
        // Validacion del nombre
        String nombre = binding.etNombreTorneo.getText().toString().trim();
        if (nombre.isEmpty()) {
            binding.etNombreTorneo.setError("El nombre es obligatorio");
            return;
        }

        // Validacion de la descripcion
        String descripcion = binding.etDescripcionTorneo.getText().toString().trim();
        if (descripcion.isEmpty()) {
            descripcion = "Sin descripción"; // Por si lo dejan en blanco
        }

        // Validacion de la fecha de inicio
        if (fechaSeleccionada.isEmpty()) {
            Toast.makeText(getContext(), "Debes seleccionar una fecha de inicio", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Días de la semana
        StringBuilder dias = new StringBuilder();
        if (binding.chipLun.isChecked()) dias.append("Lunes,");
        if (binding.chipMar.isChecked()) dias.append("Martes,");
        if (binding.chipMie.isChecked()) dias.append("Miercoles,");
        if (binding.chipJue.isChecked()) dias.append("Jueves,");
        if (binding.chipVie.isChecked()) dias.append("Viernes,");
        if (binding.chipSab.isChecked()) dias.append("Sabado,");
        if (binding.chipDom.isChecked()) dias.append("Domingo,");

        // Quitamos la última coma si existe
        String diasString = dias.toString();
        if (diasString.endsWith(",")) diasString = diasString.substring(0, diasString.length() - 1);

        // 2. Horarios dinámicos
        // 2. Leemos los horarios dinámicos y VALIDAMOS
        StringBuilder horarios = new StringBuilder();
        for (int i = 0; i < binding.containerHorarios.getChildCount(); i++) {
            View fila = binding.containerHorarios.getChildAt(i);
            Button bI = fila.findViewById(R.id.btnHoraInicio);
            Button bF = fila.findViewById(R.id.btnHoraFin);

            String horaI = bI.getText().toString();
            String horaF = bF.getText().toString();

            // Como el formato es siempre HH:MM (ej. 09:00, 18:30),
            // podemos compararlos alfabéticamente de forma segura.
            if (horaI.compareTo(horaF) >= 0) {
                Toast.makeText(getContext(), "Error: La hora de inicio (" + horaI + ") debe ser menor que la de fin (" + horaF + ")", Toast.LENGTH_LONG).show();
                return; // ¡Detenemos la ejecución, no enviamos nada al servidor!
            }

            horarios.append(horaI).append("-").append(horaF).append(",");
        }

        String horariosString = horarios.toString();
        if (horariosString.endsWith(",")) horariosString = horariosString.substring(0, horariosString.length() - 1);

        // Leemos bien el formato de los partidos
        String formato = binding.rbIdaVuelta.isChecked() ? "Ida y Vuelta" : "Ida";


        // 3. Enviamos todo al ViewModel
        viewModel.crearTorneo(nombre, descripcion, fechaSeleccionada, diasString, horariosString,formato, imagenUri);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}