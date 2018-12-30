package com.example.lusog.compartircuentas;

public class InformacionMovimiento //esta clase sirve solo para Guardar en firebase
{
    public String Nombre,concepto,fecha,id, participantes;
    public Double cantidad;
    public boolean participanTodos;



    public InformacionMovimiento(String nombre, String concepto, String fecha, String id, Double cantidad/*, String participantes*/) {
        Nombre = nombre;
        this.concepto = concepto;
        this.fecha = fecha;
        this.id = id;
        this.cantidad = cantidad;
        //this.participantes=participantes;
        this.participantes="";
        this.participanTodos=true;
    }

    //en este no participan todos
    public InformacionMovimiento(String nombre, String concepto, String fecha, String id, Double cantidad, String participantes){
        this(nombre, concepto, fecha, id, cantidad);
        this.participanTodos=false;
        this.participantes=participantes;
    }
}
