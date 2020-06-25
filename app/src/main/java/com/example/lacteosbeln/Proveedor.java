package com.example.lacteosbeln;

import androidx.annotation.NonNull;

public class Proveedor {

    private String id;
    private String nombre;
    private String envasado;

    public Proveedor() {
    }

    public Proveedor(String id, String nombre, String envasado) {
        this.id = id;
        this.nombre = nombre;
        this.envasado = envasado;
    }

    public Proveedor(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEnvasado() {
        return envasado;
    }

    public void setEnvasado(String envasado) {
        this.envasado = envasado;
    }

    @NonNull
    @Override
    public String toString() {
        return"Id: "+ this.id  + " Nombre Proveedor : " + this.nombre + " Unidad de medida : " + this.envasado;
    }
}


