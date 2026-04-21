package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.PartidoItem; // <-- Import actualizado
import com.example.tournamentapp.databinding.ItemPartidoBinding;
import java.util.List;

public class PartidosAdapter extends RecyclerView.Adapter<PartidosAdapter.PartidoViewHolder> {
    private List<PartidoItem> listaPartidos;

    public PartidosAdapter(List<PartidoItem> listaPartidos) {
        this.listaPartidos = listaPartidos;
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPartidoBinding binding = ItemPartidoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PartidoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        PartidoItem partido = listaPartidos.get(position);
        holder.bind(partido);
    }

    @Override
    public int getItemCount() {
        return listaPartidos.size();
    }

    public static class PartidoViewHolder extends RecyclerView.ViewHolder {
        private final ItemPartidoBinding binding;

        public PartidoViewHolder(ItemPartidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(PartidoItem partido) {
            // Usamos directamente las variables públicas de PartidoItem
            binding.tvEquipoLocal.setText(partido.equipo_local);
            binding.tvEquipoVisitante.setText(partido.equipo_visitante);

            // Si el nombre del torneo viene vacío (ej. en DetalleTorneo), le ponemos un texto por defecto o lo ocultamos
            binding.tvTorneoNombre.setText(partido.nombre_torneo != null ? partido.nombre_torneo : "Partido de Liga");
            binding.tvFecha.setText(partido.fecha);

            String baseUrl = "http://130.61.180.130:5000/uploads/equipos/";

            Glide.with(itemView.getContext())
                    .load(baseUrl + partido.logo_local)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivLocalLogo);

            Glide.with(itemView.getContext())
                    .load(baseUrl + partido.logo_visitante)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(binding.ivVisitanteLogo);
        }
    }
}