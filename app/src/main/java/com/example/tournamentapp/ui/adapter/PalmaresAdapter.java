package com.example.tournamentapp.ui.adapter;

import android.content.Context;
import android.util.TypedValue;
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

        holder.binding.tvTorneoNombreLogro.setText(item.torneo_nombre + " (" + item.fecha_logro + ")");

        // Protegemos el switch: Si tipo_logro es nulo, le damos un valor por defecto
        String tipoLogro = (item.tipo_logro != null) ? item.tipo_logro : "Desconocido";

        // --- AÑADE ESTA LÍNEA DE LOG TEMPORAL ---
        android.util.Log.d("DEBUG_PALMARES", "El backend manda: '" + tipoLogro + "'");
        //

        holder.binding.tvTipoLogro.setText(tipoLogro);

        int idIconoDrawable;
        boolean aplicarTinteTema = false;

        // ASIGNACIÓN DE ICONOS SEGÚN TUS NUEVOS PNG
        switch (tipoLogro) {
            case "Campeon":
                idIconoDrawable = R.drawable.ic_medalla_de_oro;
                aplicarTinteTema = true; // Se tiñe con el color neón activo (azul, cian, verde)
                break;

            case "Subcampeon":
                idIconoDrawable = R.drawable.ic_medalla_de_plata;
                aplicarTinteTema = true;
                break;

            case "Tercero":
                idIconoDrawable = R.drawable.ic_medalla_de_bronce;
                aplicarTinteTema = true;
                break;

            case "Pichichi":
                idIconoDrawable = R.drawable.ic_bota_de_oro;
                aplicarTinteTema = true;
                break;

            case "Más Amarillas":
                idIconoDrawable = R.drawable.ic_lesion;
                aplicarTinteTema = true;
                break;

            case "Más Rojas":
                idIconoDrawable = R.drawable.ic_hueso;
                aplicarTinteTema = true;
                break;

            default:
                idIconoDrawable = android.R.drawable.ic_menu_gallery;
                aplicarTinteTema = false;
                break;
        }

        // Seteamos el recurso gráfico asignado
        holder.binding.ivIconoTrofeo.setImageResource(idIconoDrawable);

        // APLICACIÓN DEL TINTE DINÁMICO SEGÚN EL TEMA ACTIVO
        if (aplicarTinteTema) {
            TypedValue typedValue = new TypedValue();
            Context context = holder.itemView.getContext();

            // Buscamos el colorPrimary de Material del tema actual (el que cambia en tu themes.xml)
            if (context.getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)) {
                holder.binding.ivIconoTrofeo.setColorFilter(typedValue.data, android.graphics.PorterDuff.Mode.SRC_IN);
            } else {
                holder.binding.ivIconoTrofeo.setColorFilter(null); // Fallback por si acaso
            }
        } else {
            holder.binding.ivIconoTrofeo.setColorFilter(null); // Limpiamos filtro para el icono default
        }
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0; // Añadida validación de nulidad para evitar crashes aleatorios
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemPalmaresBinding binding;
        public ViewHolder(ItemPalmaresBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}