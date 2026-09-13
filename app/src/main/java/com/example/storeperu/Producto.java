package com.example.storeperu;

import org.json.JSONException;
import org.json.JSONObject;

public class Producto {

    private int id;
    private String nombre;
    private String categoria;
    private String descripcion;
    private int garantia;
    private double precio;
    private int stock;

    public Producto() {
    }

    public Producto(String nombre, String categoria, String descripcion, int garantia, double precio, int stock) {
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.garantia = garantia;
        this.precio = precio;
        this.stock = stock;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();

        try {
            json.put("nombre", nombre);
            json.put("categoria", categoria);
            json.put("descripcion", descripcion);
            json.put("garantia", garantia);
            json.put("precio", precio);
            json.put("stock", stock);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public int getGarantia() {
        return garantia;
    }

    public void setGarantia(int garantia) {
        this.garantia = garantia;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}