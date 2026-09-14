package com.example.storeperu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Indice extends AppCompatActivity {
    Button btnListarProductos, btnBuscarProductos, btnRegistrarProductos;

    private void loadUI() {
        btnListarProductos = findViewById(R.id.btnListarProductos);
        btnBuscarProductos = findViewById(R.id.btnBuscarProductos);
        btnRegistrarProductos = findViewById(R.id.btnRegistrarProductos);
    }

    private void openActivity(Class interfaz){
        Intent intent = new Intent(getApplicationContext(), interfaz);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_indice);

        loadUI();

        btnBuscarProductos.setOnClickListener(v -> { openActivity( Buscar.class); });
        btnListarProductos.setOnClickListener( v -> { openActivity(Listar.class); });
        btnRegistrarProductos.setOnClickListener( v -> { openActivity(Registrar.class); });
    }
}