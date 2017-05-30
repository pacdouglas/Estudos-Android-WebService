package br.com.alura.agenda.firebase;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import br.com.alura.agenda.dao.AlunoDAO;
import br.com.alura.agenda.dto.AlunoSync;
import br.com.alura.agenda.event.AtualizaListaAlunoEvent;
import br.com.alura.agenda.modelo.Aluno;
import br.com.alura.agenda.sinc.AlunoSincronizador;


/**
 * Created by douglasmartins on 25/05/17.
 */

public class AgendaMessageService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        Map<String, String> mensagem = remoteMessage.getData();

        Log.i("Mensagem Recebida", String.valueOf(mensagem));

        converteParaAluno(mensagem);
    }

    private void converteParaAluno(Map<String, String> mensagem)
    {
        String chaveDeAcesso = "alunoSync";

        if(mensagem.containsKey(chaveDeAcesso))
        {
            String json = mensagem.get(chaveDeAcesso);

            ObjectMapper mapper = new ObjectMapper();
            try
            {
                AlunoSync alunoSync = mapper.readValue(json, AlunoSync.class);

                AlunoSincronizador alunoSincronizador = new AlunoSincronizador(AgendaMessageService.this);
                alunoSincronizador.sincroniza(alunoSync);

                EventBus eventBus = EventBus.getDefault();
                eventBus.post(new AtualizaListaAlunoEvent());

            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
