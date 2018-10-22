package com.example.lusog.compartircuentas;

import android.app.Activity;
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


        TextView texto=(TextView) findViewById(R.id.labelCabecera);
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


    public void abrirListaNombres(View view){
        Intent intentoListaDeNombres=new Intent(this,listaNombres.class);
        intentoListaDeNombres.putExtra("stringListaNombres",cuentaActual.getListaUnicoString());
        startActivityForResult(intentoListaDeNombres,1);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode== Activity.RESULT_OK){
            TextView nombres=(TextView) findViewById(R.id.textoListaParticipantes);
            String stringDevuelto=data.getStringExtra("stringListaNombres");

            String texto2="";
            int i=0;
            String[] arrayTexto=stringDevuelto.split(";");
            for(String n:arrayTexto){
                if(i>0){
                    texto2+="\n";
                }
                texto2+=n;
                i++;
            }
            nombres.setText(texto2);

            cuentaActual.setListaFromUnicoString(stringDevuelto);

        }else{
            Log.e("mensaje","fallo");
        }

    }

    public void BotonSalida_Click(View view){

        EditText txtboxTitulo=(EditText) findViewById(R.id.txtBoxTitulo);
        //EditText txtboxDescripcion=(EditText)

        cuentaActual.titulo=txtboxTitulo.getText().toString();

        if (esNuevaCuenta){
            //creo la cuenta en firebase
            myRef.child(Long.toString(cuentaActual.id)).child("id").setValue(Long.toString(cuentaActual.id));



        }

        myRef.child(Long.toString(cuentaActual.id)).child("titulo").setValue(cuentaActual.titulo);
        myRef.child(Long.toString(cuentaActual.id)).child("descripcion").setValue(cuentaActual.descripcion);
        myRef.child(Long.toString(cuentaActual.id)).child("participantes").setValue(cuentaActual.getListaUnicoString());


    }

}
