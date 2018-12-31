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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

class Cuenta implements Serializable {
    //private ArrayList<String> listaNombres;

    public ArrayList<Persona> listaNombres;
    public long id;
    public String titulo, descripcion;
    public ArrayList<Movimiento> movimientosCuenta;
    //public double importeTotal;

    public ArrayList<Movimiento> ajustesCuenta;

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

    public long crearIdMovimientoConFecha(){
        long idPropuesto=crearIdConFecha();
        Calendar ahora;
        ahora=Calendar.getInstance();

        idPropuesto=idPropuesto*100+ahora.get(Calendar.SECOND);

        return  idPropuesto;

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

    public void ajustarCuentas(){//crea una serie de movimientos con los cuales se ajustarían las cuentas
        ajustesCuenta=new ArrayList<>();

        ArrayList<Persona> personasAux=new ArrayList<>();


        for(Persona p:listaNombres){
            //creamos una nueva persona, y en lugar de lo pagado ponemos la deuda
            Persona copiaPersona=new Persona(p.nombre);
            copiaPersona.totalPagado=p.getDeuda();
            personasAux.add(copiaPersona);
        }


        int deudasDistintasDeCero=personasAux.size();


        int contadorTemporal=0;

        while(deudasDistintasDeCero>1 && contadorTemporal<30) {
            contadorTemporal++;
            Collections.sort(personasAux, new Comparator<Persona>() {
                @Override
                public int compare(Persona o1, Persona o2) {
                    return o1.totalPagado.compareTo(o2.totalPagado);
                }
            });

           /*Log.e("ajustes", "orden tras lista");
            for (Persona temp : personasAux) {
                Log.e("ajustes", "Nombre:" + temp.nombre + "  -  Deuda:" + temp.totalPagado);
            }*/

            //siempre intento que el que más debe pague al que más se le debe
            Double totalDelDeudor=personasAux.get(personasAux.size()-1).totalPagado;
            Double totalDelDebido=personasAux.get(0).totalPagado;

            Double cantidadPagada=Math.min(Math.abs( totalDelDeudor),Math.abs(totalDelDebido));//como mucho pago o lo que debe uno o lo que se le debe al otro

            //creo el movimiento y lo añado a la lista
            //Log.e("ajustes",personasAux.get(personasAux.size()-1).nombre+" paga "+cantidadPagada+"€ a "+personasAux.get(0).nombre);
            Movimiento nuevoMovimiento=new Movimiento(cantidadPagada,
                    "ajuste"+contadorTemporal,
                    personasAux.get(personasAux.size()-1).nombre,
                    "ajuste-"+crearIdMovimientoConFecha()+contadorTemporal,false,
                    personasAux.get(0).nombre,
                    getStringFecha()
            );

            ajustesCuenta.add(nuevoMovimiento);

            //recalculo
            personasAux.get(personasAux.size()-1).totalPagado-=cantidadPagada;
            personasAux.get(0).totalPagado+=cantidadPagada;



            //compruebo si se han ajustado las cuentas
            deudasDistintasDeCero=0;
            for(Persona comprobarPersona:personasAux){
                if(comprobarPersona.totalPagado!=0){
                    deudasDistintasDeCero++;
                }
            }

        }
    }

    public String getStringFecha(){
        String stringFecha;

        Calendar ahora;
        ahora=Calendar.getInstance();

        stringFecha =Integer.toString( ahora.get(Calendar.DAY_OF_MONTH));
        stringFecha+="/"+(ahora.get(Calendar.MONTH)+1);
        stringFecha+="/"+ahora.get(Calendar.YEAR);

        return stringFecha;
    }

}
