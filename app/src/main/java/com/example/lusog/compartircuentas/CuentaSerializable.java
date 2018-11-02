package com.example.lusog.compartircuentas;

import java.io.Serializable;
import java.util.ArrayList;

public class CuentaSerializable implements Serializable {

    public ArrayList<Persona> listaNombres;
    public long id;
    public String titulo, descripcion;
    public ArrayList<Movimiento> movimientosCuenta;
    public double importeTotal;


    public CuentaSerializable(Cuenta2 cuentaOriginal){
        listaNombres=cuentaOriginal.listaNombres;
        id=cuentaOriginal.id;
        titulo=cuentaOriginal.titulo;
        movimientosCuenta=cuentaOriginal.movimientosCuenta;
        importeTotal=cuentaOriginal.importeTotal;
    }


}
