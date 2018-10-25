package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

class Cuenta {
    public ArrayList<String> listaNombres;
    public long id;
    public String titulo, descripcion;
    public ArrayList<Movimiento> movimientosCuenta;
    public double importeTotal;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    /////CONSTRUCTORES
    public Cuenta(final long idCuenta){//constructor que toma únicamente el id y lee toda la información de firebase
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        movimientosCuenta=new ArrayList<>();

        id=idCuenta;

        myRef.child(Long.toString(idCuenta)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //id=Long.parseLong( dataSnapshot.child("id").getValue().toString());

                Log.e("mensaje:","id que estoy leyendo:"+idCuenta);

                //id=idCuenta;
                titulo=dataSnapshot.child("titulo").getValue().toString();
                descripcion=dataSnapshot.child("descripcion").getValue().toString();

                //añado los nombres
                String ristraNombres=dataSnapshot.child("participantes").getValue().toString();
                listaNombres=new ArrayList<>();
                for(String n:ristraNombres.split(";")){
                    Log.e("mensaje","añadir nombre '"+n+"'");
                    listaNombres.add(n);
                }

                //Log.e("mensaje","Leida cuenta "+id+" con titulo '"+titulo+"'");

                //añado los movimientos
                //movimientosCuenta=new ArrayList<>();
                for(DataSnapshot postSnapshot:dataSnapshot.child("movimientos").getChildren()){
                    Movimiento  m=postSnapshot.getValue(Movimiento.class);
                    movimientosCuenta.add(m);
                    //Log.e("mensaje","leido movimiento "+m.toString());

                }

                calcularImporteTotal();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public Cuenta(long id, String titulo, String descripcion){
        this.id=id;
        this.titulo=titulo;
        this.descripcion=descripcion;
        listaNombres=new ArrayList<>();
        movimientosCuenta=new ArrayList<>();
    }

    public Cuenta(String titulo,String descripcion){
        this.titulo=titulo;
        this.descripcion=descripcion;
        id=crearIdConFecha();
        listaNombres=new ArrayList<>();

        movimientosCuenta=new ArrayList<>();

    }

    public Cuenta(){
        id=crearIdConFecha();
        listaNombres=new ArrayList<>();

        movimientosCuenta=new ArrayList<>();
    }


    ///METODOS



    public void calcularImporteTotal(){
        importeTotal=0;
        double nuevoImporte=0;


        Log.e("mensaje","Calculando importe - importe anterior:"+importeTotal+"€");

        for(Movimiento mov:movimientosCuenta){
            importeTotal+=mov.cantidad;
            Log.e("mensaje","añadido importe de movimiento "+mov.toString());
            Log.e("mensaje","importeTotal:"+importeTotal+"€");
        }

        //redondeo por si acaso
        importeTotal=(double) Math.round(importeTotal*100)/100;

        guardarImporteTotal();
        Log.e("mensaje","importeTotaldentrodefuncion:"+importeTotal+"€");
    }

    public void guardarImporteTotal(){

        myRef.child(Long.toString(id)).child("importeTotal").setValue(importeTotal);
    }

    public double getImporteTotal(){
        return  importeTotal;
    }

    public long crearIdConFecha(){
        long idPropuesto;

        Calendar ahora;
        ahora=Calendar.getInstance();

        idPropuesto=(int) ahora.get( Calendar.YEAR);
        idPropuesto=idPropuesto*100+ahora.get(Calendar.MONTH)+1;
        idPropuesto=idPropuesto*100+ahora.get(Calendar.DAY_OF_MONTH);
        idPropuesto=idPropuesto*100+ahora.get(Calendar.HOUR_OF_DAY);
        idPropuesto=idPropuesto*100+ahora.get(Calendar.MINUTE);

        //el del segundo lo dejo solo para los movimientos
        //idPropuesto=idPropuesto*100+ahora.get(Calendar.SECOND);




        return idPropuesto;

    }

    public void add_nombre_a_lista_nombres(String nuevoNombre){
        listaNombres.add(nuevoNombre);
    }
    public void remove_nombre_from_lista_nombre(String nombre_a_quitar){
        listaNombres.remove(nombre_a_quitar);
    }


    public String getListaUnicoString(){
        String str="";
        int i=0;

        for(String n:listaNombres){
            if(i>0){
                str+= ";";
            }
            str+=n;
            i++;
        }


        return str;
    }


    public void setListaFromUnicoString(String strFirebase){
        String[] arrayNombres;
        arrayNombres=strFirebase.split(";");

        listaNombres.clear();

        for(String n:arrayNombres){
            add_nombre_a_lista_nombres(n);
        }

    }

}
