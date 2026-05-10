package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
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

    // Interfaz para avisar al Fragmento de que el marcador global debe cambiar
    public interface OnActaChangeListener {
        void onGolesChanged();
    }

    public ActaJugadorAdapter(List<JugadorActa> jugadores, OnActaChangeListener listener) {
        this.jugadores = jugadores;
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
        holder.binding.tvGolesContador.setText(String.valueOf(j.goles));

        // Cargar foto
        String url = "http://130.61.180.130:5000/uploads/perfiles/" + j.imagen;
        Glide.with(holder.itemView.getContext())
                .load(url)
                .placeholder(android.R.drawable.ic_menu_myplaces)
                .circleCrop()
                .into(holder.binding.ivJugadorFoto);

        // Feedback visual: Si tiene tarjeta, la mostramos opaca, si no, semitransparente
        holder.binding.btnAmarilla.setAlpha(j.amarillas > 0 ? 1.0f : 0.2f);
        holder.binding.btnRoja.setAlpha(j.rojas > 0 ? 1.0f : 0.2f);

        // SUMAR GOL
        holder.binding.btnMasGol.setOnClickListener(v -> {
            j.goles++;
            notifyItemChanged(position); // Actualiza solo esta fila
            listener.onGolesChanged();   // Avisa al fragmento para actualizar el marcador de arriba
        });

        // RESTAR GOL
        holder.binding.btnMenosGol.setOnClickListener(v -> {
            if (j.goles > 0) {
                j.goles--;
                notifyItemChanged(position);
                listener.onGolesChanged();
            }
        });

        // PONER/QUITAR AMARILLA (Ciclo: 0 -> 1 -> 2 -> 0)
        holder.binding.btnAmarilla.setOnClickListener(v -> {
            if (j.amarillas == 0) {
                j.amarillas = 1; // Primera amarilla
            } else if (j.amarillas == 1) {
                j.amarillas = 2; // Segunda amarilla
                j.rojas = 1;     // ¡Doble amarilla conlleva roja automática!
            } else {
                j.amarillas = 0; // Resetear (por si el árbitro se equivocó)
                j.rojas = 0;     // Quitamos la roja que pusimos automáticamente
            }
            notifyItemChanged(position);
        });

        // PONER/QUITAR ROJA DIRECTA (Toggle: 0 o 1)
        holder.binding.btnRoja.setOnClickListener(v -> {
            j.rojas = (j.rojas == 0) ? 1 : 0;
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return jugadores.size();
    }

    // Método que usaremos cuando cambiemos de pestaña
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
}