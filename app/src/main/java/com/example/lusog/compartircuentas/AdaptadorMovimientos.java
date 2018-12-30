package com.example.lusog.compartircuentas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdaptadorMovimientos extends RecyclerView.Adapter<AdaptadorMovimientos.ViewHolderCuentas> {

    ArrayList<Movimiento> listaMovimientos;
    long idCuenta;
    String ristraParticipantes,tituloCuenta;

    public AdaptadorMovimientos(ArrayList<Movimiento> listaMov,long idCuenta, String ristraParticipantes,String tituloCuenta) {
        this.listaMovimientos = listaMov;
        this.idCuenta=idCuenta;
        this.ristraParticipantes=ristraParticipantes;
        this.tituloCuenta=tituloCuenta;
    }

    @NonNull
    @Override
    public AdaptadorMovimientos.ViewHolderCuentas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movimiento,parent,false);

        return new ViewHolderCuentas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorMovimientos.ViewHolderCuentas holder, final int position) {

        holder.labelNombre.setText(listaMovimientos.get(position).concepto);
        holder.labelFecha.setText(listaMovimientos.get(position).fecha);
        holder.labelImporte.setText(String.format("%.2f", listaMovimientos.get(position).cantidad)+"€");
        holder.labelPersona.setText(listaMovimientos.get(position).pagador);

        holder.buttEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alerta=new AlertDialog.Builder(v.getContext());
                alerta.setMessage("¿Seguro que desea eliminar el movimiento "+listaMovimientos.get(position).toString()+"?");

                alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //hay que eliminarlo

                        FirebaseDatabase.getInstance().getReference("listas").child( Long.toString(idCuenta)).child("movimientos").child(listaMovimientos.get(position).id).setValue(null);

                        dialog.dismiss();
                    }
                });

                alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alerta.create().show();

            }
        });

        holder.buttEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento=new Intent(v.getContext(),formNuevoGasto.class);
                intento.putExtra("esNuevoGasto",false);
                intento.putExtra("idCuenta",idCuenta);
                intento.putExtra("idMovimiento",listaMovimientos.get(position).id);
                intento.putExtra("concepto",listaMovimientos.get(position).concepto);
                intento.putExtra("fecha",listaMovimientos.get(position).fecha);
                intento.putExtra("importe",listaMovimientos.get(position).cantidad);
                intento.putExtra("pagador",listaMovimientos.get(position).pagador);
                intento.putExtra("disfrutantes",listaMovimientos.get(position).disfrutantes);
                intento.putExtra("ristraNombres",ristraParticipantes);
                intento.putExtra("tituloCuenta",tituloCuenta);

                v.getContext().startActivity(intento);
            }
        });



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
