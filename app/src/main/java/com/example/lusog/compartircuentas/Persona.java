package com.example.lusog.compartircuentas;

import java.io.Serializable;
import java.util.ArrayList;

public class Persona implements Serializable {
    public String nombre;
    public Double totalPagado, deuda;
    public ArrayList<Movimiento> movimientos;
    public int numeroMovimientos;

    public Persona(String nombre){
        this.nombre=nombre;
        movimientos=new ArrayList<>();
        totalPagado=0.0;
        deuda=0.0;
        numeroMovimientos=0;
    }

    public void addMovimiento(Movimiento nuevoMovimiento){
        movimientos.add(nuevoMovimiento);
        calcularTotalPagado_totalMovimientos();
        //calcularDeuda(importePromedio);
    }


    public void calcularDeuda(double importePromedio){
        deuda=importePromedio-totalPagado;
    }

    private void calcularTotalPagado_totalMovimientos(){

        double total=0;

        for(Movimiento m:movimientos){
            total+=m.cantidad;
        }

        totalPagado=total;
        numeroMovimientos= movimientos.size();
    }

    public String getTotalPagado(){//hace redondeo

        return String.format("%.2f",totalPagado)+"€ Pagados";

    }


    public String getDeuda(){

        //deuda=importePromedio-totalPagado;

        if(deuda>=0){
            //return "Debe "+ Double.toString(Math.round(100*deuda)/100)+"€";
            return "Debe "+String.format("%.2f",deuda)+"€";
        }
        else{
            //return "Se le deben "+Double.toString(Math.round(-100*deuda)/100)+"€";
            return "Se le deben "+String.format("%.2f",(-deuda))+"€";
        }
    }
}
