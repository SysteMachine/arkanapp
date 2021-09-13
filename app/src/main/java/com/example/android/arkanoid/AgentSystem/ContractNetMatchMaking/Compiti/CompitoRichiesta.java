package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti;

import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.MatchListener;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.Util.QueryExecutor;

import java.util.ArrayList;

public class CompitoRichiesta extends Compito {
    private final float[] pesiRichieste = {0.1f, 0.9f};

    private final int TEMPO_MASSIMO_ATTESA_PROPOSTE = 5000;         //Tempo massimo di attesa per le proposte
    private final int TEMPO_MASSIMO_ATTESA_CONFERMA_FINALE = 2000;  //Tempo massimo di conferma finale

    private int fase;                                               //Fase attuale di esecuzione del compito
    private boolean canAccept;                                      //Flag di accettazione
    private ArrayList<RecordProposta> proposte;                     //Proposte arrivate
    private RecordProposta propostaTarget;                          //Proposta migliore per l'associazione

    private long timeStampInizioAttesaRispote;                      //TimeStampe per l'attesa delle risposte
    private long timeStampInizioAttesaFinale;                       //TimeStamp per l'attesa della conferma finale

    private MatchListener listener;                                 //Listener di match

    public CompitoRichiesta() {
        super("CompitoRichiesta");
        this.proposte = new ArrayList<>();
        this.canAccept = true;
        this.fase = 0;
    }

    @Override
    public void action() {
        if(this.fase == 0){
            //Fase di richiesta
            System.out.println("Invio una richiesta di match");
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
            this.proposte.clear();
        }
        if(this.fase == 1){
            //Fase di accettazione
            System.out.println("Inizio a ricevere le connessioni");
            if(System.currentTimeMillis() - this.timeStampInizioAttesaRispote < this.TEMPO_MASSIMO_ATTESA_PROPOSTE){
                //Se il tempo trascorso non supera il massimo definito
                MessageBox messaggio = this.myAgent.prelevaMessaggio();
                if(messaggio != null && messaggio.getMessageType().equals(MessageBox.TYPE_PROPOSE)){
                    System.out.println("Ricevuta una proposta da " + messaggio.getFrom());
                    this.proposte.add(this.generaRecordProposta(messaggio));
                }
            }else{
                System.out.println("Terminata la recezione, arrivate: " + this.proposte.size() + " richieste. Inizio controllo");
                this.fase = 2;
            }
        }
        if(this.fase == 2){
            //Fase di scelta
            System.out.println("Inizamo a scegliere");
            if(this.proposte.size() != 0){
                float[] statistiche = QueryExecutor.getParametriAccount(GA.salvataggio.getEmail());
                float mioPunteggio = this.getPunteggio(new RecordProposta(GA.salvataggio.getEmail(), statistiche[0], statistiche[1]));

                RecordProposta recordProposta = this.proposte.get(0);
                float primaDistanza = Math.abs(mioPunteggio - this.getPunteggio(recordProposta));
                for(int i = 1; i < this.proposte.size(); i++){
                    //Controlliamo tutte le proposte

                    float distanza = Math.abs(mioPunteggio - this.getPunteggio(this.proposte.get(i)));
                    if(distanza < primaDistanza) {
                        primaDistanza = distanza;
                        recordProposta = this.proposte.get(i);
                    }
                }
                System.out.println("Scelto: " + recordProposta.getEmail());
                this.propostaTarget = recordProposta;
                this.fase = 3;
            }else this.fase = 0;
        }
        if(this.fase == 3){
            //Fase di conferma
            if(this.propostaTarget != null){
                if(this.canAccept){
                    System.out.println("Invio conferma a " + this.propostaTarget.getEmail());
                    MessageBox messggio = new MessageBox(
                            GA.salvataggio.getEmail(),
                            this.myAgent.getNomeAgente(),
                            this.propostaTarget.email,
                            "AgenteRecezioneRichieste",
                            MessageBox.TYPE_ACCEPT,
                            "ACCEPT"
                    );
                    this.myAgent.inviaMessaggio(messggio);
                    this.timeStampInizioAttesaFinale = System.currentTimeMillis();
                    this.fase = 4;
                }else this.fase = 6;//Non trova il match e si chiude
            }else this.fase = 0;
        }
        if(this.fase == 4){
            //Fase finale di conferma
            if(System.currentTimeMillis() - this.timeStampInizioAttesaFinale < this.TEMPO_MASSIMO_ATTESA_CONFERMA_FINALE){
                MessageBox messaggio = this.myAgent.prelevaMessaggio();
                if(messaggio != null){
                    if(messaggio.getMessageType().equals(MessageBox.TYPE_ACCEPT))
                        this.fase = 5;//Abbiamo terminato e possiamo consegnare il match
                    else if(messaggio.getMessageType().equals(MessageBox.TYPE_REJECT))
                        this.fase = 0;//l'agente non può più unirsi e restituisce reject, ricominciamo a cercare
                }
            }else this.fase = 0;
        }
    }

    @Override
    public boolean done() {
        boolean esito = false;
        if(this.fase == 5){
            System.out.println("Trovato il match con: " + this.getMatch());
            esito = true;
            if(this.listener != null)
                this.listener.match(this.getMatch());
        }
        if(this.fase == 6){
            esito = true;
        }
        return esito;
    }

    /**
     * Restituisce il match migliore
     * @return Restituisce la mail del giocatore in caso di avvenuto match, altrimenti restituisce null
     */
    public String getMatch(){
        String esito = null;

        if(this.fase == 5 && this.propostaTarget != null)
            esito = this.propostaTarget.email;

        return esito;
    }

    /**
     * Restituisce il punteggio della proposta
     * @param proposta Proposta di partita
     * @return Restituisce un valore di proposta
     */
    private float getPunteggio(RecordProposta proposta){
        float somma = proposta.getPunteggioMassimo() + proposta.getPunteggioMedio();
        float p1 = (proposta.getPunteggioMassimo() / somma) * this.pesiRichieste[0];
        float p2 = (proposta.getPunteggioMedio() / somma) * this.pesiRichieste[1];
        return p1 + p2;
    }

    /**
     * Genera il record della proposta dal messaggio ricevuto
     * @param messaggio Messaggio da convertire
     * @return Restituisce un RecordProposta
     */
    private RecordProposta generaRecordProposta(MessageBox messaggio){
        System.out.println(messaggio.getStringaMessaggio());
        String email = messaggio.getFrom();
        String[] content = messaggio.getContent().split("=")[1].split("-");
        float massimo = Float.valueOf(content[0]);
        float media = Float.valueOf(content[1]);

        return new RecordProposta(email, massimo, media);
    }

    //Beam
    public void setListener(MatchListener listener) {
        this.listener = listener;
    }

    public void setCanAccept(boolean canAccept) {
        this.canAccept = canAccept;
    }

    //Reconrd che contiene la risposta dell'agente
    private class RecordProposta{
        private String email;
        private float punteggioMassimo;
        private float punteggioMedio;

        public RecordProposta(String email, float punteggioMassimo, float punteggioMedio) {
            this.email = email;
            this.punteggioMassimo = punteggioMassimo;
            this.punteggioMedio = punteggioMedio;
        }

        public String getEmail() {
            return email;
        }

        public float getPunteggioMassimo() {
            return punteggioMassimo;
        }

        public float getPunteggioMedio() {
            return punteggioMedio;
        }
    }
}