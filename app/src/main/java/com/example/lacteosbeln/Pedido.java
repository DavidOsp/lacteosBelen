package com.example.lacteosbeln;

import androidx.annotation.NonNull;

public class Pedido {
    private String numeroDeRuta;
    private String nombreProveedor;
    private String cantidadLeche;
    private boolean cantidadBotellas ;
    private boolean cantidadLitros ;





    public Pedido(String numeroDeRuta, String nombreProveedor, String cantidadLeche) {
        this.numeroDeRuta = numeroDeRuta;
        this.nombreProveedor = nombreProveedor;
        this.cantidadLeche = cantidadLeche;
        this.cantidadBotellas = cantidadBotellas;
        this.cantidadLitros = cantidadLitros;
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

    public boolean getCantidadBotellas() {
        return cantidadBotellas;
    }

    public void setCantidadBotellas(boolean cantidadBotellas) {
        this.cantidadBotellas = cantidadBotellas;
    }

    public boolean getCantidadLitros() {
        return cantidadLitros;
    }

    public void setCantidadLitros(boolean cantidadLitros) {
        this.cantidadLitros = cantidadLitros;
    }

    @NonNull
    @Override
    public String toString() {

        if (cantidadBotellas){
            return "Numero de ruta: "+numeroDeRuta+" Nombre del Proveedor: "+ nombreProveedor +" Cantidad de leche: "+ cantidadLeche +" Tipo de Envasado : Botellas";

        }
        else if (cantidadLitros){
            return "Numero de ruta: "+numeroDeRuta+" Nombre del Proveedor: "+ nombreProveedor +" Cantidad de leche: "+ cantidadLeche +" Tipo de Envasado Litros";

        }
        return "err_del_condicional_de_la_clase";

    }

}