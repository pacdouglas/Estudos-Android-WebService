package br.com.alura.agenda.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import br.com.alura.agenda.retrofit.RetrofitInicializador;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by douglasmartins on 24/05/17.
 */

public class AgendaInstanceIDService extends FirebaseInstanceIdService
{
    @Override
    public void onTokenRefresh()
    {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Firebase", "Refreshed token: " + refreshedToken);

        enviaTokenParaServidor(refreshedToken);
    }

    private void enviaTokenParaServidor(final String refreshedToken)
    {
        new RetrofitInicializador().getDispositivoService().enviaToken(refreshedToken)
        .enqueue(new Callback<Void>()
        {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response)
            {
                Log.i("Token", refreshedToken);
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t)
            {
                Log.e("Token Falhou", t.getMessage());
            }
        });
    }
}
