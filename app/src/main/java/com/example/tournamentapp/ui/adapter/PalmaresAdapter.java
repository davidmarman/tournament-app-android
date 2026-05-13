package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.PalmaresItem;
import com.example.tournamentapp.databinding.ItemPalmaresBinding;
import java.util.List;

public class PalmaresAdapter extends RecyclerView.Adapter<PalmaresAdapter.ViewHolder> {

    private List<PalmaresItem> lista;

    public PalmaresAdapter(List<PalmaresItem> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPalmaresBinding binding = ItemPalmaresBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PalmaresItem item = lista.get(position);

        holder.binding.tvTipoLogro.setText(item.tipo_logro);
        holder.binding.tvTorneoNombreLogro.setText(item.torneo_nombre + " (" + item.fecha_logro + ")");

        // 2. Protegemos el switch: Si tipo_logro es nulo, le damos un valor por defecto
        String tipoLogro = (item.tipo_logro != null) ? item.tipo_logro : "Desconocido";
        holder.binding.tvTipoLogro.setText(tipoLogro);

        // LÓGICA DE ICONOS SEGÚN LOGRO
        switch (tipoLogro) {
            case "Campeon":
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.btn_star_big_on);
                holder.binding.ivIconoTrofeo.setColorFilter(android.graphics.Color.parseColor("#FFD700"));
                break;

            case "Subcampeon":
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.btn_star_big_on);
                holder.binding.ivIconoTrofeo.setColorFilter(android.graphics.Color.parseColor("#C0C0C0")); // Plata
                break;

            case "Tercero":
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.btn_star_big_on);
                holder.binding.ivIconoTrofeo.setColorFilter(android.graphics.Color.parseColor("#CD7F32")); // Bronce
                break;

            case "Pichichi":
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.ic_menu_myplaces);
                holder.binding.ivIconoTrofeo.setColorFilter(null); // Sin filtro para que se vea normal
                break;

            case "Mas Amarillas":
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.ic_menu_report_image);
                holder.binding.ivIconoTrofeo.setColorFilter(android.graphics.Color.YELLOW);
                break;

            case "Mas Rojas":
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.ic_menu_report_image);
                holder.binding.ivIconoTrofeo.setColorFilter(android.graphics.Color.RED);
                break;

            default:
                holder.binding.ivIconoTrofeo.setImageResource(android.R.drawable.ic_menu_gallery);
                holder.binding.ivIconoTrofeo.setColorFilter(null);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPalmaresBinding binding;
        public ViewHolder(ItemPalmaresBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}