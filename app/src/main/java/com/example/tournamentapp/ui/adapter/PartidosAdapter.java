package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tournamentapp.data.model.Partido;
import com.example.tournamentapp.databinding.ItemPartidoBinding;

import java.util.List;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.PartidoViewHolder> {
    private List<Partido> listaPartidos;

    public PartidosAdapter(List<Partido> listaPartidos) {
        this.listaPartidos = listaPartidos;
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Usamos ViewBinding para inflar el layout del item
        ItemPartidoBinding binding = ItemPartidoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PartidoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        Partido partido = listaPartidos.get(position);
        holder.bind(partido);
    }

    @Override
    public int getItemCount() {
        return listaPartidos.size();
    }

    // Clase interna ViewHolder con ViewBinding
    public static class PartidoViewHolder extends RecyclerView.ViewHolder {
        private final ItemPartidoBinding binding;

        public PartidoViewHolder(ItemPartidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Partido partido) {
            binding.tvEquipoLocal.setText(partido.getEquipoLocal());
            binding.tvEquipoVisitante.setText(partido.getEquipoVisitante());
            binding.tvTorneoNombre.setText(partido.getNombreTorneo());
            binding.tvFecha.setText(partido.getFecha());
        }
    }
}
