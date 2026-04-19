package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.ItemSimple;
import com.example.tournamentapp.data.model.PerfilResponse;
import com.example.tournamentapp.databinding.ItemPerfilSimpleBinding;

import java.util.List;

public class PerfilAdapter extends RecyclerView.Adapter<PerfilAdapter.ViewHolder> {
    private List<ItemSimple> items;
    private String nombreCarpeta;

    public PerfilAdapter(List<ItemSimple> items, String nombreCarpeta) {
        this.items = items;
        this.nombreCarpeta = nombreCarpeta;
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

        if (item.logo != null && !item.logo.isEmpty()) {
            String url = "http://130.61.180.130:5000/uploads/"+nombreCarpeta+"/" + item.logo;
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery) // Imagen mientras carga
                    .into(holder.binding.ivItemLogo);
        } else {
            // Si no hay logo, ponemos un icono de sistema
            holder.binding.ivItemLogo.setImageResource(android.R.drawable.ic_menu_gallery);
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemPerfilSimpleBinding binding;
        public ViewHolder(ItemPerfilSimpleBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}