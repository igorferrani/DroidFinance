package com.igorferrani.financeiro;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.igorferrani.financeiro.domain.Registro;
import com.igorferrani.financeiro.domain.Usuario;
import com.igorferrani.financeiro.domain.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity {

    private Activity context = this;
    private RadioGroup rg_tipo_centro_custo;
    private EditText et_description;
    private EditText et_valor;
    private RadioGroup rg_tipo_conta;
    private EditText et_quantidade_parcelas;
    private EditText et_parcela_atual;
    private EditText et_valor_parcela;
    private EditText et_dia_vencimento;
    private EditText et_observacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rg_tipo_centro_custo = findViewById(R.id.rg_tipo_centro_custo);
        et_description = findViewById(R.id.et_description);
        et_valor = findViewById(R.id.et_valor);
        rg_tipo_conta = findViewById(R.id.rg_tipo_conta);
        et_quantidade_parcelas = findViewById(R.id.et_quantidade_parcelas);
        et_parcela_atual = findViewById(R.id.et_parcela_atual);
        et_valor_parcela = findViewById(R.id.et_valor_parcela);
        et_dia_vencimento = findViewById(R.id.et_dia_vencimento);
        et_observacao = findViewById(R.id.et_observacao);

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
            finish();
            return;
        }

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(Registro.FB_KEY_REGISTROS).child(keyConta);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg_tipo_conta);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LinearLayout block_parcelas = findViewById(R.id.block_parcelas);
                block_parcelas.setVisibility(View.GONE);

                LinearLayout block_dia_vencimento = findViewById(R.id.block_dia_vencimento);
                block_dia_vencimento.setVisibility(View.GONE);

                // find which radio button is selected
                if(checkedId == R.id.conta_avulsa) {

                } else if(checkedId == R.id.conta_fixa) {
                    block_dia_vencimento.setVisibility(View.VISIBLE);
                } else if (checkedId == R.id.conta_parcelada) {
                    block_parcelas.setVisibility(View.VISIBLE);
                    block_dia_vencimento.setVisibility(View.VISIBLE);
                }
            }
        });


        Button btn_salvar = findViewById(R.id.btn_salvar);
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    Registro registro = new Registro();

                    if (rg_tipo_centro_custo.getCheckedRadioButtonId() == R.id.centro_despesa) {
                        registro.centroCusto = Registro.CENTRO_CUSTO_SAIDA;
                    } else {
                        registro.centroCusto = Registro.CENTRO_CUSTO_ENTRADA;
                    }

                    registro.description = et_description.getText().toString();
                    registro.value = Double.parseDouble(et_valor.getText().toString());
                    registro.tipoConta = registro.getIdTipoConta(rg_tipo_conta.getCheckedRadioButtonId());

                    if (rg_tipo_conta.getCheckedRadioButtonId() == R.id.conta_parcelada) {
                        registro.quantidadeParcelas = Integer.parseInt(et_quantidade_parcelas.getText().toString());
                        registro.parcelaAtual = Integer.parseInt(et_parcela_atual.getText().toString());
                        registro.valorParcela = Double.parseDouble(et_valor_parcela.getText().toString());
                    }

                    if (rg_tipo_conta.getCheckedRadioButtonId() == R.id.conta_parcelada || rg_tipo_conta.getCheckedRadioButtonId() == R.id.conta_fixa) {
                        registro.diaVencimento = Integer.parseInt(et_dia_vencimento.getText().toString());
                    }

                    registro.observacao = et_observacao.getText().toString();

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                    registro.dataDespesa = formatter.format(Calendar.getInstance().getTime());

                    myRef.push().setValue(registro);

                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private boolean validateForm() {
        if (et_description.getText().toString().isEmpty()) {
            Util.showToast(context, "É necessário preencher com uma descrição");
            return false;
        }

        if (et_valor.getText().toString().isEmpty()) {
            Util.showToast(context, "É necessário informar o valor total da conta");
            return false;
        }

        if (rg_tipo_conta.getCheckedRadioButtonId() == R.id.conta_parcelada) {
            if (et_quantidade_parcelas.getText().toString().isEmpty()) {
                Util.showToast(context, "É necessário informar a quantidade de parcelas");
                return false;
            }

            if (et_parcela_atual.getText().toString().isEmpty()) {
                Util.showToast(context, "É necessário informar o número da parcela atual");
                return false;
            }

            if (et_valor_parcela.getText().toString().isEmpty()) {
                Util.showToast(context, "É necessário informar o valor da parcela");
                return false;
            }
        }

        if (rg_tipo_conta.getCheckedRadioButtonId() == R.id.conta_parcelada || rg_tipo_conta.getCheckedRadioButtonId() == R.id.conta_fixa) {
            if (et_dia_vencimento.getText().toString().isEmpty()) {
                Util.showToast(context, "É necessário informar o dia de vencimento da conta");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
