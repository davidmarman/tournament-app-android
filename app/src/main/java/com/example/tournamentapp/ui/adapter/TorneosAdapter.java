package com.example.tournamentapp.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.ItemTorneoGridBinding;

import java.util.List;

public class TorneosAdapter extends RecyclerView.Adapter<TorneosAdapter.ViewHolder> {

    private List<ItemSimple> torneos;
    private OnTorneoClickListener listener;
    private boolean isAdmin;

    public interface OnTorneoClickListener {
        void onTorneoClick(ItemSimple torneo);
        void onInscribirClick();
    }

    public TorneosAdapter(List<ItemSimple> torneos,boolean isAdmin, OnTorneoClickListener listener) {
        this.torneos = torneos;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTorneoGridBinding binding = ItemTorneoGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // LA MAGIA: Si la posición es igual al tamaño de la lista, es el último elemento
        if (position == torneos.size()) {

            // DIBUJAR BOTÓN DE INSCRIBIRSE
            if (isAdmin) {
                holder.binding.tvTorneoNombre.setText("Crear Torneo");
            } else {
                holder.binding.tvTorneoNombre.setText("Inscribirse");
            }
            holder.binding.ivTorneoLogo.setImageResource(android.R.drawable.ic_input_add);
            holder.binding.ivTorneoLogo.setBackgroundColor(Color.TRANSPARENT);

            holder.itemView.setOnClickListener(v -> listener.onInscribirClick());

        } else {

            // DIBUJAR TORNEO NORMAL
            ItemSimple torneo = torneos.get(position);
            holder.binding.tvTorneoNombre.setText(torneo.nombre);

            if (torneo.logo != null && !torneo.logo.isEmpty()) {
                String url = "http://130.61.180.130:5000/uploads/torneos/" + torneo.logo;
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .into(holder.binding.ivTorneoLogo);
            } else {
                holder.binding.ivTorneoLogo.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            holder.itemView.setOnClickListener(v -> listener.onTorneoClick(torneo));
        }
    }

    @Override
    public int getItemCount() {
        // Sumamos 1 para reservar el hueco del botón "Inscribirse" al final
        return torneos == null ? 1 : torneos.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemTorneoGridBinding binding;
        public ViewHolder(ItemTorneoGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}