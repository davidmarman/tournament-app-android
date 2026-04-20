package com.example.tournamentapp.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.ItemPerfilSimpleBinding; // Reutilizamos el diseño visual

import java.util.List;

public class JugadoresAdapter extends RecyclerView.Adapter<JugadoresAdapter.ViewHolder> {

    private List<ItemSimple> jugadores;
    private boolean esCapitan;
    private OnJugadorClickListener listener;

    // Interfaz para escuchar los clicks desde el Fragmento
    public interface OnJugadorClickListener {
        void onJugadorClick(ItemSimple jugador);
        void onAnadirJugadorClick();
    }

    public JugadoresAdapter(List<ItemSimple> jugadores, boolean esCapitan, OnJugadorClickListener listener) {
        this.jugadores = jugadores;
        this.esCapitan = esCapitan;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Reutilizamos el cuadrado pequeño con foto y texto que ya teníamos
        ItemPerfilSimpleBinding binding = ItemPerfilSimpleBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Si es capitán, la posición 0 está reservada para el botón "+"
        if (esCapitan && position == 0) {
            holder.binding.tvItemNombre.setText("Añadir");
            holder.binding.ivItemLogo.setImageResource(android.R.drawable.ic_input_add);
            holder.binding.ivItemLogo.setBackgroundColor(Color.TRANSPARENT);

            holder.itemView.setOnClickListener(v -> listener.onAnadirJugadorClick());
        } else {
            // Si es capitán, los jugadores reales empiezan en la posición de la lista menos 1
            // Si no es capitán, las posiciones coinciden exactamente
            int indexReal = esCapitan ? position - 1 : position;
            ItemSimple jugador = jugadores.get(indexReal);

            holder.binding.tvItemNombre.setText(jugador.nombre);

            if (jugador.logo != null && !jugador.logo.isEmpty()) {
                // Buscamos la imagen en la carpeta de perfiles
                String url = "http://130.61.180.130:5000/uploads/perfiles/" + jugador.logo;
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.binding.ivItemLogo);
            } else {
                holder.binding.ivItemLogo.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            holder.itemView.setOnClickListener(v -> listener.onJugadorClick(jugador));
        }
    }

    @Override
    public int getItemCount() {
        // Si es capitán, le decimos a Android que dibuje un elemento extra para el botón
        return esCapitan ? jugadores.size() + 1 : jugadores.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemPerfilSimpleBinding binding;
        public ViewHolder(ItemPerfilSimpleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}