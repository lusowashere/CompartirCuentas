package com.example.lusog.compartircuentas;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorPersonas extends RecyclerView.Adapter<AdaptadorPersonas.ViewHolderPersonas> {

    ArrayList<Persona> listaPersonas;
    double costePromedio;

    public AdaptadorPersonas(ArrayList<Persona> listaPersonas, double promedio) {
        this.listaPersonas = listaPersonas;
        costePromedio=promedio;
    }

    @NonNull
    @Override
    public ViewHolderPersonas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personas_cuenta,null);
        return new ViewHolderPersonas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPersonas holder, int position) {
        holder.labelNombre.setText(listaPersonas.get(position).nombre);
        holder.labelMovimientos.setText(listaPersonas.get(position).numeroMovimientos+" gastos");
        holder.labelTotal.setText(listaPersonas.get(position).getTotalPagado());


        holder.labelDeuda.setText(listaPersonas.get(position).getDeuda(costePromedio));
        //lo pinto del color del tipo de deuda
        if(listaPersonas.get(position).deuda>0){
            holder.labelDeuda.setTextColor(Color.RED);
        }else if(listaPersonas.get(position).deuda<0){
            holder.labelDeuda.setTextColor(Color.rgb(0, 102, 0));
        }


    }

    @Override
    public int getItemCount() {
        return listaPersonas.size();
    }

    public class ViewHolderPersonas extends RecyclerView.ViewHolder {

        TextView labelNombre,labelMovimientos,labelTotal,labelDeuda;
        Button botonMas;

        public ViewHolderPersonas(View itemView) {
            super(itemView);
            labelNombre=itemView.findViewById(R.id.labelNombrePersona);
            labelMovimientos=itemView.findViewById(R.id.labelNumeroPagos);
            labelTotal=itemView.findViewById(R.id.labelTotalPagado);
            labelDeuda=itemView.findViewById(R.id.labelDeuda);
            botonMas=itemView.findViewById(R.id.buttonVerPersona);
        }
    }
}