package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.IDNA;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    //public ArrayList<Cuenta> listaCuentasUsuario;
    //ArrayList<Cuenta2> listaCuentasUsuario;
    ArrayList<InformacionCuenta> listaCuentasUsuario;
    public String rutaArchivoCuentasUsuario;

    public FirebaseDatabase database;
    public DatabaseReference myRef;

    public RecyclerView recicler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");


        rutaArchivoCuentasUsuario="cuentasUsuario";
        listaCuentasUsuario=new ArrayList<>();

        recicler=findViewById(R.id.reciclerCuentas);
        recicler.setLayoutManager(new LinearLayoutManager(this));
        //adaptadorCuentas adapter=new adaptadorCuentas(  listaCuentasUsuario,this /*getApplicationContext()*/);

        adaptadorCuentas adapter=new adaptadorCuentas(listaCuentasUsuario,this);

        recicler.setAdapter(adapter);

        leerListas();

    }


    public void leerListas(){
        //leo si hay listasGuardadas
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(openFileInput(rutaArchivoCuentasUsuario)));
            String textoLeido=br.readLine();
            br.close();


            listaCuentasUsuario.clear();

            for(String idLista:textoLeido.split(";")) {

                if (!idLista.equals("")) {

                    myRef.child(idLista).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long idCambiado=Long.parseLong( dataSnapshot.child("id").getValue().toString());
                            boolean cuentaExiste=false;
                            for(InformacionCuenta cuentas:listaCuentasUsuario){
                                if(cuentas.id==idCambiado){
                                    cuentaExiste=true;
                                    cuentas.titulo=dataSnapshot.child("titulo").getValue().toString();
                                    cuentas.descripcion=dataSnapshot.child("descripcion").getValue().toString();
                                    cuentas.importeTotal=Double.parseDouble( dataSnapshot.child("importeTotal").getValue().toString());
                                    cuentas.participantes=dataSnapshot.child("participantes").getValue().toString();
                                }
                            }
                            if(!cuentaExiste){//creamos la cuenta
                                InformacionCuenta info=new InformacionCuenta(idCambiado,dataSnapshot.child("titulo").getValue().toString()
                                        ,dataSnapshot.child("descripcion").getValue().toString(),
                                        Double.parseDouble( dataSnapshot.child("importeTotal").getValue().toString()),
                                        dataSnapshot.child("participantes").getValue().toString());
                                listaCuentasUsuario.add(info);
                            }

                            recicler.getAdapter().notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }


            }

        }catch (Exception e){
            Log.e("mensaje","no se ha leido el archivo correctamente");
        }
    }

    public void actualizarRecyclerView(){
        recicler.setLayoutManager(new LinearLayoutManager(this));


        adaptadorCuentas adapter=new adaptadorCuentas(  listaCuentasUsuario,this /*getApplicationContext()*/);
        recicler.setAdapter(adapter);

        adapter.notifyDataSetChanged();

    }


    public void butt_nueva_cuenta_CLICK(View view){
        Intent intento=new Intent(this,detalles_cuenta.class);
        intento.putExtra("esNuevaCuenta",true);

        //startActivity(intento);
        startActivityForResult(intento,1);

    }

    public void butt_unirse_a_cuenta_CLICK(View view){
        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setTitle("Introduce el número de cuenta al que quieras unirte");

        final EditText input=new EditText(this);
        builder.setView(input);

        final Context contexto1=this;

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //añadir cuenta

                final long numeroCuentaNuevo=CodificacionCuentas.getLongDesCodificado(input.getText().toString());

                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AlertDialog.Builder alertaAdded=new AlertDialog.Builder(contexto1);

                        if(dataSnapshot.hasChild(Long.toString( numeroCuentaNuevo))){
                            //alertaAdded.setMessage("Cuenta con id '"+numeroCuentaNuevo+"' añadida");
                            add_ID_a_cuentas(numeroCuentaNuevo);
                        }else{

                            alertaAdded.setMessage("No existe la cuenta con el id '"+Long.toString(numeroCuentaNuevo)+"'");

                        }

                        alertaAdded.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        alertaAdded.create().show();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                dialog.dismiss();

            }
        });


        builder.create().show();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1 && resultCode==Activity.RESULT_OK){
            if(data.getBooleanExtra("addCuenta",false)){
                add_ID_a_cuentas(data.getLongExtra("idCuenta",0));
            }
        }


        if(requestCode==3 && resultCode==Activity.RESULT_OK){//pantalla de resumen cuenta
            if(data.getBooleanExtra("cuentaOlvidadaEliminada",false)){
                final long idListaEliminada=data.getLongExtra("idCuentaEliminada",0);

                //eliminamos el elemento de la lista que tenga ese id

                int i=0;
                int posicion=-1;

                for(InformacionCuenta c:listaCuentasUsuario){
                    if(c.id==idListaEliminada){
                        posicion=i;
                    }
                    i++;
                }

                if(i!=-1){
                    listaCuentasUsuario.remove(posicion);
                    recicler.getAdapter().notifyDataSetChanged();
                }


            }
        }

    }



    public void add_ID_a_cuentas(Long idNuevo){
        String cuentasActuales="";

        boolean yaEstaba=false;
        boolean addPuntoComa=false;

        for(InformacionCuenta c:listaCuentasUsuario){

            if(addPuntoComa){
                cuentasActuales+=";";
            }else{
                addPuntoComa=true;
            }

            cuentasActuales+=c.id;

            if(c.id==idNuevo) {
                yaEstaba = true;
            }
        }

        if(!yaEstaba){
            cuentasActuales+=";"+idNuevo;

            //escribo el archivo
            try{
                OutputStreamWriter osw=new OutputStreamWriter(openFileOutput(rutaArchivoCuentasUsuario,Context.MODE_PRIVATE));
                osw.write(cuentasActuales);
                osw.close();
                //listaCuentasUsuario.add(new Cuenta2(idNuevo));
                recicler.getAdapter().notifyDataSetChanged();
            }catch (Exception e){
                Log.e("mensaje","no se ha podido guardar el archivo");
            }
        }else{
            //debería saltar una alerta diciendo que ya existe la cuenta
        }

        //leerListas();

        //recargo la actividad
        finish();
        startActivity(getIntent());

    }
}
