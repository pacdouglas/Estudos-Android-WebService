package br.com.alura.agenda.sinc;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoSync;
import br.com.alura.agenda.event.AtualizaListaAlunoEvent;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.preferences.AlunoPreferences;
import br.com.alura.agenda.retrofit.RetrofitInicializador;
import br.com.alura.agenda.services.AlunoService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by douglasmartins on 26/05/17.
 */

public class AlunoSincronizador
{
    private Context context;
    private EventBus bus = EventBus.getDefault();
    private AlunoPreferences preferences;

    public AlunoSincronizador(Context context)
    {

        this.context = context;
        this.preferences = new AlunoPreferences(context);
    }

    public void buscaAlunos()
    {
        if(preferences.temVersao())
        {
            buscaNovos();
        }
        else
        {
            buscaTodos();
        }
    }

    private void buscaNovos()
    {
        AlunoService alunoService = new RetrofitInicializador().getAlunoService();
        alunoService.novos(preferences.getVersao())
        .enqueue(buscaAlunosCallback());
    }

    private void buscaTodos()
    {
        Call<AlunoSync> call = new RetrofitInicializador().getAlunoService().lista();
        call.enqueue(buscaAlunosCallback());
    }

    @NonNull
    private Callback<AlunoSync> buscaAlunosCallback()
    {
        return new Callback<AlunoSync>()
        {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response)
            {
                List<Aluno> alunos = response.body().getAlunos();
                String versao = response.body().getMomentoDaUltimaModificacao();
                preferences.salvaVersao(versao);

                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.sincroniza(alunos);
                alunoDAO.close();
                Log.i("Versao", preferences.getVersao());
                bus.post(new AtualizaListaAlunoEvent());

                sincronizaAlunosInternos();
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t)
            {
                Log.e("Failure Chamada", t.getMessage());
                bus.post(new AtualizaListaAlunoEvent());
            }
        };
    }

    private void sincronizaAlunosInternos()
    {
        final AlunoDAO dao = new AlunoDAO(context);
        List<Aluno> alunos = dao.listaNaoSincronizados();
        Call<AlunoSync> atualiza = new RetrofitInicializador().getAlunoService().atualiza(alunos);

        atualiza.enqueue(new Callback<AlunoSync>()
        {
            @Override
            public void onResponse(Call<AlunoSync> call, Response<AlunoSync> response)
            {
                AlunoSync body = response.body();
                dao.sincroniza(body.getAlunos());
                dao.close();
            }

            @Override
            public void onFailure(Call<AlunoSync> call, Throwable t)
            {
                dao.close();
            }
        });
    }

    public void deleta(final Aluno aluno)
    {
        new RetrofitInicializador().getAlunoService().deleta(aluno.getId())
                .enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                AlunoDAO alunoDAO = new AlunoDAO(context);
                alunoDAO.deleta(aluno);
                alunoDAO.close();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {

            }
        });
    }
}
