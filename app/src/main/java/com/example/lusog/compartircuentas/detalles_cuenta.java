package com.example.lusog.compartircuentas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class detalles_cuenta extends AppCompatActivity {

    public boolean esNuevaCuenta;
    public Cuenta cuentaActual;

    public FirebaseDatabase database;
    public DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_cuenta);

        Intent intento=getIntent();
        esNuevaCuenta=intento.getBooleanExtra("esNuevaCuenta",false);


        TextView texto=(TextView) findViewById(R.id.textView2);
        Button botonSalida=(Button) findViewById(R.id.buttExit);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        if(esNuevaCuenta){
            texto.setText("Crear una nueva cuenta");
            botonSalida.setText("Crear la cuenta");

            cuentaActual=new Cuenta("Prueba","Pruebaaa");
            Log.e("Mensaje","Id:"+cuentaActual.id);
        }

    }





    public void BotonSalida_Click(View view){

        EditText txtboxTitulo=(EditText) findViewById(R.id.txtBoxTitulo);

        cuentaActual.titulo=txtboxTitulo.getText().toString();

        if (esNuevaCuenta){

            myRef.child(Long.toString(cuentaActual.id)).child("id").setValue(Long.toString(cuentaActual.id));

        }

        myRef.child(Long.toString(cuentaActual.id)).child("titulo").setValue(cuentaActual.titulo);
        myRef.child(Long.toString(cuentaActual.id)).child("descripcion").setValue(cuentaActual.descripcion);

    }

}
