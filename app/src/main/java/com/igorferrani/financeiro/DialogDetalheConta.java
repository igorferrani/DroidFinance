package com.igorferrani.financeiro;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.igorferrani.financeiro.domain.Conta;

public class DialogDetalheConta extends Dialog {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public String key;

    public DialogDetalheConta(Activity a, String key) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.key = key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_detalhe_conta);

        final TextView tv_description = findViewById(R.id.tv_description);
        final TextView tv_value = findViewById(R.id.tv_value);
        final Button btn_marcar_paga = findViewById(R.id.btn_marcar_paga);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("financeiro/saida");
        myRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Conta conta = dataSnapshot.getValue(Conta.class);
                if (conta != null) {
                    if (conta.description != null) {
                        tv_description.setText(conta.description);
                    }

                    tv_value.setText(Double.toString(conta.value));

                    if (conta.status == Conta.STATUS_QUITADO) {
                        btn_marcar_paga.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_marcar_paga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("financeiro/saida");
                myRef.child(key).child("status").setValue(1);
                dismiss();
            }
        });

        Button btn_excluir = findViewById(R.id.btn_excluir);
        btn_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("financeiro/saida");
                myRef.child(key).removeValue();
                dismiss();
            }
        });
    }
}