package com.example.lacteosbeln;


import androidx.annotation.NonNull;

public class ResumenSemanal extends com.example.lacteosbeln.Proveedor {
    private Proveedor Proveedor;
    private String lecheLunes;
    private String lecheMartes;
    private String lecheMiercoles;
    private String lecheJueves;
    private String lecheViernes;
    private String lecheSabado;
    private String lecheDomingo;

    public ResumenSemanal(){

    }

    public ResumenSemanal(Proveedor proveedor, String lecheLunes, String lecheMartes, String lecheMiercoles, String lecheJueves, String lecheViernes, String lecheSabado, String lecheDomingo) {
        Proveedor = proveedor;
        this.lecheLunes = lecheLunes;
        this.lecheMartes = lecheMartes;
        this.lecheMiercoles = lecheMiercoles;
        this.lecheJueves = lecheJueves;
        this.lecheViernes = lecheViernes;
        this.lecheSabado = lecheSabado;
        this.lecheDomingo = lecheDomingo;
    }


    public Object getProveedor() {
        return Proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        Proveedor = proveedor;
    }

    public String getLecheLunes() {
        return lecheLunes;
    }

    public void setLecheLunes(String lecheLunes) {
        this.lecheLunes = lecheLunes;
    }

    public String getLecheMartes() {
        return lecheMartes;
    }

    public void setLecheMartes(String lecheMartes) {
        this.lecheMartes = lecheMartes;
    }

    public String getLecheMiercoles() {
        return lecheMiercoles;
    }

    public void setLecheMiercoles(String lecheMiercoles) {
        this.lecheMiercoles = lecheMiercoles;
    }

    public String getLecheJueves() {
        return lecheJueves;
    }

    public void setLecheJueves(String lecheJueves) {
        this.lecheJueves = lecheJueves;
    }

    public String getLecheViernes() {
        return lecheViernes;
    }

    public void setLecheViernes(String lecheViernes) {
        this.lecheViernes = lecheViernes;
    }

    public String getLecheSabado() {
        return lecheSabado;
    }

    public void setLecheSabado(String lecheSabado) {
        this.lecheSabado = lecheSabado;
    }

    public String getLecheDomingo() {
        return lecheDomingo;
    }

    public void setLecheDomingo(String lecheDomingo) {
        this.lecheDomingo = lecheDomingo;
    }

    @NonNull
    @Override
    public String toString() {
        return getProveedor().toString()+" leche lunes "+ lecheLunes + " lecheMartes: " +lecheMartes+ " lecheMiercoles: " +lecheMiercoles+ " lechejueves: " +lecheJueves+ " lecheviernes: " +lecheViernes + " lecheSabado: " +lecheSabado+ " lecheDomingo: " +lecheDomingo;
    }
}
