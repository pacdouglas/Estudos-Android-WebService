package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

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
    }
}
