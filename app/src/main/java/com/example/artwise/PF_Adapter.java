package com.example.artwise;

import android.hardware.lights.LightState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PF_Adapter extends RecyclerView.Adapter<PF_Adapter.PF_VH> {

    List<PF_Preguntas>pf_preguntasList;

    public PF_Adapter(List<PF_Preguntas> pf_preguntasList) {
        this.pf_preguntasList = pf_preguntasList;
    }

    @NonNull
    @Override
    public PF_Adapter.PF_VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pf_filas,parent, false);
        return new PF_VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PF_Adapter.PF_VH holder, int position) {
        PF_Preguntas pf_preguntas= pf_preguntasList.get(position);
        holder.preguntaTxt.setText(pf_preguntas.getPregunta());
        holder.respuestaTxt.setText(pf_preguntas.getRespuesta());

        boolean isExpandable = pf_preguntasList.get(position).isExpandable();
        holder.expandableLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {
        return pf_preguntasList.size();
    }

    public class PF_VH extends RecyclerView.ViewHolder {

        TextView preguntaTxt, respuestaTxt;
        LinearLayout linearLayout;
        RelativeLayout expandableLayout;

        public PF_VH(@NonNull View itemView) {
            super(itemView);

            preguntaTxt = itemView.findViewById(R.id.pregunta);
            respuestaTxt = itemView.findViewById(R.id.respuesta);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            expandableLayout = itemView.findViewById(R.id.expandable_layout);

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PF_Preguntas pf_preguntas = pf_preguntasList.get(getAbsoluteAdapterPosition());
                    pf_preguntas.setExpandable(!pf_preguntas.isExpandable());
                    notifyItemChanged(getAbsoluteAdapterPosition());
                }
            });

        }
    }
}
