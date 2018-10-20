package com.igorferrani.financeiro.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.igorferrani.financeiro.R;
import com.igorferrani.financeiro.domain.Registro;
import com.igorferrani.financeiro.interfaces.RecyclerViewOnClickListenerClick;

import java.util.ArrayList;

public class ContaAdapter extends RecyclerView.Adapter<ContaAdapter.MyViewHolder> {

    private ArrayList<Registro> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerClick mRecyclerViewOnClickListenerClick;

    public ContaAdapter(Context context, ArrayList<Registro> list) {
        mList = list;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = mLayoutInflater.inflate(R.layout.item_conta, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int position) {
        myViewHolder.tv_description.setText(mList.get(position).description);
        myViewHolder.tv_value.setText(Double.toString(mList.get(position).value));
        String textoTipoConta = "";

        if (mList.get(position).tipoConta == Registro.CONTA_PARCELADA) {
            textoTipoConta = "Parcela " + mList.get(position).parcelaAtual + "/" + mList.get(position).quantidadeParcelas + " (R$" + mList.get(position).valorParcela + ")";
        } else if (mList.get(position).tipoConta == Registro.CONTA_FIXA) {
            textoTipoConta = "Registro fixa, vence dia " + mList.get(position).diaVencimento;
        } else if (mList.get(position).tipoConta == Registro.CONTA_AVULSA) {
            textoTipoConta = "Registro avulsa";
        }

        int colorStatus;

        switch (mList.get(position).status) {
            case Registro.STATUS_QUITADO:
                colorStatus = R.color.color_quitado;
                break;
            default:
                colorStatus = R.color.color_aberto;
                break;
        }

        myViewHolder.v_status_conta.setBackgroundResource(colorStatus);

        myViewHolder.tv_tipo_conta.setText(textoTipoConta);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setmRecyclerViewOnClickListenerClick(RecyclerViewOnClickListenerClick r) {
        mRecyclerViewOnClickListenerClick = r;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_description;
        private TextView tv_value;
        private TextView tv_tipo_conta;
        private View v_status_conta;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_description = (TextView) itemView.findViewById(R.id.tv_description);
            tv_value = (TextView) itemView.findViewById(R.id.tv_value);
            tv_tipo_conta = (TextView) itemView.findViewById(R.id.tv_tipo_conta);
            v_status_conta = (View) itemView.findViewById(R.id.v_status_conta);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerClick != null) {
                mRecyclerViewOnClickListenerClick.onClickListener(v, getPosition());
            }
        }
    }
}
