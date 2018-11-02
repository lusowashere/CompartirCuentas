package com.example.lusog.compartircuentas;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;

public class pant_ver_movimientos extends AppCompatActivity {

    Long numeroCuenta;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RecyclerView recyclerMovimientos;
    String nombrePersona_o_cuenta;
    ArrayList<Movimiento> listaMovimientos;
    boolean sonMovimientosPersona;

    Cuenta2 cuenta;
    Persona persona;

    //Cuenta cuentaActual;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_ver_movimientos);

/*
        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        listaMovimientos=new ArrayList<>();
*/
        Intent intento=getIntent();

        //cuentaActual=(Cuenta) intento.getSerializableExtra("cuenta");

        numeroCuenta=intento.getLongExtra("numeroCuenta",0);

        sonMovimientosPersona=intento.getBooleanExtra("sonMovimientosPersona",true);

        TextView textoTitulo=findViewById(R.id.labelTitulo);
        TextView textoMovimientos=findViewById(R.id.labelMovimientos);
        TextView textoTotal=findViewById(R.id.labelImporteTotal);
        TextView textoDeuda=findViewById(R.id.labelDeuda);

        recyclerMovimientos=findViewById(R.id.reciclerViewMovimientos);


        if(sonMovimientosPersona){
            persona=(Persona) intento.getSerializableExtra("persona");
            listaMovimientos=persona.movimientos;
            textoTitulo.setText(persona.nombre);
            textoMovimientos.setText(String.valueOf(persona.numeroMovimientos)+" movimientos");
            textoTotal.setText(String.format("%.2f",persona.totalPagado)+"€");
            textoDeuda.setText(persona.getDeuda());
            if(persona.deuda>0){
                textoDeuda.setTextColor(Color.RED);
            }else{
                textoDeuda.setTextColor(Color.rgb(0, 102, 0));
            }
        }else{
            cuenta=new Cuenta2((CuentaSerializable) intento.getSerializableExtra("cuenta"));
            listaMovimientos=cuenta.movimientosCuenta;
            textoTitulo.setText( cuenta.titulo);
            textoTotal.setText(String.format("%.2f",cuenta.importeTotal)+"€");
            textoDeuda.setVisibility(View.INVISIBLE);
            textoMovimientos.setText(String.valueOf(cuenta.movimientosCuenta.size())+" movimientos");
        }


        //numeroCuenta=cuentaActual.id;

        //nombrePersona_o_cuenta=intento.getStringExtra( "nombrePersona");






        recyclerMovimientos.setLayoutManager(new  LinearLayoutManager(this));
        AdaptadorMovimientos adapter=new AdaptadorMovimientos(  listaMovimientos,numeroCuenta);
        recyclerMovimientos.setAdapter(adapter);
/*
        textoTitulo.setText(nombrePersona_o_cuenta);
        textoMovimientos.setText(Integer.toString( intento.getIntExtra("nMovimientos",0))+" movimientos");

        Query myQuery;

        if(intento.getBooleanExtra("sonMovimientosPersona",false)){
            textoTotal.setText(intento.getStringExtra("totalPagado"));
            textoDeuda.setText(intento.getStringExtra("deuda"));
            myQuery=myRef.child(Long.toString( numeroCuenta)).child("movimientos").orderByChild("Nombre").equalTo(nombrePersona_o_cuenta);
        }else{
            textoTotal.setText(String.format("%.2f", intento.getDoubleExtra("totalPagado",0))+ "€");

            textoDeuda.setVisibility(View.INVISIBLE);
            //textoTotal.setText(String.format("%.2f", cuentaActual.getImporteTotal()));

            //listaMovimientos=cuentaActual.movimientosCuenta;

            //recyclerMovimientos.getAdapter().notifyDataSetChanged();


            myQuery=myRef.child(Long.toString(numeroCuenta)).child("movimientos");
        }





        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Movimiento nuevoMov=dataSnapshot.getValue(Movimiento.class);
                listaMovimientos.add(nuevoMov);
                recyclerMovimientos.getAdapter().notifyDataSetChanged();
                Log.e("mensaje","leido movimiento "+nuevoMov.toString());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String id=dataSnapshot.child("id").getValue().toString();

                Movimiento movModificado=dataSnapshot.getValue(Movimiento.class);

                for(Movimiento mov:listaMovimientos){
                    if(id.equals(mov.id)){
                        mov.copiarDeOtroMovimiento(movModificado);
                    }
                }

                recyclerMovimientos.getAdapter().notifyDataSetChanged();

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
*/
    }
}
