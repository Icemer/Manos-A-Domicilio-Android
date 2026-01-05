package com.example.manosadomicilio.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.manosadomicilio.R;
import com.example.manosadomicilio.model.Trabajador;
import com.example.manosadomicilio.view.DetalleTrabajador;

import java.util.List;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private List<Trabajador> favoritosList;
    private Context context;

    public FavoritosAdapter(Context context, List<Trabajador> favoritosList) {
        this.context = context;
        this.favoritosList = favoritosList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trabajador trabajador = favoritosList.get(position);
        holder.tvNombreFavorito.setText(trabajador.getNombreUsuario());
        // TODO: Set category name from trabajador model
        holder.tvCategoriaFavorito.setText("Categoría");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleTrabajador.class);
            intent.putExtra("id", trabajador.getId());
            intent.putExtra("nombre", trabajador.getNombreUsuario());
            intent.putExtra("descripcion", trabajador.getDescripcion());
            intent.putExtra("categoriaId", trabajador.getCategoriaId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return favoritosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatarFavorito;
        TextView tvNombreFavorito;
        TextView tvCategoriaFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatarFavorito = itemView.findViewById(R.id.ivAvatarFavorito);
            tvNombreFavorito = itemView.findViewById(R.id.tvNombreFavorito);
            tvCategoriaFavorito = itemView.findViewById(R.id.tvCategoriaFavorito);
        }
    }
}
