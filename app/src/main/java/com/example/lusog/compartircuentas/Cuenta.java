package com.example.lusog.compartircuentas;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class Cuenta {
    public ArrayList<String> listaNombres;
    public long id;
    public String titulo, descripcion;
    public List<Movimiento> movimientosCuenta;

    public Cuenta(long id, String titulo, String descripcion){
        this.id=id;
        this.titulo=titulo;
        this.descripcion=descripcion;
        listaNombres=new ArrayList<>();
    }

    public Cuenta(String titulo,String descripcion){
        this.titulo=titulo;
        this.descripcion=descripcion;
        id=crearIdConFecha();
        listaNombres=new ArrayList<>();

    }

    public Cuenta(){
        id=crearIdConFecha();
        listaNombres=new ArrayList<>();
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
