package com.igorferrani.financeiro.domain;

import android.content.Context;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class Usuario {
    public String key;
    public String uid;
    public String nome;
    public String telefone;
    public String email;
    public String foto;
    public String conta;

    public static final String FB_KEY_USUARIOS = "usuarios";
    public static final String SP_KEY_USUARIO_LOGADO = "usuarioLogado";

    public static void saveUsuario(Context context, Usuario usuario) {
        Gson gson = new Gson();
        String usuarioString = gson.toJson(usuario);
        Util.setDataSession(context, SP_KEY_USUARIO_LOGADO, usuarioString);
    }

    public static JSONObject getUsuarioLogado(Context context) throws JSONException {
        String usuarioString = Util.getDataSession(context, SP_KEY_USUARIO_LOGADO);
        JSONObject usuario = new JSONObject(usuarioString);
        return usuario;
    }
}
