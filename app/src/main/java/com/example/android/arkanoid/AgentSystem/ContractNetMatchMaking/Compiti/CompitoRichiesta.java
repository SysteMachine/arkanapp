package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti;

import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.MatchListener;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.RecordProposta;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;
import com.example.android.arkanoid.Util.QueryExecutor;

import java.util.ArrayList;

public class CompitoRichiesta extends Compito {
    private final float[] pesiRichieste = {0.1f, 0.9f};             //Pesi dei valori di proposta

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
        this.canAccept = false;
        this.fase = -1;
    }

    /**
     * Fase di stallo
     */
    private void fasem1(){
        if(this.canAccept)
            this.fase = 0;
    }

    /**
     * Fase di invio delle richieste
     */
    private void fase0(){
        if(this.canAccept){
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
            this.proposte.clear();
            System.out.println("Inizio a ricevere le risposte");
            this.fase = 1;
        }else this.fase = -1;
    }

    /**
     * Fase 1 di accettazione delle richieste
     */
    private void fase1(){
        if(this.canAccept){
            if(System.currentTimeMillis() - this.timeStampInizioAttesaRispote < this.TEMPO_MASSIMO_ATTESA_PROPOSTE){
                MessageBox messaggio = this.myAgent.prelevaMessaggio();
                if(messaggio != null && messaggio.getMessageType().equals(MessageBox.TYPE_PROPOSE)){
                    System.out.println("Ricevuta una risposta dall'agente dell'utente " + messaggio.getFrom() + ":" + messaggio.getFromAgentName());
                    this.proposte.add(this.generaRecordProposta(messaggio));
                }
            }else{
                System.out.println("Fase di recezione delle risposte conclusa");
                this.fase = 2;
            }
        }else this.fase = -1;
    }

    /**
     * Fase di selezione del migliore agente da contattare
     */
    private void fase2(){
        if(this.canAccept){
            System.out.println("Fase di ricerca della migliore associazzione");
            if(this.proposte.size() != 0){
                //Se sono state ricevute delle richieste
                float[] statistiche = QueryExecutor.getParametriAccount(GA.salvataggio.getEmail());
                float mioPunteggio = this.getPunteggio(new RecordProposta(GA.salvataggio.getEmail(), statistiche[0], statistiche[1]));

                RecordProposta recordProposta = this.proposte.get(0);
                float primaDistanza = Math.abs(mioPunteggio - this.getPunteggio(recordProposta));
                for(int i = 1; i < this.proposte.size(); i++){
                    //Controlliamo tutte le proposte e se la distanza tra i punteggi è minore di quella presente viene fatto lo scambio
                    float distanza = Math.abs(mioPunteggio - this.getPunteggio(this.proposte.get(i)));
                    if(distanza < primaDistanza) {
                        primaDistanza = distanza;
                        recordProposta = this.proposte.get(i);
                    }
                }
                System.out.println("L'utente migliore per il match è: " + recordProposta.getEmail());
                this.propostaTarget = recordProposta;
                this.fase = 3;
            }else {
                System.out.println("Non sono state ricevute proposte, ritorno alla fase 0");
                this.fase = 0;
            }
        }else this.fase = -1;
    }

    /**
     * Fase di conferma dell'associazione
     */
    private void fase3(){
        if(this.canAccept){
            if(this.propostaTarget != null){
                System.out.println("Invio conferma a " + this.propostaTarget.getEmail());
                MessageBox messggio = new MessageBox(
                        GA.salvataggio.getEmail(),
                        this.myAgent.getNomeAgente(),
                        this.propostaTarget.getEmail(),
                        "AgenteRecezioneRichieste",
                        MessageBox.TYPE_ACCEPT,
                        "ACCEPT"
                );
                this.timeStampInizioAttesaFinale = System.currentTimeMillis();
                this.myAgent.inviaMessaggio(messggio);
                System.out.println("Attendo la conferma finale da " + this.propostaTarget.getEmail());
                this.fase = 4;
            }else{
                System.out.println("La proposta target è vuota, ritorno alla fase 0");
                this.fase = 0;
            }
        }else this.fase = -1;
    }

    /**
     * Fase conclusiva di risposta dall'altro agente
     */
    private void fase4(){
        if(this.canAccept){
            if(System.currentTimeMillis() - this.timeStampInizioAttesaFinale < this.TEMPO_MASSIMO_ATTESA_CONFERMA_FINALE){
                MessageBox messaggio = this.myAgent.prelevaMessaggio();
                if(messaggio != null){
                    if(messaggio.getMessageType().equals(MessageBox.TYPE_ACCEPT)){
                        System.out.println(propostaTarget.getEmail() + " Ha accettato la nostra richiesta match avvenuto!");
                        if(this.listener != null){
                            this.fase = -1;
                            this.canAccept = false;
                            this.listener.match(this.myAgent, this.propostaTarget.getEmail());
                        }
                    }else if(messaggio.getMessageType().equals(MessageBox.TYPE_REJECT)){
                        System.out.println(propostaTarget.getEmail() + " non ha accettato la nostra richiesta, ritorno alla fase 0");
                        this.fase = 0;
                    }
                }
            }else{
                System.out.println("Tempo di attesa per la riposta finale conluso, ritorno alla fase 0");
                this.fase = 0;
            }
        }else this.fase = -1;
    }

    @Override
    public void action() {
        if(this.fase == -1)
            this.fasem1();
        if(this.fase == 0)
            this.fase0();
        if(this.fase == 1)
            this.fase1();
        if(this.fase == 2)
            this.fase2();
        if(this.fase == 3)
            this.fase3();
        if(this.fase == 4)
            this.fase4();
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

    public boolean isCanAccept() {
        return canAccept;
    }
}