package br.com.alura.agenda.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by douglasmartins on 26/05/17.
 */

public class AlunoPreferences
{

    private final Context context;
    private final String ALUNO_PREFERENCES = "br.com.alura.agenda.preferences.AlunoPreferences";
    private final String VERSAO_DO_DADO = "versao_do_dado";
    private String versao;

    public AlunoPreferences(Context context)
    {
        this.context = context;
    }

    public void salvaVersao(String versao)
    {
        SharedPreferences.Editor edit = getSharedPreferences().edit();
        edit.putString(VERSAO_DO_DADO, versao);
        edit.commit();
    }

    private SharedPreferences getSharedPreferences()
    {
        return context.getSharedPreferences(ALUNO_PREFERENCES, context.MODE_PRIVATE);
    }

    public String getVersao()
    {
        return getSharedPreferences().getString(VERSAO_DO_DADO, "");
    }

    public boolean temVersao()
    {
        return !getVersao().isEmpty();
    }
}
