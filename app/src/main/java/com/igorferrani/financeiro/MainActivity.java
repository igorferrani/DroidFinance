package com.igorferrani.financeiro;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.igorferrani.financeiro.domain.Util;
import com.igorferrani.financeiro.fragment.ContaPagarFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Activity context = this;
    private static int CODE_CADASTRO_CONTA = 1;
    public static int mes_atual;
    public static int ano_atual;
    public static double valor_saldo;
    public static double valor_entrada;
    public static double valor_saida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleDateFormat formatter = new SimpleDateFormat("MM-yyyy");
        String data = formatter.format(Calendar.getInstance().getTime());

        mes_atual = Integer.parseInt(data.split("-")[0]);
        ano_atual = Integer.parseInt(data.split("-")[1]);

        refreshPage();

        Button btn_voltar_mes = findViewById(R.id.btn_voltar_mes);
        btn_voltar_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerVoltarMes();
                refreshPage();
            }
        });

        Button btn_proximo_mes = findViewById(R.id.btn_proximo_mes);
        btn_proximo_mes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlerProximoMes();
                refreshPage();
            }
        });

        FloatingActionButton fab = findViewById(R.id.btn_add_conta);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ContaActivity.class);
                startActivityForResult(intent, CODE_CADASTRO_CONTA);
            }
        });
    }

    private void handlerVoltarMes() {
        if (mes_atual >= 2 && mes_atual <= 12) {
            mes_atual--;
        } else {
            mes_atual = 12;
            ano_atual--;
        }
    }

    private void handlerProximoMes() {
        if (mes_atual >= 1 && mes_atual <= 11) {
            mes_atual++;
        } else {
            mes_atual = 1;
            ano_atual++;
        }
    }

    private void refreshPage() {
        TextView tv_mes_escrito = findViewById(R.id.tv_mes_escrito);
        tv_mes_escrito.setText(Util.getMesEscrito(mes_atual));

        createFragmentList();
    }

    public void setSaldo() {
        TextView tv_saldo = findViewById(R.id.tv_saldo);
        tv_saldo.setText("R$" + Double.toString(valor_saldo));

        TextView tv_entrada = findViewById(R.id.tv_entrada);
        tv_entrada.setText("R$" + Double.toString(valor_entrada));

        TextView tv_saida = findViewById(R.id.tv_saida);
        tv_saida.setText("R$" + Double.toString(valor_saida));
    }

    private void createFragmentList() {
        ContaPagarFragment frag = new ContaPagarFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.rl_fragment_container, frag, "mainFrag");
        ft.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_CADASTRO_CONTA) {
            if(resultCode == Activity.RESULT_OK){
                Util.showToast(context, "Conta registrada com sucesso !");
            }
        }
    }
}
