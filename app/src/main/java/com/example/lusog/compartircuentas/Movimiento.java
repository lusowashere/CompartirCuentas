package com.example.lusog.compartircuentas;

public class Movimiento {
    public double cantidad;
    public String concepto, Nombre;

    public Movimiento(double cantidad, String concepto, String nombre) {
        this.cantidad = cantidad;
        this.concepto = concepto;
        this.Nombre = nombre;
    }
}
