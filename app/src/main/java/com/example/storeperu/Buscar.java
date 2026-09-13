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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Buscar extends AppCompatActivity {
    EditText edtId, edtNombreB, edtDescripcionB, edtGarantiaB, edtPrecioB, edtStockB;
    Button btnBuscar, btnActualizar, btnEliminar, btnLimpiar;
    RadioGroup rgCategoriaB;
    RadioButton rbJuguetesB, rbTecnologiaB, rbRopaB, rbHogarB;

    RequestQueue requestQueue;
    private final String URL = "http://192.168.1.9:3000/productos";

    private void loadUI() {
        edtId = findViewById(R.id.edtId);
        edtNombreB = findViewById(R.id.edtNombreB);
        edtDescripcionB = findViewById(R.id.edtDescripcionB);
        edtGarantiaB = findViewById(R.id.edtGarantiaB);
        edtPrecioB = findViewById(R.id.edtPrecioB);
        edtStockB = findViewById(R.id.edtStockB);

        rgCategoriaB = findViewById(R.id.rgCategoriaB);
        rbJuguetesB = findViewById(R.id.rbJuguetesB);
        rbTecnologiaB = findViewById(R.id.rbTecnologiaB);
        rbRopaB = findViewById(R.id.rbRopaB);
        rbHogarB = findViewById(R.id.rbHogarB);

        btnBuscar = findViewById(R.id.btnBuscar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
    }

    private void resetUI() {
        edtNombreB.setText(null);
        edtDescripcionB.setText(null);
        edtGarantiaB.setText(null);
        edtPrecioB.setText(null);
        edtStockB.setText(null);

        rgCategoriaB.clearCheck();
        edtId.requestFocus();
    }

    private String getCategoriaRG() {
        int idSeleccionado = rgCategoriaB.getCheckedRadioButtonId();

        if (idSeleccionado == -1) {
            return null;
        }

        RadioButton radioButton = findViewById(idSeleccionado);

        return radioButton.getText().toString();
    }

    private int obtenerId() {
        String id = edtId.getText().toString().trim();

        if (id.isEmpty()) {
            edtId.setError("Ingrese el ID del producto");
            edtId.requestFocus();
            return 0;
        }

        return Integer.parseInt(id);
    }

    private void validarError(NetworkResponse response) {
        if(response == null || response.data == null){
            return;
        }

        int statusCode = response.statusCode;
        String errorJSON = new String(response.data);

        if(statusCode == 404 || statusCode == 400){
            try {
                JSONObject jsonObject = new JSONObject(errorJSON);
                String mensajeError = jsonObject.getString("mensaje");
                Toast.makeText(getApplicationContext(), mensajeError, Toast.LENGTH_LONG).show();
                this.resetUI();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void validarAccion(String accion){
        AlertDialog.Builder builder = new AlertDialog.Builder(Buscar.this);
        builder.setTitle("Confirmación");
        builder.setMessage("¿Desea" + accion + " el registro?");

        builder.setPositiveButton("Sí", (dialog, which) -> {
            if(accion.equalsIgnoreCase("eliminar")){
                eliminarProducto();
            }

            if(accion.equalsIgnoreCase("actualizar")){
                actualizarProducto();
            }
        });

        builder.setNegativeButton("No", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void buscarProducto(){
        int id = obtenerId();
        if(id == 0) { return; }

        String endPoint = URL + "/" + id;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                endPoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONObject datos = jsonObject.getJSONObject("datos");
                            Producto producto = Producto.fromJSON(datos);

                            edtId.setText(String.valueOf(producto.getId()));
                            edtNombreB.setText(producto.getNombre());
                            edtDescripcionB.setText(producto.getDescripcion());
                            edtGarantiaB.setText(String.valueOf(producto.getGarantia()));
                            edtPrecioB.setText(String.valueOf(producto.getPrecio()));
                            edtStockB.setText(String.valueOf(producto.getStock()));
                            switch (producto.getCategoria()) {
                                case "Juguetes":
                                    rbJuguetesB.setChecked(true);
                                    break;
                                case "Tecnología":
                                    rbTecnologiaB.setChecked(true);
                                    break;
                                case "Ropa":
                                    rbRopaB.setChecked(true);
                                    break;
                                case "Hogar":
                                    rbHogarB.setChecked(true);
                                    break;
                            }
                            btnActualizar.setEnabled(true);
                            btnEliminar.setEnabled(true);
                        } catch (JSONException e) {
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

    private void actualizarProducto() {
        int id = obtenerId();
        if(id == 0) { return; }

        String endPoint = URL + "/" + id;

        Producto producto = new Producto();

        String nombre = edtNombreB.getText().toString().trim();
        String descripcion = edtDescripcionB.getText().toString().trim();
        String garantia = edtGarantiaB.getText().toString().trim();
        String precio = edtPrecioB.getText().toString().trim();
        String stock = edtStockB.getText().toString().trim();
        String categoria = getCategoriaRG();

        if (nombre.isEmpty()) {
            edtNombreB.setError("Requerido");
            edtNombreB.requestFocus();
            return;
        }

        if (categoria == null) {
            Toast.makeText(getApplicationContext(), "Seleccione una categoría", Toast.LENGTH_SHORT).show();
            return;
        }

        if (descripcion.isEmpty()) {
            edtDescripcionB.setError("Requerido");
            edtDescripcionB.requestFocus();
            return;
        }

        if (garantia.isEmpty()) {
            edtGarantiaB.setError("Requerido");
            edtGarantiaB.requestFocus();
            return;
        }

        if (precio.isEmpty()) {
            edtPrecioB.setError("Requerido");
            edtPrecioB.requestFocus();
            return;
        }

        if (stock.isEmpty()) {
            edtStockB.setError("Requerido");
            edtStockB.requestFocus();
            return;
        }

        producto.setNombre(nombre);
        producto.setCategoria(categoria);
        producto.setDescripcion(descripcion);
        producto.setGarantia(Integer.parseInt(garantia));
        producto.setPrecio(Double.parseDouble(precio));
        producto.setStock(Integer.parseInt(stock));

        JSONObject jsonObj = producto.toJSON();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                endPoint,
                jsonObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            resetUI();
                            JSONObject datos = jsonObject.getJSONObject("datos");
                            String mensaje = datos.getString("mensaje");
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
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

    private void eliminarProducto() {
        int id = obtenerId();
        if(id == 0) { return; }

        String endPoint = URL + "/" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                endPoint,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            resetUI();
                            JSONObject datos = jsonObject.getJSONObject("datos");
                            String mensaje = datos.getString("mensaje");
                            Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buscar);
        requestQueue = Volley.newRequestQueue(this);

        loadUI();

        btnBuscar.setOnClickListener( v -> { buscarProducto(); });
        btnLimpiar.setOnClickListener( v -> { resetUI(); });
        btnActualizar.setOnClickListener( v -> { validarAccion("actualizar"); });
        btnEliminar.setOnClickListener( v -> { validarAccion("eliminar"); });

    }
}