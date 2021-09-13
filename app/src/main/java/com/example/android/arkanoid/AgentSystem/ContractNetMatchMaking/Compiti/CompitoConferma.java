package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti;

import com.example.android.arkanoid.AgentSystem.Compito;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.AgenteDiMatching;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.MessageBox;

public class CompitoConferma extends Compito{
    private final int TEMPO_MASSIMO_CONFERMA = 2000;            //Tempo massimo di conferma

    private String emailMatch;                                  //Email con cui effettuare il match
    private boolean confermato;                                 //Flag di conferma

    private long inizioConfermaTimeStamp;                       //Inizio del timer di reczione della conferma
    private int myRandomNumber;                                 //Numero randomico di questo agente
    private int randomNumber;                                   //Numero randomico dell'altro client

    public CompitoConferma(String emailMatch) {
        super("CompitoConferma");

        this.emailMatch = emailMatch;
        this.confermato = false;
        this.inizioConfermaTimeStamp = System.currentTimeMillis();
        this.myRandomNumber = (int)(Math.random() * 50000);
    }

    @Override
    public void action() {
        if(!this.confermato){
            //Se non è stato confermato
            MessageBox message = new MessageBox(
                    GA.salvataggio.getEmail(),
                    this.myAgent.getNomeAgente(),
                    this.emailMatch,
                    this.myAgent.getNomeAgente(),
                    MessageBox.CONFIRM_REQUEST,
                    "" + this.myRandomNumber
            );
            this.myAgent.inviaMessaggio(message);
        }

        MessageBox messaggio = this.myAgent.prelevaMessaggio();
        if(messaggio != null){
            if(messaggio.getFrom().equals(this.emailMatch)){
                //Se il messaggio è ricevuto solo dall'email trovata nella fase di match

                if(messaggio.getMessageType().equals(MessageBox.CONFIRM_REQUEST)){
                    MessageBox risposta = new MessageBox(
                            GA.salvataggio.getEmail(),
                            this.myAgent.getNomeAgente(),
                            messaggio.getFrom(),
                            messaggio.getFromAgentName(),
                            MessageBox.CONFIRM,
                            messaggio.getContent()
                    );
                    this.randomNumber = Integer.valueOf(messaggio.getContent());
                    this.myAgent.inviaMessaggio(risposta);
                }else if(messaggio.getMessageType().equals(MessageBox.CONFIRM)){
                    if(messaggio.getContent().equals("" + this.myRandomNumber))
                        this.confermato = true;
                }
            }
        }
    }

    @Override
    public boolean done() {
        boolean esito = System.currentTimeMillis() - this.inizioConfermaTimeStamp > this.TEMPO_MASSIMO_CONFERMA;
        if(esito)
            ((AgenteDiMatching)this.myAgent).notificaConferma(this.confermato, this.myRandomNumber > this.randomNumber);
        return esito;
    }
}
