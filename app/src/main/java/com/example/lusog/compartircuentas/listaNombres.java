package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class listaNombres extends AppCompatActivity {

    public ArrayList<String> nombres;
    public ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_nombres);

        lista=findViewById(R.id.listViewNombres);

        String stringNombres;

        Intent intento=getIntent();
        stringNombres=intento.getStringExtra("stringListaNombres");

        nombres=new ArrayList<>();
        if(stringNombres!=""){
            for(String n:stringNombres.split(";")){
                nombres.add(n);
            }

            refreshLista();
        }


    }

    public void refreshLista(){
        ArrayAdapter<String> adaptador_de_array=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,nombres);

        //Recyc

        lista.setAdapter(null);
        lista.setAdapter(adaptador_de_array);

    }

    public void addNombreAlista(View view){
        String nuevoNombre;
        EditText txtbox=(EditText) findViewById(R.id.txtBoxNuevoParticipante);

        nuevoNombre=txtbox.getText().toString();

        if(nuevoNombre!=""){

            //compruebo que no está repetido
            boolean repetido=false;

            for(String nombre:nombres){
                if(nombre==nuevoNombre){
                    repetido=false;
                }
            }

            if(repetido) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(listaNombres.this);
                alerta.setTitle("añadir nombre");
                alerta.setMessage("\""+nuevoNombre+"\" ya está en la lista");

                alerta.show();
            }else{
                nombres.add(nuevoNombre);
                txtbox.setText("");
                refreshLista();
            }
        }

    }//fin de la función addNombreALista


    public void buttVolverClick(View view){

        String ristra="";
        int i=0;

        for(String n:nombres){
            if(i>0){
                ristra+=";";
            }
            ristra+=n;
            i++;
        }

        Intent returnIntent=new Intent();
        returnIntent.putExtra("stringListaNombres",ristra);

        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

}


