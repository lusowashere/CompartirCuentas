package com.example.lusog.compartircuentas;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_ver_movimientos);

        database=FirebaseDatabase.getInstance();
        myRef=database.getReference("listas");

        listaMovimientos=new ArrayList<>();

        Intent intento=getIntent();

        numeroCuenta=intento.getLongExtra("numeroCuenta",0);

        nombrePersona_o_cuenta=intento.getStringExtra( "nombrePersona");

        TextView textoTitulo=findViewById(R.id.labelTitulo);
        TextView textoMovimientos=findViewById(R.id.labelMovimientos);
        TextView textoTotal=findViewById(R.id.labelImporteTotal);
        TextView textoDeuda=findViewById(R.id.labelDeuda);

        recyclerMovimientos=findViewById(R.id.reciclerViewMovimientos);

        recyclerMovimientos.setLayoutManager(new  LinearLayoutManager(this));
        AdaptadorMovimientos adapter=new AdaptadorMovimientos(  listaMovimientos,numeroCuenta);
        recyclerMovimientos.setAdapter(adapter);

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

    }
}
