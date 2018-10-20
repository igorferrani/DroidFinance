package com.igorferrani.financeiro;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.igorferrani.financeiro.domain.Conta;
import com.igorferrani.financeiro.domain.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CadastroActivity extends AppCompatActivity {

    private Activity context = this;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            finish();
            return;
        }

        Button btn_continuar = findViewById(R.id.btn_continuar);
        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();

                EditText et_nome_conta = findViewById(R.id.et_nome_conta);

                List<String> usuarios = new ArrayList<String>();
                usuarios.add(currentUser.getUid());

                Conta conta = new Conta();
                conta.nome = et_nome_conta.getText().toString();

                String keyConta = database.getReference(Conta.FB_KEY_CONTA).push().getKey();
                database.getReference(Conta.FB_KEY_CONTA).child(keyConta).setValue(conta);
                database.getReference(Usuario.FB_KEY_USUARIOS).child(currentUser.getUid()).child("conta").setValue(keyConta);

                Usuario usuario = new Usuario();
                usuario.uid = currentUser.getUid();
                usuario.telefone = currentUser.getPhoneNumber();
                usuario.conta = keyConta;

                saveUserLocal(usuario);

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void saveUserLocal(Usuario usuario) {
        // Salva informações na sessão
        try {
            Usuario.saveUsuario(context, usuario);
            System.out.println("usuarioLogado: " + Usuario.getUsuarioLogado(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
