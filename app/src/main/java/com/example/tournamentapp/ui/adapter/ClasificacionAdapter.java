package com.example.tournamentapp.ui.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.ClasificacionItem;
import com.example.tournamentapp.databinding.ItemClasificacionBinding;
import java.util.List;

public class ClasificacionAdapter extends RecyclerView.Adapter<ClasificacionAdapter.ViewHolder> {
    private List<ClasificacionItem> lista;
    private boolean isExpanded = false;
    private boolean isAdmin = false; // <-- NUEVO
    private OnExpulsarClickListener expulsarListener; // <-- NUEVO

    // Interfaz para comunicar el click al fragmento
    public interface OnExpulsarClickListener {
        void onExpulsarClick(ClasificacionItem item);
    }

    // Constructor actualizado
    public ClasificacionAdapter(List<ClasificacionItem> lista, boolean isAdmin, OnExpulsarClickListener listener) {
        this.lista = lista;
        this.isAdmin = isAdmin;
        this.expulsarListener = listener;
    }

    public void setExpanded(boolean expanded){
        this.isExpanded = expanded;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemClasificacionBinding binding = ItemClasificacionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ClasificacionItem item = lista.get(position);

        holder.binding.tvRank.setText(String.valueOf(position + 1));
        holder.binding.tvEquipoNombre.setText(item.nombre);
        holder.binding.tvPts.setText(String.valueOf(item.pts));
        holder.binding.tvPG.setText(String.valueOf(item.pg));
        holder.binding.tvPE.setText(String.valueOf(item.pe));
        holder.binding.tvPP.setText(String.valueOf(item.pp));
        holder.binding.tvGF.setText(String.valueOf(item.gf));
        holder.binding.tvGC.setText(String.valueOf(item.gc));
        holder.binding.tvPJ.setText(String.valueOf(item.pj));

        holder.binding.layoutDetalle.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        // CONFIGURACIÓN DEL BOTÓN DE EXPULSAR
        if (isAdmin) {
            holder.binding.btnExpulsarEquipo.setVisibility(View.VISIBLE);
            holder.binding.btnExpulsarEquipo.setOnClickListener(v -> {
                if (expulsarListener != null) expulsarListener.onExpulsarClick(item);
            });
        } else {
            holder.binding.btnExpulsarEquipo.setVisibility(View.GONE);
        }

        String url = "http://130.61.180.130:5000/uploads/equipos/" + item.logo;
        Glide.with(holder.itemView.getContext()).load(url).placeholder(android.R.drawable.ic_menu_gallery).into(holder.binding.ivEquipoLogo);

        holder.itemView.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("equipoId", item.id_equipo);
            try {
                Navigation.findNavController(v).navigate(R.id.equipoDetalleFr, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() { return lista != null ? lista.size() : 0; }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemClasificacionBinding binding;
        public ViewHolder(ItemClasificacionBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}