package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorMovimientos extends RecyclerView.Adapter<AdaptadorMovimientos.ViewHolderCuentas> {

    ArrayList<Movimiento> listaMovimientos;

    public AdaptadorMovimientos(ArrayList<Movimiento> listaMov) {
        this.listaMovimientos = listaMov;
    }

    @NonNull
    @Override
    public AdaptadorMovimientos.ViewHolderCuentas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento,null);

        return new ViewHolderCuentas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMovimientos.ViewHolderCuentas holder, int position) {

        holder.labelNombre.setText(listaMovimientos.get(position).concepto);
        holder.labelFecha.setText(listaMovimientos.get(position).fecha);
        holder.labelImporte.setText(String.format("%.2f", listaMovimientos.get(position).cantidad)+"€");
        holder.labelPersona.setText(listaMovimientos.get(position).Nombre);
    }

    @Override
    public int getItemCount() {
        return listaMovimientos.size();
    }

    public class ViewHolderCuentas extends RecyclerView.ViewHolder {

        TextView labelNombre,labelFecha,labelPersona,labelImporte;
        Button buttEditar,buttEliminar;

        public ViewHolderCuentas(View itemView) {
            super(itemView);

            labelNombre=itemView.findViewById(R.id.labelNombreMovimiento);
            labelFecha=itemView.findViewById(R.id.labelFecha);
            labelPersona=itemView.findViewById(R.id.labelPagador);
            labelImporte=itemView.findViewById(R.id.labelImporte);
            buttEditar=itemView.findViewById(R.id.buttEditar);
            buttEliminar=itemView.findViewById(R.id.buttEliminar);
        }
    }
}
