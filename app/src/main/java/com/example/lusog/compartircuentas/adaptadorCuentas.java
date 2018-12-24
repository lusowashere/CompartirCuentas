package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adaptadorCuentas extends RecyclerView.Adapter<adaptadorCuentas.ViewHolderCuentas> {

    //ArrayList<Cuenta> listaCuentas;
    ArrayList<Cuenta2> listaCuentas;
    private final Context context;

    FirebaseDatabase database;
    DatabaseReference myRef;


    public adaptadorCuentas(ArrayList<Cuenta2> listaCuentas, Context contexto) {
        this.listaCuentas = listaCuentas;
        this.context=contexto;

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

    }



    @NonNull
    @Override
    public ViewHolderCuentas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lista_principal,parent,false);
        return new ViewHolderCuentas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderCuentas holder, final int position) {
        //holder.txtTitulo.setText(listaCuentas.get(position).titulo);
        //holder.txtImporte.setText(Double.toString( listaCuentas.get(position).importeTotal)+"€");

        listaCuentas.get(position).addLabel("titulo",holder.txtTitulo);
        listaCuentas.get(position).addLabel("importeTotal",holder.txtImporte);

        holder.buttVerDetalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aquí se abrirá un formulario que llevará a los detalles
                Intent intento=new Intent(context,pant_principal_cuenta.class);

                intento.putExtra("idCuenta",listaCuentas.get(position).id);
                //intento.putExtra("cuenta",listaCuentas.get(position).getCuentaSerializable());

                ((Activity) context).startActivityForResult(intento,3);

            }
        });




        holder.buttNuevoGasto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aquí se abrirá el formulario para el nuevo gasto
                Intent intento=new Intent(context,formNuevoGasto.class);

                intento.putExtra("idCuenta",listaCuentas.get(position).id);
                //intento.putExtra("cuentaSerializable",listaCuentas.get(position).getCuentaSerializable());
                intento.putExtra("esNuevoGasto",true);
                //intento.putExtra("nombres",listaCuentas.get(position).getListaUnicoString());
                //intento.putExtra("titulo",listaCuentas.get(position).titulo);
                //esto lo puse para algo, pero ya me acordaré de qué
                //intento.putExtra("idMovimiento","");

                //context.startActivity(intento);

                //Activity a=new Activity();
                //a.startActivityForResult(intento,2);

                ((Activity) context).startActivityForResult(intento,2);

                //context.startActivity(new Intent(context,formNuevoGasto.class));

            }
        });

/*
        myRef.child(Long.toString(listaCuentas.get(position).id)).child("importeTotal").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaCuentas.get(position).importeTotal=Double.parseDouble( dataSnapshot.getValue().toString());
                holder.txtImporte.setText(dataSnapshot.getValue().toString()+"€");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        myRef.child(Long.toString(listaCuentas.get(position).id)).child("titulo").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("mensaje","se va a intentar leer el título del id:"+Long.toString( listaCuentas.get(position).id));
                listaCuentas.get(position).titulo=dataSnapshot.getValue().toString();
                holder.txtTitulo.setText(dataSnapshot.getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
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
