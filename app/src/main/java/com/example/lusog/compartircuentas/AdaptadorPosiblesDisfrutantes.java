package com.example.lusog.compartircuentas;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;

public class AdaptadorPosiblesDisfrutantes extends RecyclerView.Adapter<AdaptadorPosiblesDisfrutantes.ViewHolderAdaptador> {

    public ArrayList<PosibleDisfrutante> listaPosiblesDisfrutantes;

    public AdaptadorPosiblesDisfrutantes(ArrayList<PosibleDisfrutante> listaPosiblesDisfrutantes) {
        this.listaPosiblesDisfrutantes = listaPosiblesDisfrutantes;
    }

    @NonNull
    @Override
    public AdaptadorPosiblesDisfrutantes.ViewHolderAdaptador onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_posible_disfrutante,parent,false);
        return new ViewHolderAdaptador(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorPosiblesDisfrutantes.ViewHolderAdaptador holder, final int position) {
        holder.labelNombre.setText(listaPosiblesDisfrutantes.get(position).nombre);
        holder.checkBoxEsDisfrutante.setChecked(listaPosiblesDisfrutantes.get(position).esDisfrutante);

        holder.checkBoxEsDisfrutante.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listaPosiblesDisfrutantes.get(position).esDisfrutante=isChecked;
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaPosiblesDisfrutantes.size();
    }

    public class ViewHolderAdaptador extends RecyclerView.ViewHolder {
        TextView labelNombre;
        CheckBox checkBoxEsDisfrutante;
        public ViewHolderAdaptador(View itemView) {
            super(itemView);
            labelNombre=(TextView) itemView.findViewById(R.id.labelNombre);
            checkBoxEsDisfrutante=(CheckBox) itemView.findViewById(R.id.checkBoxDisfrutante);
        }
    }
}
