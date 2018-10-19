package com.example.lusog.compartircuentas;

import android.util.Log;

import java.util.Calendar;
import java.util.List;

class Cuenta {
    public List<String> listaNombres;
    public long id;
    public String titulo, descripcion;
    public List<Movimiento> movimientosCuenta;

    public Cuenta(long id, String titulo, String descripcion){
        this.id=id;
        this.titulo=titulo;
        this.descripcion=descripcion;
    }

    public Cuenta(String titulo,String descripcion){
        this.titulo=titulo;
        this.descripcion=descripcion;
        id=crearIdConFecha();

    }

    public Cuenta(){
        id=crearIdConFecha();
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

}
