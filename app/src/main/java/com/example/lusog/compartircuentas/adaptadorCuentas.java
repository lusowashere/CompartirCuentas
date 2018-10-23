package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class adaptadorCuentas extends RecyclerView.Adapter<adaptadorCuentas.ViewHolderCuentas> {

    ArrayList<Cuenta> listaCuentas;

    public adaptadorCuentas(ArrayList<Cuenta> listaCuentas) {
        this.listaCuentas = listaCuentas;
    }

    @NonNull
    @Override
    public ViewHolderCuentas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_principal,null);
        return new ViewHolderCuentas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderCuentas holder, int position) {
        holder.txtTitulo.setText(listaCuentas.get(position).titulo);
        holder.txtImporte.setText(Double.toString( listaCuentas.get(position).importeTotal));

        holder.buttVerDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aquí se abrirá un formulario que llevará a los detalles
            }
        });

        holder.buttNuevoGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aquí se abrirá el formulario para el nuevo gasto
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaCuentas.size();
    }

    public class ViewHolderCuentas extends RecyclerView.ViewHolder {

        TextView txtTitulo, txtImporte;
        Button buttNuevoGasto, buttVerDetalles;

        public ViewHolderCuentas(View itemView) {
            super(itemView);
            txtTitulo=itemView.findViewById(R.id.idTitulo);
            txtImporte=itemView.findViewById(R.id.idImporte);
            buttNuevoGasto=itemView.findViewById(R.id.buttAddMovim);
            buttVerDetalles=itemView.findViewById(R.id.buttDetalles);
        }
    }
}
