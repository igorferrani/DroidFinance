package com.igorferrani.financeiro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static String TAG = "LOG_FIREBASE";
    private Activity context = this;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        Button btn_sms = findViewById(R.id.btn_sms);
        btn_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText et_numero_telefone = findViewById(R.id.et_numero_telefone);
                String numeroTelefone = et_numero_telefone.getText().toString();

                sendSms(numeroTelefone);
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
            }

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                String a = "";
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                String b = "";
            }
        });
    }

    private void loginPorTelefone(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);

        mAuth.signInWithCredential(credential).addOnCompleteListener(context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success");

                    FirebaseUser user = task.getResult().getUser();

                    updateUI(user);
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

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }

                // ...
            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        if (currentUser != null) {
            finish();

            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
        } else {

        }
    }
}
