package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

class Cuenta {
    //private ArrayList<String> listaNombres;

    public ArrayList<Persona> listaNombres;
    public long id;
    public String titulo, descripcion;
    public ArrayList<Movimiento> movimientosCuenta;
    public double importeTotal;


    private TextView textoTitulo, textoImporteTotal,textoNpersonas,textoCostePromedio;
    private RecyclerView recycler;


    private FirebaseDatabase database;
    private DatabaseReference myRef;

    /////CONSTRUCTORES
    public Cuenta(final long idCuenta, final boolean leerMovimientos, final boolean rellenarTextViews){//constructor que toma únicamente el id y lee toda la información de firebase
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        movimientosCuenta=new ArrayList<>();

        id=idCuenta;

        listaNombres=new ArrayList<>();

        myRef.child(Long.toString(idCuenta)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //id=Long.parseLong( dataSnapshot.child("id").getValue().toString());

                Log.e("mensaje:","id que estoy leyendo:"+idCuenta);

                //id=idCuenta;
                titulo=dataSnapshot.child("titulo").getValue().toString();
                descripcion=dataSnapshot.child("descripcion").getValue().toString();
                importeTotal=Double.parseDouble(dataSnapshot.child("importeTotal").getValue().toString());

                //añado los nombres
                String ristraNombres=dataSnapshot.child("participantes").getValue().toString();
                listaNombres=new ArrayList<>();
                for(String n:ristraNombres.split(";")){
                    Log.e("mensaje","añadir nombre '"+n+"'");
                    listaNombres.add(new Persona(n));
                }



                //si es la pantalla principal, relleno los textBoxes
                if(rellenarTextViews) {
                    textoTitulo.setText(titulo);
                    textoImporteTotal.setText(Double.toString(importeTotal)+"€");
                    textoNpersonas.setText(listaNombres.size()+" personas");
                    textoCostePromedio.setText( getImportePromedio()+"€/persona");
                    AdaptadorPersonas adapter=new AdaptadorPersonas(listaNombres,getImportePromedio());
                    recycler.setAdapter(adapter);
                }

                //Log.e("mensaje","Leida cuenta "+id+" con titulo '"+titulo+"'");

                if(leerMovimientos) {

                    //añado los movimientos
                    //movimientosCuenta=new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.child("movimientos").getChildren()) {
                        Movimiento m = postSnapshot.getValue(Movimiento.class);
                        addMovimiento(m);
                        //Log.e("mensaje","leido movimiento "+m.toString());

                        if(rellenarTextViews){recycler.getAdapter().notifyDataSetChanged();}

                    }


                    calcularImporteTotal();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    public Cuenta(long id, String titulo, String descripcion){
        this.id=id;
        this.titulo=titulo;
        this.descripcion=descripcion;
        listaNombres=new ArrayList<>();
        movimientosCuenta=new ArrayList<>();
    }

    public Cuenta(String titulo,String descripcion){
        this.titulo=titulo;
        this.descripcion=descripcion;
        id=crearIdConFecha();
        listaNombres=new ArrayList<>();

        movimientosCuenta=new ArrayList<>();

    }

    public Cuenta(){
        id=crearIdConFecha();
        listaNombres=new ArrayList<>();

        movimientosCuenta=new ArrayList<>();
    }


    ///METODOS



    public void calcularImporteTotal(){
        importeTotal=0;
        double nuevoImporte=0;


        Log.e("mensaje","Calculando importe - importe anterior:"+importeTotal+"€");

        for(Movimiento mov:movimientosCuenta){
            importeTotal+=mov.cantidad;
            Log.e("mensaje","añadido importe de movimiento "+mov.toString());
            Log.e("mensaje","importeTotal:"+importeTotal+"€");
        }

        //redondeo por si acaso
        importeTotal=(double) Math.round(importeTotal*100)/100;

        guardarImporteTotal();
        Log.e("mensaje","importeTotaldentrodefuncion:"+importeTotal+"€");
    }

    public void guardarImporteTotal(){

        myRef.child(Long.toString(id)).child("importeTotal").setValue(importeTotal);
    }

    public double getImporteTotal(){
        return  importeTotal;
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
        listaNombres.add(new Persona(nuevoNombre));
    }
    public void remove_nombre_from_lista_nombre(String nombre_a_quitar){
        listaNombres.remove(nombre_a_quitar);
    }


    public String getListaUnicoString(){
        String str="";
        int i=0;

        for(Persona n:listaNombres){
            if(i>0){
                str+= ";";
            }
            str+=n.nombre;
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

    public double getImportePromedio(){//no lo pongo como fijo, se calcula  cada vez que es lo mejor

        if(listaNombres.size()>0) {

            return (double)  Math.round(100*importeTotal / listaNombres.size())/100;
        }else{
            return importeTotal;
        }
    }

    public int getNumeroPersonas(){
        return listaNombres.size();
    }

    //esto se utiliza sólamente cuando se está leyendo toda la info para la pantalla general
    public void setTextViews(TextView labelTitulo, TextView labelImporte, TextView labelPersonas, TextView labelPromedio, RecyclerView reciclerNombres){
        textoTitulo=labelTitulo;
        textoImporteTotal=labelImporte;
        textoNpersonas=labelPersonas;
        textoCostePromedio=labelPromedio;

        recycler=reciclerNombres;
        //aquí además le asigno la lista
        /*AdaptadorPersonas adapter=new AdaptadorPersonas(listaNombres);
        recycler.setAdapter(adapter);*/

    }


    public void addMovimiento(Movimiento nuevoMovimiento){//añado movimientos a la lista y a la gente a la que lo ha hecho
        movimientosCuenta.add(nuevoMovimiento);



        for(Persona p:listaNombres){
            if(nuevoMovimiento.Nombre.equals(p.nombre)){//se le añade también el movimiento a la persona
                p.addMovimiento(nuevoMovimiento,getImportePromedio());
            }
        }

    }


}
