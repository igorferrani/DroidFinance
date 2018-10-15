package com.igorferrani.financeiro.domain;

import android.content.Context;
import android.widget.Toast;

public class Util {
    public static void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static String getMesEscrito(int numeroMes) {
        String mes = "";
        switch (numeroMes) {
            case 1:
                mes = "Janeiro";
                break;
            case 2:
                mes = "Fevereiro";
                break;
            case 3:
                mes = "Março";
                break;
            case 4:
                mes = "Abril";
                break;
            case 5:
                mes = "Maio";
                break;
            case 6:
                mes = "Junho";
                break;
            case 7:
                mes = "Julho";
                break;
            case 8:
                mes = "Agosto";
                break;
            case 9:
                mes = "Setembro";
                break;
            case 10:
                mes = "Outubro";
                break;
            case 11:
                mes = "Novembro";
                break;
            case 12:
                mes = "Dezembro";
                break;
        }
        return mes;
    }
}
