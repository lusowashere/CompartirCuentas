package com.example.lusog.compartircuentas;

import java.io.Serializable;
public class Persona implements Serializable {
    public String nombre;
    public Double totalPagado, totalBeneficiado;
    public int numeroMovimientos;

    public Persona(String nombre){
        this.nombre=nombre;
        totalPagado=0.0;
        totalBeneficiado=0.0;
        numeroMovimientos=0;
    }



    public Double getDeuda(){
        return totalBeneficiado-totalPagado;
    }



    public String getTotalPagado(){//hace redondeo

        return String.format("%.2f",totalPagado)+"€ Pagados";

    }

    public int getNumeroMovimientos(){return  numeroMovimientos;}


    public String getStringDeuda(){

        //deuda=importePromedio-totalPagado;

        if(getDeuda()>=0){
            //return "Debe "+ Double.toString(Math.round(100*deuda)/100)+"€";
            return "Debe "+String.format("%.2f",getDeuda())+"€";
        }
        else{
            //return "Se le deben "+Double.toString(Math.round(-100*deuda)/100)+"€";
            return "Se le deben "+String.format("%.2f",(-getDeuda()))+"€";
        }
    }


}
