package com.example.tournamentapp.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tournamentapp.R;
import com.example.tournamentapp.data.model.AdminUserResponse;
import com.google.android.material.imageview.ShapeableImageView;
import java.util.List;

public class AdministradoresAdapter extends RecyclerView.Adapter<AdministradoresAdapter.AdminViewHolder> {

    private List<AdminUserResponse> lista;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(AdminUserResponse admin);
    }

    public AdministradoresAdapter(List<AdminUserResponse> lista, OnDeleteClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_administrador, parent, false);
        return new AdminViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        AdminUserResponse admin = lista.get(position);
        holder.tvUsername.setText(admin.username);

        String urlLogo = "http://130.61.180.130:5000/uploads/perfiles/" + admin.imagen;
        Glide.with(holder.itemView.getContext())
                .load(urlLogo)
                .placeholder(android.R.drawable.ic_menu_report_image)
                .into(holder.ivPerfil);

        holder.btnQuitar.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(admin);
        });
    }

    @Override
    public int getItemCount() {
        return lista != null ? lista.size() : 0;
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView ivPerfil;
        ImageButton btnQuitar;
        TextView tvUsername;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPerfil = itemView.findViewById(R.id.ivAdminPerfil);
            btnQuitar = itemView.findViewById(R.id.btnQuitarAdmin);
            tvUsername = itemView.findViewById(R.id.tvAdminUsername);
        }
    }
}