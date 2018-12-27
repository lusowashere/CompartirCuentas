package com.example.lusog.compartircuentas;

import android.content.Context;
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
import java.util.Iterator;

class Cuenta implements Serializable {
    //private ArrayList<String> listaNombres;

    public ArrayList<Persona> listaNombres;
    public long id;
    public String titulo, descripcion;
    public ArrayList<Movimiento> movimientosCuenta;
    //public double importeTotal;



    /////CONSTRUCTORES



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


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void guardarImporteTotal(){

        FirebaseDatabase.getInstance().getReference("listas").child(Long.toString(id)).child("importeTotal").setValue(getImporteTotal());
    }

    public double getImporteTotal(){
        double importeTotal=0;
        double nuevoImporte=0;


        Log.e("mensaje","Calculando importe - importe anterior:"+importeTotal+"€");

        for(Movimiento mov:movimientosCuenta){
            importeTotal+=mov.cantidad;/*
            Log.e("mensaje","añadido importe de movimiento "+mov.toString());
            Log.e("mensaje","importeTotal:"+importeTotal+"€");*/
        }

        //redondeo por si acaso
        importeTotal=(double) Math.round(importeTotal*100)/100;
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
        listaNombres.add(new Persona(nuevoNombre));
    }
    public void remove_nombre_from_lista_nombre(String nombre_a_quitar){
        listaNombres.remove(nombre_a_quitar);
    }


    public String getListaUnicoString(){
        String str="";
        int i=0;

        for(Persona n:listaNombres){
            if(i>0){
                str+= ";";
            }
            str+=n.nombre;
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

    public double getImportePromedio(){//no lo pongo como fijo, se calcula  cada vez que es lo mejor

        if(listaNombres.size()>0) {

            return (double)  Math.round(100*getImporteTotal() / getNumeroPersonas())/100;
        }else{
            return getImporteTotal();
        }
    }

    public int getNumeroPersonas(){
        return listaNombres.size();
    }


    public void calcular_pagado_y_deudas(){

        //ponemos todo a 0
        for(Persona p:listaNombres){
            p.totalPagado=0.0;
            p.totalBeneficiado=0.0;
            p.numeroMovimientos=0;
        }

        for(Movimiento mov:movimientosCuenta){
            for(Persona pers:listaNombres){
                if(mov.pagador.equals(pers.nombre)){//es el pagador
                    pers.totalPagado+=mov.cantidad;
                    pers.numeroMovimientos++;
                }

                if(mov.esDisfrutante(pers.nombre)){
                    pers.totalBeneficiado+=mov.getCantidadPromedio();
                }
            }
        }
    }



    public void addMovimiento(Movimiento nuevoMovimiento){//añado movimientos a la lista y a la gente a la que lo ha hecho
        movimientosCuenta.add(nuevoMovimiento);
        calcular_pagado_y_deudas();


    }


    public void quitarMovimiento(String idMovimientoQuitado){
        Iterator<Movimiento> iter=movimientosCuenta.iterator();

        while(iter.hasNext()){
            if(iter.next().id.equals(idMovimientoQuitado)){
                iter.remove();
            }
        }

        calcular_pagado_y_deudas();

    }

    public void modificarMovimiento(Movimiento movimientoModificado){
        for(Movimiento m:movimientosCuenta){
            if(m.id.equals(movimientoModificado.id)){
                m.copiarDeOtroMovimiento(movimientoModificado);
                calcular_pagado_y_deudas();
            }
        }
    }

    public int getNumeroMovimientos(){return movimientosCuenta.size();}

    public Persona getPersona(String nombrePersona){

        for(Persona pers:listaNombres){
            if(pers.nombre.equals(nombrePersona)){
                return pers;
            }
        }


        return new Persona("");

    }

}
