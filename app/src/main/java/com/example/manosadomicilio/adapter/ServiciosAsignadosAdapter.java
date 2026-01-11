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
import com.example.manosadomicilio.view.DetalleServicioAsignado;

import java.util.List;

public class ServiciosAsignadosAdapter extends RecyclerView.Adapter<ServiciosAsignadosAdapter.ViewHolder> {

    private List<Servicio> servicioList;
    private Context context;

    public ServiciosAsignadosAdapter(Context context, List<Servicio> servicioList) {
        this.context = context;
        this.servicioList = servicioList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio_asignado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Servicio servicio = servicioList.get(position);
        holder.tvNombreCliente.setText(servicio.getNombreCliente());
        holder.tvFechaServicioAsignado.setText("Fecha: " + servicio.getFecha());
        holder.tvEstadoServicioAsignado.setText("Estado: " + servicio.getEstado());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleServicioAsignado.class);
            intent.putExtra("servicioId", servicio.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return servicioList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCliente;
        TextView tvFechaServicioAsignado;
        TextView tvEstadoServicioAsignado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCliente = itemView.findViewById(R.id.tvNombreCliente);
            tvFechaServicioAsignado = itemView.findViewById(R.id.tvFechaServicioAsignado);
            tvEstadoServicioAsignado = itemView.findViewById(R.id.tvEstadoServicioAsignado);
        }
    }
}
