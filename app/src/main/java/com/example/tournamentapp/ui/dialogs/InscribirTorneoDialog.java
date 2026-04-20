package com.example.tournamentapp.ui.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.databinding.DialogInscribirTorneoBinding;

import java.util.ArrayList;
import java.util.List;

public class InscribirTorneoDialog extends DialogFragment {

    private DialogInscribirTorneoBinding binding;
    private OnInscribirListener listener;
    private List<EquipoResponse> misEquipos; // Todos mis equipos
    private List<EquipoResponse> equiposCapitan = new ArrayList<>(); // Solo de los que soy capitán

    // Interfaz para devolver los datos seleccionados
    public interface OnInscribirListener {
        void onInscribir(String codigoAcceso, int idEquipo);
    }

    public void setListener(OnInscribirListener listener) {
        this.listener = listener;
    }

    // Le pasamos la lista de equipos antes de abrir el diálogo
    public void setEquipos(List<EquipoResponse> equipos) {
        this.misEquipos = equipos;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        binding = DialogInscribirTorneoBinding.inflate(LayoutInflater.from(getContext()));
        builder.setView(binding.getRoot());

        configurarSpinner();

        binding.btnCancelarInscripcion.setOnClickListener(v -> dismiss());

        binding.btnConfirmarInscripcion.setOnClickListener(v -> {
            String codigo = binding.etCodigoTorneo.getText().toString().trim();

            if (codigo.isEmpty()) {
                Toast.makeText(getContext(), "Introduce el código del torneo", Toast.LENGTH_SHORT).show();
                return;
            }

            if (equiposCapitan.isEmpty()) {
                Toast.makeText(getContext(), "Necesitas ser capitán de un equipo para inscribirlo", Toast.LENGTH_SHORT).show();
                return;
            }

            // Sacamos el ID del equipo que el usuario seleccionó en el Spinner
            int posicionSeleccionada = binding.spinnerEquipos.getSelectedItemPosition();
            int idEquipoSeleccionado = equiposCapitan.get(posicionSeleccionada).id;

            if (listener != null) {
                listener.onInscribir(codigo, idEquipoSeleccionado);
            }
            dismiss();
        });

        return builder.create();
    }

    private void configurarSpinner() {
        List<String> nombresEquipos = new ArrayList<>();

        // Filtramos para mostrar solo donde es capitán
        if (misEquipos != null) {
            for (EquipoResponse equipo : misEquipos) {
                if (equipo.es_capitan) {
                    equiposCapitan.add(equipo);
                    nombresEquipos.add(equipo.nombre);
                }
            }
        }

        if (nombresEquipos.isEmpty()) {
            nombresEquipos.add("No eres capitán de ningún equipo");
            binding.btnConfirmarInscripcion.setEnabled(false); // Deshabilitamos el botón si no puede
        }

        // Creamos el adaptador nativo para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                nombresEquipos
        );
        binding.spinnerEquipos.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}