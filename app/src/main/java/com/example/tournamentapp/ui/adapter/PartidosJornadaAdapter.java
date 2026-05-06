package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.PartidoItem;
import com.example.tournamentapp.databinding.ItemPartidoJornadaBinding;
import java.util.List;

public class PartidosJornadaAdapter extends RecyclerView.Adapter<PartidosJornadaAdapter.ViewHolder> {
    private List<PartidoItem> lista;

    // Añade la interfaz
    public interface OnPartidoClickListener { void onClick(PartidoItem partido); }

    private OnPartidoClickListener listener; // Añade la variable

    // Cambia el constructor
    public PartidosJornadaAdapter(List<PartidoItem> lista, OnPartidoClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPartidoJornadaBinding binding = ItemPartidoJornadaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PartidoItem partido = lista.get(position);
        holder.binding.tvLocalNombre.setText(partido.equipo_local);
        holder.binding.tvVisitanteNombre.setText(partido.equipo_visitante);
        holder.binding.tvMarcador.setText(partido.goles_local + " - " + partido.goles_visit);
        holder.binding.tvEstadoPartido.setText(partido.estado.equals("Pendiente") ? partido.fecha : "Finalizado");
        holder.itemView.setOnClickListener(v -> listener.onClick(partido));

        String baseUrl = "http://130.61.180.130:5000/uploads/equipos/";
        Glide.with(holder.itemView.getContext()).load(baseUrl + partido.logo_local).into(holder.binding.ivLocalLogo);
        Glide.with(holder.itemView.getContext()).load(baseUrl + partido.logo_visitante).into(holder.binding.ivVisitanteLogo);
    }

    @Override
    public int getItemCount() { return lista.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemPartidoJornadaBinding binding;
        public ViewHolder(ItemPartidoJornadaBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}