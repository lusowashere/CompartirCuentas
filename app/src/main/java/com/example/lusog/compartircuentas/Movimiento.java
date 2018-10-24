package com.example.lusog.compartircuentas;

import java.util.Date;

public class Movimiento {
    public double cantidad;
    public String concepto, Nombre, id;
    public String fecha;

    public Movimiento(double cantidad, String concepto, String nombre, String fecha, String id) {
        this.cantidad = cantidad;
        this.concepto = concepto;
        this.Nombre = nombre;
        this.fecha=fecha;
        this.id=id;
    }

    //constructor necesario para las llamadas de firebase
    public Movimiento(){

    }


    public String toString(){
        return "Cantidad:"+cantidad+"€ concepto:'"+concepto+"' pagador:'"+Nombre+"' fecha:"+fecha+" id:"+id;
    }

}
