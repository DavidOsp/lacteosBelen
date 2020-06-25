package com.example.lacteosbeln;

import androidx.annotation.NonNull;

public class Pedido {
    private String numeroDeRuta;
    private String nombreProveedor;
    private String cantidadLeche;
    private String tipoDeEnvase;
    private String idProveedor;


    public Pedido (){

    }


    public Pedido(String numeroDeRuta, String nombreProveedor, String cantidadLeche, String tipoDeEnvase) {
        this.numeroDeRuta = numeroDeRuta;
        this.nombreProveedor = nombreProveedor;
        this.cantidadLeche = cantidadLeche;
        this.tipoDeEnvase = tipoDeEnvase;

    }
    public String getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(String idProveedor) {
        this.idProveedor = idProveedor;
    }


    public String getNumeroDeRuta() {
        return numeroDeRuta;
    }

    public void setNumeroDeRuta(String numeroDeRuta) {
        this.numeroDeRuta = numeroDeRuta;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getCantidadLeche() {
        return cantidadLeche;
    }

    public void setCantidadLeche(String cantidadLeche) {
        this.cantidadLeche = cantidadLeche;
    }

    public String getTipoDeEnvase() {
        return tipoDeEnvase;
    }

    public void setTipoDeEnvase(String tipoDeEnvase) {
        this.tipoDeEnvase = tipoDeEnvase;
    }

    @NonNull
    @Override
    public String toString() {

            return "Codigo Proveedor :"+ idProveedor + " Numero de ruta: "+numeroDeRuta+" Nombre del Proveedor: "+ nombreProveedor +" Cantidad de leche: "+ cantidadLeche + " Tipo de Envase : " + tipoDeEnvase ;

    }

}