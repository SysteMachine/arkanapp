package com.example.android.arkanoid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.android.arkanoid.ActivityUtil.SoundControlActivity;
import com.example.android.arkanoid.AgentSystem.Agente;
import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.AgenteRecezioneRichieste;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.AgenteRichiesta;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoRecezione;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoRichiesta;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Util.Timer;
import com.example.android.arkanoid.Util.TimerListener;

import java.util.ArrayList;

public class multiplayer_activity extends SoundControlActivity implements View.OnClickListener {
    private ListView listView;
    private Button button;

    private AgenteRichiesta agenteRichiesta;
    private AgenteRecezioneRichieste agenteRecezione;
    private CompitoRichiesta compitoRichiesta;
    private CompitoRecezione compitoRecezione;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        this.button = this.findViewById(R.id.buttonStart);
        if(this.button != null) {
            System.out.println("attivato il listener");
            this.button.setOnClickListener(this);
        }
        this.listView = this.findViewById(R.id.listaMessaggi);

        //

        this.agenteRecezione = new AgenteRecezioneRichieste();
        this.compitoRecezione = new CompitoRecezione();
        this.agenteRecezione.addCompito(this.compitoRecezione);

        GA.container.addAgente(this.agenteRecezione);
    }

    @Override
    public void onClick(View v) {
        System.out.println("Creazione");
        if(this.agenteRichiesta != null) {
            GA.container.removeAgente(this.agenteRichiesta);
            this.agenteRichiesta.doDelete();
        }
        this.agenteRichiesta = new AgenteRichiesta();
        this.compitoRichiesta = new CompitoRichiesta();
        this.agenteRichiesta.addCompito(this.compitoRichiesta);
        GA.container.addAgente(this.agenteRichiesta);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}