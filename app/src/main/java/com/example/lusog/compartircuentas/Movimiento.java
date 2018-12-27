package com.example.lusog.compartircuentas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Movimiento implements Serializable {
    public double cantidad;
    public String concepto, pagador, id,disfrutantes;
    public String fecha;

    public Movimiento(double cantidad, String concepto, String pagador, String id, String disfrutantes, String fecha) {
        this.cantidad = cantidad;
        this.concepto = concepto;
        this.pagador = pagador;
        this.id = id;
        this.disfrutantes = disfrutantes;
        this.fecha = fecha;
    }

    //constructor necesario para las llamadas de firebase
    public Movimiento(){

    }


    public String toString(){
        return "Cantidad:"+cantidad+"€ concepto:'"+concepto+"' pagador:'"+pagador+"' fecha:"+fecha+" id:"+id;
    }

    public void copiarDeOtroMovimiento(Movimiento otroMov){
        cantidad=otroMov.cantidad;
        concepto=otroMov.concepto;
        pagador=otroMov.pagador;
        id=otroMov.id;
        fecha=otroMov.fecha;
        disfrutantes=otroMov.disfrutantes;
    }


    public ArrayList<String> getListaDisfrutantes(){
        ArrayList<String> lista=new ArrayList<>();

        for(String nombre:disfrutantes.split(";")){
            lista.add(nombre);
        }

        return lista;
    }

    public int getNumeroDisfrutantes(){
        return getListaDisfrutantes().size();
    }

    public Double getCantidadPromedio(){
        return (double)  Math.round(100*cantidad / getNumeroDisfrutantes())/100;
    }

    public boolean esDisfrutante(String nombre){
        boolean loEs=false;
        for(String disf:getListaDisfrutantes()){
            if(nombre.equals(disf)){loEs=true;}
        }

        return loEs;
    }

    public InformacionMovimiento getInfoMovimiento(){//para guardar en firebase
        return new InformacionMovimiento(pagador,concepto,fecha,id,cantidad,disfrutantes);
    }

}
