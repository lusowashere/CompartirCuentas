package com.example.lusog.compartircuentas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.TextView;

public class pant_principal_cuenta extends AppCompatActivity {

    public Cuenta cuentaActual;
    TextView labelTitulo, labelImporteTotal, labelPersonas, labelPromedio;
    RecyclerView recicler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_principal_cuenta);

        Intent intento=getIntent();
        Long idCuenta=intento.getLongExtra("idCuenta",0);

        if(idCuenta!=0){
            cuentaActual=new Cuenta(idCuenta,true,true);//con esto ya tengo toda la información de la cuenta que hay en firebase
        }

        //con la info, relleno los cuatro textView
        labelTitulo=(TextView) findViewById(R.id.labelTitulo);
        labelImporteTotal=(TextView) findViewById(R.id.labelImporteTotal);
        labelPersonas=(TextView) findViewById(R.id.labelNumeroPersonas);
        labelPromedio=(TextView) findViewById(R.id.labelPromedio);
        recicler=(RecyclerView) findViewById(R.id.recyclerViewNombres);
        /*labelTitulo.setText(cuentaActual.titulo);
        labelImporteTotal.setText(Double.toString( cuentaActual.importeTotal)+"€");
        labelPersonas.setText(cuentaActual.getNumeroPersonas()+" personas");
        labelImporteTotal.setText(cuentaActual.getImportePromedio()+" €/persona");
        */

        recicler.setLayoutManager(new LinearLayoutManager(this));

        cuentaActual.setTextViews(labelTitulo,labelImporteTotal,labelPersonas,labelPromedio,recicler);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pantalla_cuenta,menu);
        return true;
    }
}
