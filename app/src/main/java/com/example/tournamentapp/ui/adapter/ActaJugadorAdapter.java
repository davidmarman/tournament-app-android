package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.JugadorActa;
import com.example.tournamentapp.databinding.ItemJugadorActaBinding;

import java.util.List;

public class ActaJugadorAdapter extends RecyclerView.Adapter<ActaJugadorAdapter.ViewHolder> {

    private List<JugadorActa> jugadores;
    private OnActaChangeListener listener;
    private int idCapitanEquipo;

    public interface OnActaChangeListener {
        void onGolesChanged();
    }

    public ActaJugadorAdapter(List<JugadorActa> jugadores, int idCapitanEquipo, OnActaChangeListener listener) {
        this.jugadores = jugadores;
        this.idCapitanEquipo = idCapitanEquipo;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemJugadorActaBinding binding = ItemJugadorActaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JugadorActa j = jugadores.get(position);

        holder.binding.tvJugadorNombre.setText(j.nombre);

        if (j.id_usuario == idCapitanEquipo) {
            holder.binding.tvBadgeCapitanJugador.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvBadgeCapitanJugador.setVisibility(View.GONE);
        }

        holder.binding.tvGolesContador.setText(String.valueOf(j.goles));

        String url = "http://130.61.180.130:5000/uploads/perfiles/" + j.imagen;
        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .circleCrop()
                .into(holder.binding.ivJugadorFoto);

        // REPARADO: Ahora cambiamos el alpha del CardView entero (bloque completo)
        holder.binding.cvAmarilla.setAlpha(j.amarillas > 0 ? 1.0f : 0.20f);
        holder.binding.cvRoja.setAlpha(j.rojas > 0 ? 1.0f : 0.20f);

        // REPARADO: Control del indicador x2 sobre la tarjeta amarilla
        if (j.amarillas == 2) {
            holder.binding.tvAmarillax2.setVisibility(View.VISIBLE);
        } else {
            holder.binding.tvAmarillax2.setVisibility(View.GONE);
        }

        // SUMAR GOL
        holder.binding.btnMasGol.setOnClickListener(v -> {
            j.goles++;
            notifyItemChanged(position);
            listener.onGolesChanged();
        });

        // RESTAR GOL
        holder.binding.btnMenosGol.setOnClickListener(v -> {
            if (j.goles > 0) {
                j.goles--;
                notifyItemChanged(position);
                listener.onGolesChanged();
            }
        });

        // REPARADO: Ciclo interactivo con refresco de celda impecable
        holder.binding.btnAmarilla.setOnClickListener(v -> {
            if (j.amarillas == 0) {
                j.amarillas = 1;
            } else if (j.amarillas == 1) {
                j.amarillas = 2;
                j.rojas = 1; // Expulsión por doble tarjeta
            } else {
                j.amarillas = 0;
                j.rojas = 0;
            }
            notifyItemChanged(position);
        });

        holder.binding.btnRoja.setOnClickListener(v -> {
            j.rojas = (j.rojas == 0) ? 1 : 0;
            // Si le quitamos la roja directa y tenía 2 amarillas, reseteamos el ciclo para evitar bloqueos
            if (j.rojas == 0 && j.amarillas == 2) {
                j.amarillas = 0;
            }
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    public void setJugadores(List<JugadorActa> nuevaLista) {
        this.jugadores = nuevaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemJugadorActaBinding binding;
        public ViewHolder(ItemJugadorActaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void updateData(List<JugadorActa> nuevaLista, int nuevoIdCapitan) {
        this.jugadores = nuevaLista;
        this.idCapitanEquipo = nuevoIdCapitan;
        notifyDataSetChanged();
    }
}