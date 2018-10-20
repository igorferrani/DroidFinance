package com.igorferrani.financeiro.domain;

import com.igorferrani.financeiro.R;

public class Conta {
    public String key;
    public String description;
    public double value;
    public int tipoConta;
    public int quantidadeParcelas;
    public int parcelaAtual;
    public double valorParcela;
    public int diaVencimento;
    public String observacao;
    public int status;
    public String dataDespesa;
    public int centroCusto;

    public static final String FB_KEY_CONTAS = "registros";

    public static final int CONTA_AVULSA = 1;
    public static final int CONTA_FIXA = 2;
    public static final int CONTA_PARCELADA = 3;

    public static final int STATUS_QUITADO = 1;

    public static int CENTRO_CUSTO_ENTRADA = 1;
    public static int CENTRO_CUSTO_SAIDA = 2;

    public int getIdTipoConta(int tipoConta) {
        int tipoId;
        if (tipoConta == R.id.conta_fixa) {
            tipoId = CONTA_FIXA;
        } else if (tipoConta == R.id.conta_parcelada) {
            tipoId = CONTA_PARCELADA;
        } else {
             tipoId = CONTA_AVULSA;
        }
        return tipoId;
    }
}
