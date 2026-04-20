package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.data.model.ClasificacionItem;
import com.example.tournamentapp.databinding.ItemClasificacionBinding;
import java.util.List;

public class ClasificacionAdapter extends RecyclerView.Adapter<ClasificacionAdapter.ViewHolder> {
    private List<ClasificacionItem> lista;

    public ClasificacionAdapter(List<ClasificacionItem> lista) { this.lista = lista; }

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
        holder.binding.tvGF.setText(String.valueOf(item.gf));
        holder.binding.tvGC.setText(String.valueOf(item.gc));

        String url = "http://130.61.180.130:5000/uploads/equipos/" + item.logo;
        Glide.with(holder.itemView.getContext()).load(url).into(holder.binding.ivEquipoLogo);
    }

    @Override
    public int getItemCount() { return lista.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemClasificacionBinding binding;
        public ViewHolder(ItemClasificacionBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}