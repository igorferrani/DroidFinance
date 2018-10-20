package com.igorferrani.financeiro.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.igorferrani.financeiro.DialogDetalheConta;
import com.igorferrani.financeiro.MainActivity;
import com.igorferrani.financeiro.R;
import com.igorferrani.financeiro.adapter.ContaAdapter;
import com.igorferrani.financeiro.domain.Registro;
import com.igorferrani.financeiro.domain.Usuario;
import com.igorferrani.financeiro.domain.Util;
import com.igorferrani.financeiro.interfaces.RecyclerViewOnClickListenerClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ContaPagarFragment extends Fragment implements RecyclerViewOnClickListenerClick {

    private RecyclerView mRecyclerView;
    private ArrayList<Registro> mList;
    private ContaAdapter adapter;
    private MainActivity context;
    private ValueEventListener registrosListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conta, container, false);

        context = (MainActivity) getActivity();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list);
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerView.setHasFixedSize(true);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String uid = currentUser.getUid();

        JSONObject usuarioLogado;
        String keyConta;

        try {
            usuarioLogado = Usuario.getUsuarioLogado(context);
            keyConta = usuarioLogado.getString("conta");
        } catch (JSONException e) {
            e.printStackTrace();
            return view;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Query myRef = database.getReference(Registro.FB_KEY_REGISTROS).child(keyConta).orderByChild("dataDespesa").startAt(MainActivity.ano_atual + "-" + MainActivity.mes_atual);

        mList = new ArrayList<Registro>();

        adapter = new ContaAdapter(getActivity(), mList);
        adapter.setmRecyclerViewOnClickListenerClick(this);

        registrosListener = myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot contaSnapshot: dataSnapshot.getChildren()) {
                    // TODO: handle the post
                    Registro registro = contaSnapshot.getValue(Registro.class);
                    if (registro != null) {
                        registro.key = contaSnapshot.getKey();
                        mList.add(registro);
                    }
                }

                calculaSaldoGeral(mList);
                mRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Util.showToast(context, "Erro ao relizar busca: " + databaseError.getMessage());
            }
        });

        return view;
    }

    private void calculaSaldoGeral(ArrayList<Registro> list) {
        double entrada = 0;
        double saida = 0;
        double saldo = 0;

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).status == Registro.STATUS_QUITADO) {
                if (list.get(i).centroCusto == Registro.CENTRO_CUSTO_ENTRADA) {
                    entrada += list.get(i).value;
                }

                if (list.get(i).centroCusto == Registro.CENTRO_CUSTO_SAIDA) {
                    saida += list.get(i).value;
                }
            }
        }
        MainActivity.valor_saldo = entrada - saida;
        MainActivity.valor_entrada = entrada;
        MainActivity.valor_saida = saida;

        context.setSaldo();
    }

    @Override
    public void onClickListener(View view, int position) {
        String key = mList.get(position).key;

        DialogDetalheConta dialogDetalheConta = new DialogDetalheConta(getActivity(), key);
        dialogDetalheConta.show();
    }
}
