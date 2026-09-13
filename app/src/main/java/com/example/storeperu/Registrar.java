package com.example.storeperu;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.jar.JarEntry;

public class Registrar extends AppCompatActivity {
    EditText edtNombre, edtDescripcion, edtGarantia, edtPrecio, edtStock;
    RadioGroup rgCategoria;
    Button btnRegistrar;

    RequestQueue requestQueue;
    private final String URL = "http://192.168.1.9:3000/productos";

    private void loadUI() {
        edtNombre = findViewById(R.id.edtNombre);
        edtDescripcion = findViewById(R.id.edtDescripcion);
        edtGarantia = findViewById(R.id.edtGarantia);
        edtPrecio = findViewById(R.id.edtPrecio);
        edtStock = findViewById(R.id.edtStock);
        rgCategoria = findViewById(R.id.rgCategoria);
        btnRegistrar = findViewById(R.id.btnRegistrar);
    }

    private void resetUI() {
        edtNombre.setText(null);
        edtDescripcion.setText(null);
        edtGarantia.setText(null);
        edtPrecio.setText(null);
        edtStock.setText(null);
        rgCategoria.clearCheck();
        edtNombre.requestFocus();
    }

    private void validarError(NetworkResponse response) {
        if(response == null || response.data == null){
            return;
        }

        int statusCode = response.statusCode;
        String errorJSON = new String(response.data);

        if(statusCode == 400){
            try {
                JSONObject jsonObject = new JSONObject(errorJSON);
                String mensajeError = jsonObject.getString("mensaje");
                Toast.makeText(getApplicationContext(), mensajeError, Toast.LENGTH_LONG).show();
                this.resetUI();
            } catch (JSONException e) {
                Log.e("JSON_ERROR", e.toString());
                throw new RuntimeException(e);
            }
        }
    }
    private String getCategoriaRG() {
        int idSeleccionado = rgCategoria.getCheckedRadioButtonId();

        if (idSeleccionado == -1) {
            return null;
        }

        RadioButton radioButton = findViewById(idSeleccionado);

        return radioButton.getText().toString();
    }

    private void registrarProducto() {
        Producto producto = new Producto();

        String nombre = edtNombre.getText().toString().trim();
        String descripcion = edtDescripcion.getText().toString().trim();
        String garantia = edtGarantia.getText().toString().trim();
        String precio = edtPrecio.getText().toString().trim();
        String stock = edtStock.getText().toString().trim();
        String categoria = getCategoriaRG();

        if (nombre.isEmpty()) {
            edtNombre.setError("Requerido");
            edtNombre.requestFocus();
            return;
        }

        if (categoria == null) {
            Toast.makeText(getApplicationContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion.isEmpty()) {
            edtDescripcion.setError("Requerido");
            edtDescripcion.requestFocus();
            return;
        }

        if (garantia.isEmpty()) {
            edtGarantia.setError("Requerido");
            edtGarantia.requestFocus();
            return;
        }

        if (precio.isEmpty()) {
            edtPrecio.setError("Requerido");
            edtPrecio.requestFocus();
            return;
        }

        if (stock.isEmpty()) {
            edtStock.setError("Requerido");
            edtStock.requestFocus();
            return;
        }

        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setDescripcion(descripcion);
        producto.setGarantia(Integer.parseInt(garantia));
        producto.setPrecio(Double.parseDouble(precio));
        producto.setStock(Integer.parseInt(stock));

        JSONObject jsonObj = producto.toJSON();

        requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                URL,
                jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            resetUI();
                            JSONObject datos = jsonObject.getJSONObject("datos");
                            String mensaje = datos.getString("mensaje");
                            int id = datos.getInt("id");
                            Toast.makeText(getApplicationContext(), mensaje + " - ID: " + id, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            Log.e("ERROR_RESPONSE", e.toString());
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        NetworkResponse response = volleyError.networkResponse;
                        validarError(response);
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    private void mostrarPregunta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Registrar.this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Está seguro que desea registrar?");

        builder.setPositiveButton("Sí", (dialog, which) -> { registrarProducto(); });
        builder.setNegativeButton("No", null);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);
        requestQueue = Volley.newRequestQueue(this);

        loadUI();
        btnRegistrar.setOnClickListener( v -> { mostrarPregunta(); });
    }
}