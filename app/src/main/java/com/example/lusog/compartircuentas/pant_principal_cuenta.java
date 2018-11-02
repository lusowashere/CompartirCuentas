package com.example.lusog.compartircuentas;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class pant_principal_cuenta extends AppCompatActivity {

    //public Cuenta cuentaActual;
    public Cuenta2 cuentaActual;
    TextView labelTitulo, labelImporteTotal, labelPersonas, labelPromedio;
    RecyclerView recicler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pant_principal_cuenta);

        Intent intento=getIntent();
        Long idCuenta=intento.getLongExtra("idCuenta",0);

        /*if(idCuenta!=0){
            cuentaActual=new Cuenta(idCuenta,true,true);//con esto ya tengo toda la información de la cuenta que hay en firebase
        }*/

        //cuentaActual=new Cuenta2( (CuentaSerializable)  intento.getSerializableExtra("cuenta"));

        cuentaActual=new Cuenta2(idCuenta);


        //con la info, relleno los cuatro textView
        labelTitulo=(TextView) findViewById(R.id.labelTitulo);
        labelImporteTotal=(TextView) findViewById(R.id.labelImporteTotal);
        labelPersonas=(TextView) findViewById(R.id.labelNumeroPersonas);
        labelPromedio=(TextView) findViewById(R.id.labelPromedio);
        recicler=(RecyclerView) findViewById(R.id.recyclerViewNombres);


        cuentaActual.addLabel("titulo",labelTitulo);
        cuentaActual.addLabel("importePromedio",labelPromedio);
        cuentaActual.addLabel("importeTotal",labelImporteTotal);
        cuentaActual.addLabel("numeroPersonas",labelPersonas);


        /*labelTitulo.setText(cuentaActual.titulo);
        labelImporteTotal.setText(Double.toString( cuentaActual.importeTotal)+"€");
        labelPersonas.setText(cuentaActual.getNumeroPersonas()+" personas");
        labelImporteTotal.setText(cuentaActual.getImportePromedio()+" €/persona");
        */

        recicler.setLayoutManager(new LinearLayoutManager(this));

        //cuentaActual.setTextViews(labelTitulo,labelImporteTotal,labelPersonas,labelPromedio,recicler);

        AdaptadorPersonas adapter=new AdaptadorPersonas(cuentaActual.listaNombres,cuentaActual.getImportePromedio(),cuentaActual.id);

        recicler.setAdapter(adapter);
        cuentaActual.setRecycler(recicler);
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
            case R.id.idButtOlvidarCuenta:
                olvidarCuenta();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void olvidarCuenta() {//la cuenta sigue existiendo, pero ya no se verá en la lista del usuario

        AlertDialog.Builder alerta=new AlertDialog.Builder(this);
        alerta.setTitle("Olvidar");
        alerta.setMessage("¿Desea dejar de ver esta cuenta? Podrá volver a consultarla mientras siga existiendo con el código '" + CodificacionCuentas.getStringCodificado(cuentaActual.id)+"'");

        alerta.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alerta.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String rutaArchivoCuentasUsuario="cuentasUsuario";

                //leo el archivo
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(rutaArchivoCuentasUsuario)));
                    String cuentasActuales = br.readLine();
                    br.close();

                    cuentasActuales=cuentasActuales.replaceAll(Long.toString(cuentaActual.id),"");
                    cuentasActuales=cuentasActuales.replaceAll(";;",";");

                    //escribo el archivo
                    try {
                        OutputStreamWriter osw = new OutputStreamWriter(openFileOutput(rutaArchivoCuentasUsuario, Context.MODE_PRIVATE));
                        osw.write(cuentasActuales);
                        osw.close();
                    } catch (Exception e) {
                        Log.e("mensaje", "no se ha podido guardar el archivo");
                    }

                }catch (Exception e){
                    Log.e("mensaje", "no se ha podido leer el archivo");
                }

                dialog.dismiss();

                Intent returnIntent=new Intent();
                returnIntent.putExtra("cuentaOlvidadaEliminada",true);
                returnIntent.putExtra("idCuentaEliminada",cuentaActual.id);
                setResult(Activity.RESULT_OK,returnIntent);

                finish();

            }
        });

        alerta.create().show();


    }

    public void verMovimientosCuenta(View view){
        Intent intento=new Intent(this,pant_ver_movimientos.class);

        intento.putExtra("numeroCuenta",cuentaActual.id);
        intento.putExtra("sonMovimientosPersona",false);
/*

        intento.putExtra("nombrePersona",cuentaActual.titulo);
        intento.putExtra("nMovimientos",cuentaActual.movimientosCuenta.size());
        intento.putExtra("totalPagado",cuentaActual.importeTotal);

*/
       // intento.putExtra("cuenta",cuentaActual);


        intento.putExtra("cuenta",cuentaActual.getCuentaSerializable());

        startActivity(intento);


    }

    public void compartirCuenta(){//muestra un cuadro de diálogo con el código transformado para compartir la cuenta
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("compartir");
        builder.setMessage("Introduce este código en otro móvil para tener acceso a esta cuenta");

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
