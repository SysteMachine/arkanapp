package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti;

import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.MatchListener;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.Util.DBUtil;
import com.example.android.arkanoid.Util.QueryExecutor;

import org.json.JSONObject;

public class CompitoRecezione extends Compito {
    private final String QUERY_RECUPERO_MEDIA_MASSIMO_PUNTEGGIO = "SELECT MAX(punteggio_punteggio) AS MASSIMO, AVG(punteggio_punteggio) AS MEDIA, punteggio_user_email AS USER FROM punteggio WHERE punteggio_user_email LIKE EMAIL";

    private boolean canAccept;      //Flag di accettazione, se true l'agente può accettare
    private MatchListener listener; //Listener del match

    public CompitoRecezione() {
        super("CompitoRecezione");
        this.canAccept = true;
    }


    @Override
    public void action() {
        MessageBox messaggio = this.myAgent.prelevaMessaggio();
        if(messaggio != null && !messaggio.getFrom().equals(GA.salvataggio.getEmail())){
            if(messaggio.getMessageType().equals(MessageBox.TYPE_CALL_FOR_PROPOSAL)){
                System.out.println("Ricevuta una richiesta da " + messaggio.getFrom());
                float[] params = QueryExecutor.getParametriAccount(GA.salvataggio.getEmail());
                String content = "PARAMETRI=" + params[0] + "-" + params[1];
                MessageBox risposta = new MessageBox(
                        GA.salvataggio.getEmail(),
                        this.myAgent.getNomeAgente(),
                        messaggio.getFrom(),
                        messaggio.getFromAgentName(),
                        MessageBox.TYPE_PROPOSE,
                        content
                );
                System.out.println("Invio una proposta a " + messaggio.getFrom());
                this.myAgent.inviaMessaggio(risposta);
            }

            if(messaggio.getMessageType().equals(MessageBox.TYPE_ACCEPT)){
                MessageBox risposta = new MessageBox(
                        GA.salvataggio.getEmail(),
                        this.myAgent.getNomeAgente(),
                        messaggio.getFrom(),
                        messaggio.getFromAgentName(),
                        this.canAccept ? MessageBox.TYPE_ACCEPT : MessageBox.TYPE_REJECT,
                        "RESPONSE"
                );
                System.out.println("Ricevuta una richiesta da: " + messaggio.getFrom() + "ed io posso: " + this.canAccept);
                this.myAgent.inviaMessaggio(risposta);
                if(this.canAccept && this.listener != null) {
                    this.canAccept = false;
                    this.listener.match(messaggio.getFrom());
                }
            }
        }
    }

    /**
     * Ripristina lo stato di accettazione
     */
    public void reset(){
        this.canAccept = true;
    }

    //Beam

    public void setListener(MatchListener listener) {
        this.listener = listener;
    }


}