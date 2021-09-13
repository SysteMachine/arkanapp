package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti;

import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;

import java.util.ArrayList;

public class CompitoRichiesta extends Compito {
    private final int TEMPO_MASSIMO_ATTESA_PROPOSTE = 5000;         //Tempo massimo di attesa per le proposte

    private int fase;                                               //Fase attuale di esecuzione del compito
    private ArrayList<String> proposte;                             //Proposte arrivate
    private long timeStampInizioAttesaRispote;                      //TimeStampe per l'attesa delle risposte

    public CompitoRichiesta() {
        super("CompitoRichiesta");
        this.proposte = new ArrayList<>();
        this.fase = 0;
    }

    @Override
    public void action() {
        if(this.fase == 0){
            MessageBox message = new MessageBox(
                    GA.salvataggio.getEmail(),
                    this.myAgent.getNomeAgente(),
                    MessageBox.BROADCAST_MESSAGE,
                    "AgenteRecezioneRichieste",
                    MessageBox.TYPE_CALL_FOR_PROPOSAL,
                    "EMPTY");
            this.myAgent.inviaMessaggio(message);
            this.timeStampInizioAttesaRispote = System.currentTimeMillis();
            this.fase = 1;
        }
    }

    @Override
    public boolean done() {
        return this.fase == 1;
    }
}