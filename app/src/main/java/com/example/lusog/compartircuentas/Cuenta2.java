package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Cuenta2 {

    public ArrayList<Persona> listaNombres;
    public long id;
    public String titulo, descripcion, ristraPersonas;
    public ArrayList<Movimiento> movimientosCuenta;
    public double importeTotal;

    private FirebaseDatabase database;
    private DatabaseReference myRef;



    //labels asociadas a la información
    private TextView   labelTitulo,labelImporteTotal   ,labelImportePromedio   , labelNumeroPersonas;
    private boolean hayLabelTitulo,hayLabelImporteTotal,hayLabelImportePromedio, hayLabelNumeroPersonas, hayRecicler;
    private RecyclerView recicler;


    //CONSTRUCTORES

    public Cuenta2()//inicializador de todo lo nuevo
    {
        listaNombres=new ArrayList<>();
        movimientosCuenta=new ArrayList<>();
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");
        ristraPersonas="";

        //en principio no hay ningún tipo de labels
        hayLabelImportePromedio=false;
        hayLabelImporteTotal=false;
        hayLabelNumeroPersonas=false;
        hayLabelTitulo=false;
        hayRecicler=false;
    }

    public Cuenta2(Long idCuenta)//coge toda la información de firebase de esta cuenta
    {
        this();//llamo al constructor sin parámetros para inicializar todo

        id=idCuenta;

        //cojo todos los valores de la cuenta de firebase para empezar
        myRef.child(Long.toString(idCuenta)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titulo=dataSnapshot.child("titulo").getValue().toString();
                descripcion=dataSnapshot.child("descripcion").getValue().toString();
                importeTotal=Double.parseDouble( dataSnapshot.child("importeTotal").getValue().toString());
                setListaFromUnicoString(dataSnapshot.child("participantes").getValue().toString());
                for(DataSnapshot snapshotMovimientos:dataSnapshot.child("movimientos").getChildren()){
                    addMovimiento(snapshotMovimientos.getValue(Movimiento.class));
                    if(hayRecicler){
                        recicler.getAdapter().notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });


    }

    public Cuenta2(CuentaSerializable cuentaSerializable){
        this();
        listaNombres=cuentaSerializable.listaNombres;
        id=cuentaSerializable.id;
        titulo=cuentaSerializable.titulo;
        movimientosCuenta=cuentaSerializable.movimientosCuenta;
        importeTotal=cuentaSerializable.importeTotal;
    }


  //FUNCIONES

    public void setRecycler(RecyclerView recicler){
        this.recicler=recicler;
        hayRecicler=true;
    }

    public void addLabel(String nombreLabel,TextView label){

        //FALTA AÑADIR LOS LISTENERS!!!
        switch (nombreLabel){
            case "importePromedio":
                labelImportePromedio=label;
                hayLabelImportePromedio=true;

                Double importePromedio=importeTotal/getNumeroPersonas();
                labelImportePromedio.setText(String.format("%.2f",importePromedio)+"€/persona");

                break;
            case "titulo":
                labelTitulo=label;
                hayLabelTitulo=true;

                labelTitulo.setText(titulo);

                myRef.child(Long.toString(id)).child("titulo").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        titulo=dataSnapshot.getValue().toString();
                        labelTitulo.setText(titulo);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                break;
            case "importeTotal":
                labelImporteTotal=label;
                hayLabelImporteTotal=true;

                labelImporteTotal.setText(String.format("%.2f",importeTotal));

                myRef.child(Long.toString(id)).child("importeTotal").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        importeTotal=Double.parseDouble( dataSnapshot.getValue().toString());
                        labelImporteTotal.setText(String.format("%.2f",Double.parseDouble( dataSnapshot.getValue().toString()))+"€");

                        if(hayLabelImportePromedio){
                            Double importePromedio=importeTotal/getNumeroPersonas();
                            labelImportePromedio.setText(String.format("%.2f",importePromedio)+"€/persona");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });

                break;
            case "numeroPersonas":
                labelNumeroPersonas=label;
                hayLabelNumeroPersonas=true;

                labelNumeroPersonas.setText(String.valueOf( getNumeroPersonas()));

                //añadir personas es too much para mí, sólo cambio la cantidad de nombres y la ristra
                myRef.child(Long.toString(id)).child("participantes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ristraPersonas=String.valueOf( dataSnapshot.getValue());

                        int nuevoNumero=ristraPersonas.split(";").length;

                        labelNumeroPersonas.setText(nuevoNumero+" personas");

                        if(hayLabelImportePromedio){
                            Double importePromedio=importeTotal/nuevoNumero;
                            labelImportePromedio.setText(String.format("%.2f",importePromedio)+"€/persona");
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {}
                });


                break;
        }
    }



    public void add_nombre_a_lista_nombres(String nuevoNombre){
        listaNombres.add(new Persona(nuevoNombre));
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

    public String getListaUnicoString(){

        if(listaNombres.size()==0){
            return ristraPersonas;
        }else {
            String str = "";
            int i = 0;

            for (Persona n : listaNombres) {
                if (i > 0) {
                    str += ";";
                }
                str += n.nombre;
                i++;
            }


            return str;
        }
    }

    public void setListaFromUnicoString(String strFirebase){
        String[] arrayNombres;
        arrayNombres=strFirebase.split(";");

        listaNombres.clear();

        for(String n:arrayNombres){
            add_nombre_a_lista_nombres(n);
        }

    }


    public double getImportePromedio(){//no lo pongo como fijo, se calcula  cada vez que es lo mejor

        if(listaNombres.size()>0) {

            return (double)  Math.round(100*importeTotal / listaNombres.size())/100;
        }else{
            return importeTotal;
        }
    }

    public int getNumeroPersonas(){
        return listaNombres.size();
    }



    public void addMovimiento(Movimiento nuevoMovimiento){//añado movimientos a la lista y a la gente a la que lo ha hecho
        movimientosCuenta.add(nuevoMovimiento);

        double importePromedio=getImportePromedio();

        for(Persona p:listaNombres){
            /*if(nuevoMovimiento.Nombre.equals(p.nombre)){//se le añade también el movimiento a la persona
                p.addMovimiento(nuevoMovimiento);
            }

            //sea o no el afectado, le recalculo la deuda en base al nuevo importe promedio
            p.calcularDeuda(importePromedio); */
        }


    }

    public CuentaSerializable getCuentaSerializable(){
        return new CuentaSerializable(this);
    }



    public void calcularImporteTotal(boolean guardar){
        importeTotal=0;
        double nuevoImporte=0;


        Log.e("mensaje","Calculando importe - importe anterior:"+importeTotal+"€");

        for(Movimiento mov:movimientosCuenta){
            importeTotal+=mov.cantidad;
           // Log.e("mensaje","añadido importe de movimiento "+mov.toString());
            //Log.e("mensaje","importeTotal:"+importeTotal+"€");
        }

        //redondeo por si acaso
        importeTotal=(double) Math.round(importeTotal*100)/100;

        if(guardar){ myRef.child(Long.toString(id)).child("importeTotal").setValue(importeTotal);}
    }

}//fin de la clase persona2
