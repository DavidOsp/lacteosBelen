package com.example.lacteosbeln.util;

import androidx.recyclerview.widget.DiffUtil;

import com.example.lacteosbeln.Proveedor;

import java.util.ArrayList;

public class MyDiffUtils extends DiffUtil.Callback {

    ArrayList<Proveedor> listaAntigua = new ArrayList<Proveedor>();
    ArrayList<Proveedor> listaNueva = new ArrayList<Proveedor>();

    public MyDiffUtils(ArrayList<Proveedor> listaAntigua, ArrayList<Proveedor> listaNueva) {
        this.listaAntigua = listaAntigua;
        this.listaNueva = listaNueva;
    }

    @Override
    public int getOldListSize() {
        return listaAntigua.size();
    }

    @Override
    public int getNewListSize() {
        return listaNueva.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItemPosition == newItemPosition;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return listaAntigua.get(oldItemPosition) == listaNueva.get(newItemPosition);
    }
}
