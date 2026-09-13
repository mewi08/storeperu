package com.example.storeperu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Listar extends AppCompatActivity {
    ArrayList<Producto> lstProductos = new ArrayList<>();
    AdapterDatos adapterDatos;
    RecyclerView recyclerProductos;

    RequestQueue requestQueue;

    private final String URL = "http://192.168.1.9:3000/productos";

    private void loadIU(){
        recyclerProductos = findViewById(R.id.recyclerProductos);
    }

    private void obtenerDatosWS(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray datos = jsonObject.getJSONArray("datos");
                            for (int i = 0; i < datos.length(); i++){
                                JSONObject objProducto = datos.getJSONObject(i);
                                Producto producto = Producto.fromJSON(objProducto);
                                lstProductos.add(producto);
                            }
                            adapterDatos.notifyDataSetChanged();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("Error_WS", volleyError.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listar);
        requestQueue = Volley.newRequestQueue(this);

        loadIU();

        obtenerDatosWS();

        adapterDatos = new AdapterDatos(lstProductos);
        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProductos.setAdapter(adapterDatos);
    }
}