package com.example.lusog.compartircuentas;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id=item.getItemId();

        switch(id){
            case R.id.idButtCompartirCuenta:
                compartirCuenta();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void verMovimientosCuenta(View view){
        Intent intento=new Intent(this,pant_ver_movimientos.class);

        intento.putExtra("numeroCuenta",cuentaActual.id);
        intento.putExtra("sonMovimientosPersona",false);


        intento.putExtra("nombrePersona",cuentaActual.titulo);
        intento.putExtra("nMovimientos",cuentaActual.movimientosCuenta.size());
        intento.putExtra("totalPagado",cuentaActual.getImporteTotal());


        //intento.putExtra("cuenta",cuentaActual);

        startActivity(intento);


    }

    public void compartirCuenta(){//muestra un cuadro de diálogo con el código transformado para compartir la cuenta
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("Introduce este código en otro móvil para tener acceso a esta cuenta");

        final EditText input=new EditText(this);
        input.setText(CodificacionCuentas.getStringCodificado(cuentaActual.id));
        input.setInputType(InputType.TYPE_NULL);
        input.setTextIsSelectable(true);

        builder.setView(input);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.create().show();

    }










}
