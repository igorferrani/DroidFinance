package com.igorferrani.financeiro;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.igorferrani.financeiro.domain.Registro;
import com.igorferrani.financeiro.domain.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

public class DialogDetalheConta extends Dialog {

    public Activity ccontext;
    public Dialog d;
    public Button yes, no;
    public String key;
    private JSONObject usuarioLogado;
    private String keyConta;

    public DialogDetalheConta(Activity a, String key) {
        super(a);
        // TODO Auto-generated constructor stub
        this.ccontext = a;
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

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String uid = currentUser.getUid();

        try {
            usuarioLogado = Usuario.getUsuarioLogado(ccontext);
            keyConta = usuarioLogado.getString("conta");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(Registro.FB_KEY_REGISTROS).child(keyConta);
        myRef.child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Registro registro = dataSnapshot.getValue(Registro.class);
                if (registro != null) {
                    if (registro.description != null) {
                        tv_description.setText(registro.description);
                    }

                    tv_value.setText(Double.toString(registro.value));

                    if (registro.status == Registro.STATUS_QUITADO) {
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
                DatabaseReference myRef = database.getReference(Registro.FB_KEY_REGISTROS).child(keyConta);
                myRef.child(key).child("status").setValue(1);
                dismiss();
            }
        });

        Button btn_excluir = findViewById(R.id.btn_excluir);
        btn_excluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(Registro.FB_KEY_REGISTROS).child(keyConta);
                myRef.child(key).removeValue();
                dismiss();
            }
        });
    }
}