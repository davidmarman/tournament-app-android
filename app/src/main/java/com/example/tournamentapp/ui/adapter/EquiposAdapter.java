package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.EquipoResponse;
import com.example.tournamentapp.databinding.ItemEquipoGridBinding;

import java.util.List;

public class EquiposAdapter extends RecyclerView.Adapter<EquiposAdapter.ViewHolder> {

    private List<EquipoResponse> listaEquipos;
    private OnEquipoClickListener listener;

    // Interfaz para avisar a la pantalla principal cuando se hace click
    public interface OnEquipoClickListener {
        void onEquipoClick(EquipoResponse equipo); // Click en un equipo normal
        void onCrearEquipoClick(); // Click en el botón de "+"
    }

    public EquiposAdapter(List<EquipoResponse> listaEquipos, OnEquipoClickListener listener) {
        this.listaEquipos = listaEquipos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEquipoGridBinding binding = ItemEquipoGridBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // LA MAGIA: Si la posición es igual al tamaño de la lista, es el último elemento
        if (position == listaEquipos.size()) {
            // DIBUJAR BOTÓN DE CREAR
            holder.binding.tvEquipoNombre.setText("Crear Equipo");
            holder.binding.ivEquipoLogo.setImageResource(android.R.drawable.ic_input_add);
            // Quitamos el fondo oscuro para que parezca un botón
            holder.binding.ivEquipoLogo.setBackgroundColor(android.graphics.Color.TRANSPARENT);

            holder.itemView.setOnClickListener(v -> listener.onCrearEquipoClick());
        } else {
            // DIBUJAR EQUIPO NORMAL
            EquipoResponse equipo = listaEquipos.get(position);
            holder.binding.tvEquipoNombre.setText(equipo.nombre);

            // Cargar logo con Glide (OJO con la IP y la nueva carpeta /equipos/)
            String url = "http://130.61.180.130:5000/uploads/equipos/" + equipo.logo;
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.binding.ivEquipoLogo);

            holder.itemView.setOnClickListener(v -> listener.onEquipoClick(equipo));
        }
    }

    @Override
    public int getItemCount() {
        // Sumamos 1 para reservar el hueco del botón "Crear Equipo"
        return listaEquipos.size() + 1;
    }

    public void actualizarLista(List<EquipoResponse> nuevaLista) {
        this.listaEquipos = nuevaLista;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemEquipoGridBinding binding;
        public ViewHolder(ItemEquipoGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}