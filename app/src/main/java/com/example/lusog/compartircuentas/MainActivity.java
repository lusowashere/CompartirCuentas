package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public ArrayList<Cuenta> listaCuentasUsuario;
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
        adaptadorCuentas adapter=new adaptadorCuentas(  listaCuentasUsuario,this /*getApplicationContext()*/);
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

                    Log.e("Mensaje", "Intentando leer cuenta con id'" + idLista + "'");

                    /*
                    myRef.child(idLista).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Long id;
                            String titulo, descripcion, ristraNombres;
                            Double importeTotal;

                            id = (Long) Long.parseLong(dataSnapshot.child("id").getValue().toString());
                            titulo = (String) dataSnapshot.child("titulo").getValue();
                            descripcion = (String) dataSnapshot.child("descripcion").getValue();
                            importeTotal = (Double) Double.parseDouble(dataSnapshot.child("importeTotal").getValue().toString());
                            //Log.e("mensaje","importe total:"+importeTotal+"€");
                            ristraNombres = (String) dataSnapshot.child("participantes").getValue();

                            Cuenta nuevaCuenta = new Cuenta(id, titulo, descripcion);
                            nuevaCuenta.importeTotal = importeTotal;
                            nuevaCuenta.setListaFromUnicoString(ristraNombres);

                            listaCuentasUsuario.add(nuevaCuenta);

                            //actualizarRecyclerView();

                            recicler.getAdapter().notifyDataSetChanged();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });*/

                    listaCuentasUsuario.add(new Cuenta(Long.parseLong(idLista),false,false));//en este no necesito leer los movimientos
                }

                //Log.e("mensaje","parece que se ha leido bien el archivo"+cuenta);


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

        builder.setTitle("Introducir el número de cuenta al que quieras unirte");

        final EditText input=new EditText(this);
        builder.setView(input);

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

        if(requestCode==2 && resultCode==Activity.RESULT_OK){//se ha añadido un movimiento
            /*Log.e("mensaje","se ha cargado un nuevo movimientoooo");
            leerListas();*/

            long idMovimientoModificado=data.getLongExtra("idCuenta",0);

            /*

            for(Cuenta c:listaCuentasUsuario){
                if(idMovimientoModificado==c.id){

                    recicler.getAdapter().notifyDataSetChanged();
                }
            }*/


        }

    }



    public void add_ID_a_cuentas(Long idNuevo){
        String cuentasActuales="";

        boolean yaEstaba=false;
        boolean addPuntoComa=false;

        for(Cuenta c:listaCuentasUsuario){

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
            }catch (Exception e){
                Log.e("mensaje","no se ha podido guardar el archivo");
            }
        }

        leerListas();

    }
}
