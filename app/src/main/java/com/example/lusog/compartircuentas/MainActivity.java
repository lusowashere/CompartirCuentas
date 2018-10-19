package com.example.lusog.compartircuentas;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void butt_nueva_cuenta_CLICK(View view){
        Intent intento=new Intent(this,detalles_cuenta.class);
        intento.putExtra("esNuevaCuenta",true);

        startActivity(intento);
    }

}
