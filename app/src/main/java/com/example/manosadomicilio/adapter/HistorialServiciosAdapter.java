package com.example.manosadomicilio.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.manosadomicilio.R;
import com.example.manosadomicilio.model.Servicio;
import com.example.manosadomicilio.view.DetalleServicioActivity;
import java.util.List;

public class HistorialServiciosAdapter extends RecyclerView.Adapter<HistorialServiciosAdapter.ViewHolder> {

    private List<Servicio> serviceList;
    private Context context;

    public HistorialServiciosAdapter(Context context, List<Servicio> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historial_servicio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = serviceList.get(position);
        holder.tvNombreTrabajador.setText(servicio.getNombreTrabajador());
        holder.tvFechaServicio.setText("Fecha: " + servicio.getFecha());
        holder.tvEstadoServicio.setText("Estado: " + servicio.getEstado());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleServicioActivity.class);
            intent.putExtra("servicioId", servicio.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreTrabajador;
        TextView tvFechaServicio;
        TextView tvEstadoServicio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreTrabajador = itemView.findViewById(R.id.tvNombreTrabajador);
            tvFechaServicio = itemView.findViewById(R.id.tvFechaServicio);
            tvEstadoServicio = itemView.findViewById(R.id.tvEstadoServicio);
        }
    }
}
