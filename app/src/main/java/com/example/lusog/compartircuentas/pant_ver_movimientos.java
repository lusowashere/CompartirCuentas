package com.example.lusog.compartircuentas;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class pant_ver_movimientos extends AppCompatActivity {

    Long numeroCuenta;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerMovimientos;
    String nombrePersona,tituloCuenta;
    ArrayList<Movimiento> listaMovimientos; //aquí la separamos para diferenciar los que son de todos de los que son sólo del usuario
    boolean sonMovimientosPersona;

    TextView textoTitulo,textoMovimientos,textoTotal,textoDeuda;


    Cuenta cuentaActual;
    Persona persona;

    //Cuenta cuentaActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_ver_movimientos);


        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        listaMovimientos=new ArrayList<>();

        Intent intento=getIntent();

        //cuentaActual=(Cuenta) intento.getSerializableExtra("cuenta");

        numeroCuenta=intento.getLongExtra("numeroCuenta",0);

        sonMovimientosPersona=intento.getBooleanExtra("sonMovimientosPersona",true);

        textoTitulo=findViewById(R.id.labelTitulo);
        textoMovimientos=findViewById(R.id.labelMovimientos);
        textoTotal=findViewById(R.id.labelImporteTotal);
        textoDeuda=findViewById(R.id.labelDeuda);

        recyclerMovimientos=findViewById(R.id.reciclerViewMovimientos);


        tituloCuenta=intento.getStringExtra("tituloCuenta");

        if(sonMovimientosPersona){
            nombrePersona= intento.getStringExtra("persona");
            listaMovimientos=new ArrayList<>();
            textoTitulo.setText(nombrePersona);
            cuentaActual=new Cuenta(numeroCuenta,"","");
            /*
            textoMovimientos.setText(String.valueOf(persona.numeroMovimientos)+" movimientos");
            textoTotal.setText(String.format("%.2f",persona.totalPagado)+"€");
            textoDeuda.setText(persona.getDeuda());
            if(persona.deuda>0){
                textoDeuda.setTextColor(Color.RED);
            }else{
                textoDeuda.setTextColor(Color.rgb(0, 102, 0));
            }
            */
        }else{
            textoTitulo.setText(tituloCuenta);
            cuentaActual=new Cuenta(numeroCuenta,tituloCuenta,"");
        }

        String participantes=intento.getStringExtra("participantes");

        cuentaActual.setListaFromUnicoString(participantes);


        recyclerMovimientos.setLayoutManager(new  LinearLayoutManager(this));

        AdaptadorMovimientos adapter;

        if(sonMovimientosPersona) {
            adapter = new AdaptadorMovimientos(listaMovimientos, numeroCuenta,participantes,tituloCuenta);
        }
        else{
             adapter=new AdaptadorMovimientos(cuentaActual.movimientosCuenta,numeroCuenta,participantes,tituloCuenta);
        }

        recyclerMovimientos.setAdapter(adapter);

        myRef.child(numeroCuenta.toString()).child("movimientos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String disfrutantes;
                if(dataSnapshot.hasChild("disfrutantes")){
                    disfrutantes=dataSnapshot.child("disfrutantes").getValue().toString();
                }else{
                    disfrutantes=cuentaActual.getListaUnicoString();
                }

                Movimiento nuevoMovimiento=new Movimiento(Double.parseDouble( dataSnapshot.child("cantidad").getValue().toString()),
                        dataSnapshot.child("concepto").getValue().toString(),
                        dataSnapshot.child("Nombre").getValue().toString(),
                        dataSnapshot.child("id").getValue().toString(),disfrutantes,
                        dataSnapshot.child("fecha").getValue().toString()
                );

                cuentaActual.addMovimiento(nuevoMovimiento);

                if(sonMovimientosPersona && nuevoMovimiento.pagador.equals(nombrePersona)){//se añade a la lista de movimientos
                    listaMovimientos.add(nuevoMovimiento);
                }

                actualizarInformacion();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String disfrutantes;
                if(dataSnapshot.hasChild("disfrutantes")){
                    disfrutantes=dataSnapshot.child("disfrutantes").getValue().toString();
                }else{
                    disfrutantes=cuentaActual.getListaUnicoString();
                }

                Movimiento movimientoModificado=new Movimiento(Double.parseDouble( dataSnapshot.child("cantidad").getValue().toString()),
                        dataSnapshot.child("concepto").getValue().toString(),
                        dataSnapshot.child("Nombre").getValue().toString(),
                        dataSnapshot.child("id").getValue().toString(),disfrutantes,
                        dataSnapshot.child("fecha").getValue().toString()
                );

                cuentaActual.modificarMovimiento(movimientoModificado);

                if(sonMovimientosPersona){
                    listaMovimientos.clear();
                    for(Movimiento mov:cuentaActual.movimientosCuenta){
                        if(mov.pagador.equals(nombrePersona)){
                            listaMovimientos.add(mov);
                        }
                    }
                }

                actualizarInformacion();


            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String idRemovido=dataSnapshot.child("id").getValue().toString();
                cuentaActual.quitarMovimiento(idRemovido);

                if(sonMovimientosPersona){
                    listaMovimientos.clear();
                    for(Movimiento mov:cuentaActual.movimientosCuenta){
                        if(mov.pagador.equals(nombrePersona)){
                            listaMovimientos.add(mov);
                        }
                    }
                }

                actualizarInformacion();

                //aquí además actualizamos el importe total porque si no se queda incorrecto
                cuentaActual.guardarImporteTotal();

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }//fin de la función onCreate


    public void actualizarInformacion(){
        if(sonMovimientosPersona) {
            textoMovimientos.setText(String.valueOf(listaMovimientos.size()) + " movimientos");
            textoTotal.setText(cuentaActual.getPersona(nombrePersona).getTotalPagado());
            textoDeuda.setText(cuentaActual.getPersona(nombrePersona).getStringDeuda());
            if(cuentaActual.getPersona(nombrePersona).getDeuda()>0){
                textoDeuda.setTextColor(Color.RED);
            }else{
                textoDeuda.setTextColor(Color.rgb(0, 102, 0));
            }

        }else{
            textoMovimientos.setText(cuentaActual.getNumeroMovimientos()+" movimientos");
            textoTotal.setText(String.format("%.2f",cuentaActual.getImporteTotal())+"€");
            textoDeuda.setVisibility(View.INVISIBLE);
        }
        recyclerMovimientos.getAdapter().notifyDataSetChanged();
    }



}
