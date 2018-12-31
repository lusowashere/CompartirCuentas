package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.solver.widgets.Snapshot;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class formNuevoGasto extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference myRef;

    Long numeroCuenta;
    Movimiento nuevoMovimiento;
    Spinner comboBoxNombres;
    ArrayList<String> arrayNombres;
    String nombre;
    EditText txtBoxConcepto, txtBoxFecha,txtBoxImporte;
    TextView textoTitulo;
    CheckBox checkBoxSonTodosDisfrutantes;
    RecyclerView recyclerPosiblesDisfrutantes;
    Button botonApuntar;

    ArrayList<PosibleDisfrutante> listaPosiblesDisfrutantes;

    String idMovimiento;
    //Cuenta2 cuentaActual;
    Cuenta cuentaActual;
    boolean esNuevoGasto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_nuevo_gasto);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        txtBoxConcepto=findViewById(R.id.editTextConcepto);
        txtBoxFecha=findViewById(R.id.editTextFecha);
        txtBoxImporte=findViewById(R.id.editTextImporte);
        textoTitulo=findViewById(R.id.tituloCuenta);
        botonApuntar=findViewById(R.id.buttonApuntarGasto);


        Intent intento=getIntent();




        esNuevoGasto=intento.getBooleanExtra("esNuevoGasto",true);

        numeroCuenta=intento.getLongExtra("idCuenta",0);

        checkBoxSonTodosDisfrutantes=(CheckBox) findViewById(R.id.checkBoxSonTodosDisfrutantes);


        if(esNuevoGasto){
            idMovimiento=crearIdConFecha();
            txtBoxFecha.setText(getStringFecha());
            textoTitulo.setText(intento.getStringExtra("tituloCuenta") + " - Nuevo Gasto");
            botonApuntar.setText("Apuntar gasto");

        }else{
            idMovimiento=intento.getStringExtra("idMovimiento");
            txtBoxConcepto.setText( intento.getStringExtra("concepto"));
            txtBoxFecha.setText(intento.getStringExtra("fecha"));
            txtBoxImporte.setText(String.format("%.2f",intento.getDoubleExtra("importe",0)));
            textoTitulo.setText(intento.getStringExtra("tituloCuenta") + " - Modificar Gasto");
            botonApuntar.setText("Modificar gasto");
        }



        String ristraNombres=intento.getStringExtra("ristraNombres");
        arrayNombres=new ArrayList<>();
        listaPosiblesDisfrutantes=new ArrayList<>();

        for(String n:      ristraNombres.split(";")){
            //Log.e("mensaje","añadiendo nombre '"+n+"'");
            arrayNombres.add(n);
            listaPosiblesDisfrutantes.add(new PosibleDisfrutante(n));
        }

        nombre=arrayNombres.get(0);//entiendo que por defecto es el primero

        comboBoxNombres=findViewById(R.id.spinnerNombres);

        comboBoxNombres.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,arrayNombres));

        //apunto el pagador
        if(!esNuevoGasto){
            String pagador=intento.getStringExtra("pagador");
            comboBoxNombres.setSelection(arrayNombres.indexOf(pagador));
        }


        //recicler de los posibles disfrutantes
        recyclerPosiblesDisfrutantes=(RecyclerView) findViewById(R.id.recyclerPosiblesDisfrutantes);
        recyclerPosiblesDisfrutantes.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorPosiblesDisfrutantes adapter=new AdaptadorPosiblesDisfrutantes(listaPosiblesDisfrutantes);
        recyclerPosiblesDisfrutantes.setAdapter(adapter);



        if(esNuevoGasto){
            checkBoxSonTodosDisfrutantes.setChecked(true);
            recyclerPosiblesDisfrutantes.setVisibility(View.INVISIBLE);
        }else{
            if (intento.getBooleanExtra("sonTodosDisfrutantes", true)) {
                checkBoxSonTodosDisfrutantes.setChecked(true);
                recyclerPosiblesDisfrutantes.setVisibility(View.INVISIBLE);
            }else{
                checkBoxSonTodosDisfrutantes.setChecked(false);
                recyclerPosiblesDisfrutantes.setVisibility(View.VISIBLE);
                String disfrutantes=intento.getStringExtra("disfrutantes");
                for(String disfrutante:disfrutantes.split(";")){
                    for(PosibleDisfrutante p:listaPosiblesDisfrutantes){
                        if(disfrutante.equals(p.nombre)){
                            p.esDisfrutante=true;
                        }
                    }
                }
                recyclerPosiblesDisfrutantes.getAdapter().notifyDataSetChanged();
            }
        }


        checkBoxSonTodosDisfrutantes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recyclerPosiblesDisfrutantes.setVisibility(View.INVISIBLE);
                }else{
                    recyclerPosiblesDisfrutantes.setVisibility(View.VISIBLE);
                }
            }
        });


        cuentaActual =new Cuenta(numeroCuenta,textoTitulo.getText().toString(),"");

        cuentaActual.setListaFromUnicoString(ristraNombres);

        myRef=FirebaseDatabase.getInstance().getReference("listas");

        myRef.child(numeroCuenta.toString()).child("movimientos").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String disfrutantes;
                boolean sonTodosDisfrutantes;

                if(dataSnapshot.hasChild("sonTodosDisfrutantes")){
                    sonTodosDisfrutantes=(boolean) dataSnapshot.child("sonTodosDisfrutantes").getValue();
                }else{
                    sonTodosDisfrutantes=true;//desestimo la posibilidad de que no lo sean todos
                }

                if(sonTodosDisfrutantes){
                    disfrutantes=cuentaActual.getListaUnicoString();
                }else{
                    disfrutantes=dataSnapshot.child("participantes").getValue().toString();
                }
                Movimiento nuevoMovimiento=new Movimiento(Double.parseDouble( dataSnapshot.child("cantidad").getValue().toString()),
                        dataSnapshot.child("concepto").getValue().toString(),
                        dataSnapshot.child("Nombre").getValue().toString(),
                        dataSnapshot.child("id").getValue().toString(),sonTodosDisfrutantes,disfrutantes,
                        dataSnapshot.child("fecha").getValue().toString()
                );

                cuentaActual.addMovimiento(nuevoMovimiento);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String disfrutantes;
                boolean sonTodosDisfrutantes;

                if(dataSnapshot.hasChild("sonTodosDisfrutantes")){
                    sonTodosDisfrutantes=(boolean) dataSnapshot.child("sonTodosDisfrutantes").getValue();
                }else{
                    sonTodosDisfrutantes=true;//desestimo la posibilidad de que no lo sean todos
                }

                if(sonTodosDisfrutantes){
                    disfrutantes=cuentaActual.getListaUnicoString();
                }else{
                    disfrutantes=dataSnapshot.child("participantes").getValue().toString();
                }

                Movimiento movimientoModificado=new Movimiento(Double.parseDouble( dataSnapshot.child("cantidad").getValue().toString()),
                        dataSnapshot.child("concepto").getValue().toString(),
                        dataSnapshot.child("Nombre").getValue().toString(),
                        dataSnapshot.child("id").getValue().toString(),sonTodosDisfrutantes,disfrutantes,
                        dataSnapshot.child("fecha").getValue().toString()
                );
                cuentaActual.modificarMovimiento(movimientoModificado);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                cuentaActual.quitarMovimiento(dataSnapshot.child("id").getValue().toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }//fin de la función on create


    public void clickCrearElemento(View view){//FALTAN LAS VALIDACIONES

        final String concepto;
        final Double importe;
        final String fecha;
        final String pagador;
        final boolean sonTodosDisfrutantes;
        final String disfrutantes;

        concepto=txtBoxConcepto.getText().toString();

        //validacion del importe
        if(txtBoxImporte.getText()==null){
            importe=0.0;
        }else{
            String stringImporte=txtBoxImporte.getText().toString();
            stringImporte=stringImporte.replaceAll(",",".");
            importe=Double.parseDouble(stringImporte);
        }

        fecha=txtBoxFecha.getText().toString();
        pagador=comboBoxNombres.getSelectedItem().toString();
        sonTodosDisfrutantes=checkBoxSonTodosDisfrutantes.isChecked();
        String aux="";
        if(!sonTodosDisfrutantes){
            for(PosibleDisfrutante p:listaPosiblesDisfrutantes){
                if(p.esDisfrutante){//lo añado a la lista
                    if(aux!=""){
                        aux+= ";";
                    }
                    aux+=p.nombre;
                }
            }
        }

        disfrutantes=aux;

        AlertDialog.Builder alerta=new AlertDialog.Builder(formNuevoGasto.this);


        if(esNuevoGasto)//es nuevo movimiento
        {

            alerta.setTitle("Apuntar nuevo gasto");
            alerta.setMessage("¿Desea añadir un gasto de "+importe+"€ pagados por '"+pagador+"' el día "+fecha+" con el concepto \""+concepto+"\"?");



        }else{//es modificación del movimiento
            alerta.setTitle("Modificar gasto");
            alerta.setMessage("¿Desea modificar el movimiento para que sea un gasto de "+importe+"€ pagados por "+pagador+" el día "+fecha+" con el concepto \""+concepto+"\"?");

        }

        alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Log.e("mensaje","Lista:"+cuentaActual.getListaUnicoString());

                //por ahora pongo como disfrutantes a todos
                Movimiento nuevoMovimiento=new Movimiento(importe,concepto,pagador,idMovimiento,sonTodosDisfrutantes,disfrutantes,fecha);

                myRef.child(Long.toString(numeroCuenta)).child("movimientos").child(idMovimiento).setValue(nuevoMovimiento.getInfoMovimiento());


                if(esNuevoGasto) {
                    cuentaActual.addMovimiento(nuevoMovimiento);
                }else{
                    cuentaActual.modificarMovimiento(nuevoMovimiento);
                }


                dialog.dismiss();

                Intent returnIntent=new Intent();


                cuentaActual.guardarImporteTotal();



                setResult(Activity.RESULT_OK, returnIntent);

                finish();
            }
        });


        alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alerta.create().show();

    }//fin de la función finCrearElemento


   /* public void recalcularYguardarImporteTotal(){
        Log.e ("mensaje","el numero de cuenta es:"+numeroCuenta);
        Cuenta cuentaTemp=new Cuenta(numeroCuenta,true,false);

        cuentaActual.


        //cuentaTemp.calcularImporteTotal();

    }*/


    public String crearIdConFecha(){
        long idPropuesto;

        Calendar ahora;
        ahora=Calendar.getInstance();

        idPropuesto=(int) ahora.get( Calendar.YEAR);
        idPropuesto=idPropuesto*100+ahora.get(Calendar.MONTH)+1;
        idPropuesto=idPropuesto*100+ahora.get(Calendar.DAY_OF_MONTH);
        idPropuesto=idPropuesto*100+ahora.get(Calendar.HOUR_OF_DAY);
        idPropuesto=idPropuesto*100+ahora.get(Calendar.MINUTE);

        //el del segundo lo dejo solo para los movimientos
        idPropuesto=idPropuesto*100+ahora.get(Calendar.SECOND);

        return "mov-"+Long.toString(idPropuesto);

    }

    public String getStringFecha(){
        String stringFecha;

        Calendar ahora;
        ahora=Calendar.getInstance();

        stringFecha =Integer.toString( ahora.get(Calendar.DAY_OF_MONTH));
        stringFecha+="/"+(ahora.get(Calendar.MONTH)+1);
        stringFecha+="/"+ahora.get(Calendar.YEAR);

        return stringFecha;
    }


}//fin de la clase
