package com.example.manosadomicilio.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.manosadomicilio.R;
import com.example.manosadomicilio.model.Zona;
import java.util.List;

public class ZonasAdapter extends RecyclerView.Adapter<ZonasAdapter.ViewHolder> {

    private List<Zona> zonaList;

    public ZonasAdapter(List<Zona> zonaList) {
        this.zonaList = zonaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zona, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Zona zona = zonaList.get(position);
        holder.cbZona.setText(zona.getNombre());
        holder.cbZona.setChecked(zona.isSelected());
        holder.cbZona.setOnCheckedChangeListener((buttonView, isChecked) -> {
            zona.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return zonaList.size();
    }

    public List<Zona> getZonaList() {
        return zonaList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbZona;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbZona = itemView.findViewById(R.id.cbZona);
        }
    }
}
