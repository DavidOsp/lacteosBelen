package com.example.lacteosbeln;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdaptadorBuscador extends RecyclerView.Adapter<AdaptadorBuscador.ProveedorViewHolder> implements View.OnClickListener  {
    Context context;
    ArrayList<Proveedor>nombresProveedores = new ArrayList<Proveedor>();
    private View.OnClickListener listener;

    public AdaptadorBuscador(Context context, ArrayList<Proveedor> nombresProveedores) {
        this.context = context;
        this.nombresProveedores = nombresProveedores;
    }

    @NonNull
    @Override
    public ProveedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buscador,parent,false);
        view.setOnClickListener(this);  // sentencia que instancia el listener en el view
        return new ProveedorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProveedorViewHolder holder, int position) {
            holder.tvNombre.setText(nombresProveedores.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return nombresProveedores.size();
    }
public void setOnClickListener(View.OnClickListener listener1){
        this.listener = listener1;
}

    @Override
    public void onClick(View view) {
        if (listener != null){
            listener.onClick(view);
        }
    }

    public class ProveedorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre ;

        public ProveedorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombre);
        }
    }

    public void filtrar (ArrayList<Proveedor> filtroProveedores){
         this.nombresProveedores = filtroProveedores;
         notifyDataSetChanged();
    }

}
