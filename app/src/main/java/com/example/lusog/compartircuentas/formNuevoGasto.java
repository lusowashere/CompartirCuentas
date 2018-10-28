package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
    String idMovimiento;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_nuevo_gasto);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        txtBoxConcepto=findViewById(R.id.editTextConcepto);
        txtBoxFecha=findViewById(R.id.editTextFecha);
        txtBoxImporte=findViewById(R.id.editTextImporte);

        txtBoxFecha.setText(getStringFecha());


        Intent intento=getIntent();

        TextView textoTitulo=findViewById(R.id.tituloCuenta);
        textoTitulo.setText(intento.getStringExtra("titulo"));

        numeroCuenta=intento.getLongExtra("idCuenta",0);

        idMovimiento=intento.getStringExtra("idMovimiento");




        String ristraNombres=intento.getStringExtra("nombres");

        //Log.e("mensaje","ristra nombres:'"+ristraNombres+"'");

        arrayNombres=new ArrayList<>();

          for(String n:      ristraNombres.split(";")){
              //Log.e("mensaje","añadiendo nombre '"+n+"'");
              arrayNombres.add(n);
          }

          nombre=arrayNombres.get(0);//entiendo que por defecto es el primero

        comboBoxNombres=findViewById(R.id.spinnerNombres);

        comboBoxNombres.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,arrayNombres));

      /*  comboBoxNombres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nombre=arrayNombres.get(position);
            }
        });*/


    }//fin de la función on create


    public void clickCrearElemento(View view){

        final String concepto;
        final Double importe;
        final String fecha;



        concepto=txtBoxConcepto.getText().toString();
        importe=Double.parseDouble(txtBoxImporte.getText().toString());
        fecha=txtBoxFecha.getText().toString();

        AlertDialog.Builder alerta=new AlertDialog.Builder(formNuevoGasto.this);


        if(idMovimiento.equals(""))//es nuevo movimiento
        {

            alerta.setTitle("Apuntar nuevo gasto");
            alerta.setMessage("¿Desea añadir un gasto de "+importe+"€ pagados por '"+comboBoxNombres.getSelectedItem().toString()+"' el día "+fecha+" con el concepto \""+concepto+"\"?");

            alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    idMovimiento=crearIdConFecha();

                    Movimiento nuevoMovimiento=new Movimiento(importe,concepto,comboBoxNombres.getSelectedItem().toString(),fecha,idMovimiento);

                    myRef.child(Long.toString(numeroCuenta)).child("movimientos").child(idMovimiento).setValue(nuevoMovimiento);



                    dialog.dismiss();

                    Intent returnIntent=new Intent();

                    recalcularYguardarImporteTotal();
                    /*double nuevoImporte=cuentaTemp.importeTotal;
                    Log.e("mensaje","importe fuera de funcion:"+nuevoImporte+"€");

                    returnIntent.putExtra("nuevoImporteTotal", nuevoImporte);
                    returnIntent.putExtra("idCuenta",numeroCuenta);*/

                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();
                }
            });

        }else{//es modificación del movimiento
            alerta.setTitle("Modificar gasto");
            alerta.setMessage("¿Desea modificar el movimiento para que sea un gasto de "+importe+"€ pagados por "+nombre+" el día "+fecha+" con el concepto \""+concepto+"\"?");
        }

        alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alerta.create().show();

    }//fin de la función finCrearElemento


    public void recalcularYguardarImporteTotal(){
        Log.e ("mensaje","el numero de cuenta es:"+numeroCuenta);
        Cuenta cuentaTemp=new Cuenta(numeroCuenta,true,false);

        cuentaTemp.calcularImporteTotal();

    }


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
