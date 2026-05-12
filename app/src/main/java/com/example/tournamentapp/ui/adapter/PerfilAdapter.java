package com.example.tournamentapp.ui.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.databinding.ItemPerfilSimpleBinding;

import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.ViewHolder> {
    private List<ItemSimple> items;
    private String nombreCarpeta;
    private int idPerfilPropietario; // Para saber a quién poner la "C"

    public PerfilAdapter(List<ItemSimple> items, String nombreCarpeta, int idPerfilPropietario) {
        this.items = items;
        this.nombreCarpeta = nombreCarpeta;
        this.idPerfilPropietario = idPerfilPropietario;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPerfilSimpleBinding binding = ItemPerfilSimpleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemSimple item = items.get(position);
        holder.binding.tvItemNombre.setText(item.nombre);

        // Carga de imagen
        if (item.logo != null && !item.logo.isEmpty()) {
            String url = "http://130.61.180.130:5000/uploads/" + nombreCarpeta + "/" + item.logo;
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.binding.ivItemLogo);
        } else {
            holder.binding.ivItemLogo.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // --- LÓGICA DE LA "C" DE CAPITÁN ---
        // Importa android.util.Log;
        Log.d("CAPITAN_DEBUG", "Equipo: " + item.nombre + " | idCapitan: " + item.idCapitan + " | Propietario: " + idPerfilPropietario);

        if ("equipos".equals(nombreCarpeta)) {
            if (item.idCapitan != 0 && item.idCapitan == idPerfilPropietario) {
                holder.binding.tvBadgeCapitan.setVisibility(View.VISIBLE);
            } else {
                holder.binding.tvBadgeCapitan.setVisibility(View.GONE);
            }
        }

        // --- NAVEGACIÓN ---
        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            if ("equipos".equals(nombreCarpeta)) {
                // Para EquipoDetalleFr
                args.putInt("equipoId", item.id);
                Navigation.findNavController(v).navigate(R.id.equipoDetalleFr, args);
            } else {
                // Para TorneoDetalleFr
                // IMPORTANTE: Verifica si tu TorneoDetalleFr usa "id_torneo" o "torneoId"
                args.putInt("torneoId", item.id);
                Navigation.findNavController(v).navigate(R.id.torneoDetalleFr, args);
            }
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemPerfilSimpleBinding binding;
        public ViewHolder(ItemPerfilSimpleBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}