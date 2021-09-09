package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.arkanoid.ActivityUtil.SoundControlActivity;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Util.QueryExecutor;

public class classifica_activity extends SoundControlActivity {
    private TextView ultimoPunteggioLabel;
    private ListView listaClassifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifica);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.ultimoPunteggioLabel = this.findViewById(R.id.ultimoPunteggioLabel);
        this.listaClassifica = this.findViewById(R.id.listaClassifica);

        this.configuraListaClassifica();
        this.configuraUltimoPunteggio();
    }

    /**
     * Configura la lista della classifica
     */
    private void configuraListaClassifica(){
        if(this.listaClassifica != null){
            try{
                listaClassifica.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout, QueryExecutor.recuperaClassifica()));
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Configura l'ultimo punteggio del giocatore
     */
    private void configuraUltimoPunteggio(){
        if(ultimoPunteggioLabel != null){
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
            if(recordSalvataggio.isLogin()){
                try{
                    ultimoPunteggioLabel.setText(recordSalvataggio.getNomeUtente() + " " + QueryExecutor.recuperoPunteggioMassimoGiocatore(recordSalvataggio.getEmail()));
                }catch (Exception e){e.printStackTrace();}
            }else
                this.ultimoPunteggioLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        this.startActivity(new Intent(this, main_menu_activity.class ));
    }
}