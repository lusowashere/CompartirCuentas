package com.example.lusog.compartircuentas;

import java.io.Serializable;

public class InformacionCuenta implements Serializable {
    public long id;
    public String titulo, descripcion,participantes;
    public double importeTotal;

    public InformacionCuenta(long id, String titulo, String descripcion, double importeTotal, String participantes) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.importeTotal = importeTotal;
        this.participantes=participantes;
    }


}
