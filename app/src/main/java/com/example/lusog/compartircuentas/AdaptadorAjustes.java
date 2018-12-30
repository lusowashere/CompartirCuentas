package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorAjustes extends RecyclerView.Adapter<AdaptadorAjustes.ViewHolderElementos> {

    public ArrayList<Movimiento> listaAjustes;
    public long numeroCuenta;

    public AdaptadorAjustes(ArrayList<Movimiento> listaAjustes, long numeroCuenta) {
        this.listaAjustes = listaAjustes;
        this.numeroCuenta = numeroCuenta;
    }

    @NonNull
    @Override
    public ViewHolderElementos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ajuste,parent,false);
        return new ViewHolderElementos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderElementos holder, int position) {
        holder.labelPagador.setText(listaAjustes.get(position).pagador);
        holder.labelReceptor.setText(listaAjustes.get(position).disfrutantes);
        holder.labelCantidad.setText("debe "+String.format("%.2f",listaAjustes.get(position).cantidad)+"€ a");
    }

    @Override
    public int getItemCount() {
        return listaAjustes.size();
    }

    public class ViewHolderElementos extends RecyclerView.ViewHolder {

        public TextView labelPagador,labelReceptor,labelCantidad;

        public ViewHolderElementos(View itemView) {
            super(itemView);

            labelPagador=itemView.findViewById(R.id.labelPagador);
            labelReceptor=itemView.findViewById(R.id.labelReceptor);
            labelCantidad=itemView.findViewById(R.id.labelCantidad);

        }


    }
}
