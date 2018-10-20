package com.igorferrani.financeiro;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.igorferrani.financeiro.domain.Usuario;
import com.igorferrani.financeiro.domain.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static String TAG = "LOG_FIREBASE";
    private Activity context = this;
    private String verificationId;
    private ValueEventListener usuariosListener;
    private ValueEventListener usuarioListener;
    private FirebaseUser currentUser;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        Button btn_sms = findViewById(R.id.btn_sms);
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_numero_telefone = findViewById(R.id.et_numero_telefone);
                String numeroTelefone = et_numero_telefone.getText().toString();

                if (numeroTelefone != null) {
                    sendSms(numeroTelefone);
                }
            }
        });

        Button btn_entrar = findViewById((R.id.btn_entrar));
        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_code = findViewById(R.id.et_code);
                String codigoSms = et_code.getText().toString();

                loginPorTelefone(codigoSms);
            }
        });
    }

    private void sendSms(String phoneNumber) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, (Activity) context, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(String vId, PhoneAuthProvider.ForceResendingToken token) {
                verificationId = vId;

                LinearLayout block_telefone = findViewById(R.id.block_telefone);
                block_telefone.setVisibility(View.GONE);

                LinearLayout block_codigo = findViewById(R.id.block_codigo);
                block_codigo.setVisibility(View.VISIBLE);

                Util.showToast(context, "Código de verificação enviado por SMS");
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String code = phoneAuthCredential.getSmsCode();

                if (code != null) {
                    EditText et_code = findViewById(R.id.et_code);
                    et_code.setText(code);
                    loginPorTelefone(code);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Util.showToast(context, "Não foi possível enviar o código via SMS. Tente novamente: " + e.getMessage());
                TextView tv_erro = findViewById(R.id.tv_erro);
                tv_erro.setText(e.getMessage());
            }
        });
    }

    private void loginPorTelefone(String code) {
        if (code != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            mAuth.signInWithCredential(credential).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");

                        final FirebaseUser user = task.getResult().getUser();
                        String uid = user.getUid();

                        final Query ref = database.getReference(Usuario.FB_KEY_USUARIOS).orderByChild("uid").startAt(uid);
                        usuariosListener = ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Object object = dataSnapshot.getValue();

                                if (object == null) {
                                    Usuario usuario = new Usuario();
                                    usuario.telefone = user.getPhoneNumber();
                                    usuario.uid = user.getUid();

                                    database.getReference(Usuario.FB_KEY_USUARIOS).child(usuario.uid).setValue(usuario);

                                    Intent intent = new Intent(context, CadastroActivity.class);
                                    startActivity(intent);
                                } else {
                                    updateUI(user);
                                }

                                ref.removeEventListener(usuariosListener);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Util.showToast(context, "Erro ao relizar busca: " + databaseError.getMessage());
                            }
                        });

                        // ...
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                }
            });
        }
    }

    private void saveUserLocal(Usuario usuario) {
        // Salva informações na sessão
        Usuario.saveUsuario(context, usuario);
        try {
            System.out.println("usuarioLogado: " + Usuario.getUsuarioLogado(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateUI(final FirebaseUser currentUser) {
        if (currentUser != null) {
            final Query ref = database.getReference(Usuario.FB_KEY_USUARIOS).orderByChild("uid").startAt(currentUser.getUid());
            usuarioListener = ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    for (DataSnapshot usuarioSnapshot: dataSnapshot.getChildren()) {

                        Usuario object = usuarioSnapshot.getValue(Usuario.class);

                        System.out.println("usuarioSnapshot: ");
                        System.out.println(object);

                        if (object != null) {
                            Usuario usuario = new Usuario();
                            usuario.uid = currentUser.getUid();
                            usuario.telefone = currentUser.getPhoneNumber();
                            usuario.conta = object.conta;

                            saveUserLocal(usuario);

                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }

                    ref.removeEventListener(usuarioListener);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Util.showToast(context, "Erro ao relizar busca: " + databaseError.getMessage());
                }
            });
        } else {

        }
    }
}
