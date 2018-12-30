package com.example.lusog.compartircuentas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;

public class Pant_Ajustar_Cuentas extends AppCompatActivity {

    public ArrayList<Movimiento> listaAjustes;
    public long  idCuenta;
    public String tituloCuenta;
    public RecyclerView recyclerAjustes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant__ajustar__cuentas);

        Intent intento=getIntent();
        idCuenta=intento.getLongExtra("idCuenta",0);
        tituloCuenta=intento.getStringExtra("tituloCuenta");

        int numeroAjustes=intento.getIntExtra("numeroAjustes",0);
        listaAjustes=new ArrayList<>();

        for(int i=0;i<numeroAjustes;i++){
            listaAjustes.add((Movimiento) intento.getSerializableExtra("ajuste"+i));
        }

        TextView labelTitulo=(TextView) findViewById(R.id.labelTitulo);
        labelTitulo.setText("Ajustes de la cuenta '"+tituloCuenta+"'");

        recyclerAjustes=(RecyclerView) findViewById(R.id.recyclerAjustes);
        recyclerAjustes.setLayoutManager(new LinearLayoutManager(this));
        AdaptadorAjustes adapter=new AdaptadorAjustes(listaAjustes,idCuenta);
        recyclerAjustes.setAdapter(adapter);






    }//fin de la función onCreate
}
