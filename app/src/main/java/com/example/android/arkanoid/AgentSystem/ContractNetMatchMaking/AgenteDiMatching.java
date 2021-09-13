package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking;

import com.example.android.arkanoid.AgentSystem.Agente;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoConferma;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoRecezione;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoRichiesta;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.Multiplayer.ServerMultiplayer;

public class AgenteDiMatching extends Agente implements MatchListener{
    private AgenteRichiesta agenteRichiesta;                            //Agente di richiesta
    private AgenteRecezioneRichieste agenteRecezioneRichieste;          //Agente di recezione delle richieste

    private CompitoRichiesta compitoRichiesta;                          //Compito dell'agente di richiesta
    private CompitoRecezione compitoRecezione;                          //Compito dell'agente di recezione

    private String emailMatch;                                          //Email di match

    public AgenteDiMatching() {
        super("AgenteMatching");
        this.agenteRichiesta = new AgenteRichiesta();
        this.agenteRecezioneRichieste = new AgenteRecezioneRichieste();

        this.compitoRichiesta = new CompitoRichiesta();
        this.compitoRichiesta.setListener(this);
        this.compitoRecezione = new CompitoRecezione();
        this.compitoRecezione.setListener(this);

        this.agenteRichiesta.addCompito(this.compitoRichiesta);
        this.agenteRecezioneRichieste.addCompito(this.compitoRecezione);

        this.avviaMatch();
    }

    /**
     * Avvia la fase di match tra agenti
     */
    public void avviaMatch(){
        this.compitoRichiesta.setCanAccept(true);
        this.compitoRecezione.setCanAccept(true);
    }

    /**
     * Evento invocato dal compito dell'agente quando finisce di eseguire la fase di conferma
     * @param statoConferma Esito del compito di conferma, se true ha avuto conferma  e i client sono pronti a comunicare, altrimenti da false
     * @param host Se true indica che il server verrà creato localmente su una porta di due numeri superiore alla corrente e l'altro giocatore si connetterà
     *             su questo server con una porta superirore di 1 rispetto a quella standard
     */
    public void notificaConferma(boolean statoConferma, boolean host){
        System.out.println("Lo stato della conferma è: " + statoConferma + " sono l'host: " + host);

        if(statoConferma && host){
            //Avvio del server
            new ServerMultiplayer(GA.salvataggio.getEmail(), this.emailMatch, GA.channel.getPorta() + 2);
        }

        if(statoConferma){
            //TODO: avviare il client con una chiamata di callback
        }else{
            //Se non c'è stata la conferma si ritorna a fare il matchmaking
            System.out.println("Nessuna conferma di connessione, riavviamo il matchmaking");
            this.avviaMatch();
        }
    }

    @Override
    public void match(Agente agente, String emailMatch) {
        this.emailMatch = emailMatch;
        this.compitoRecezione.setCanAccept(false);
        this.compitoRichiesta.setCanAccept(false);

        System.out.println("Finale di meccanismo match con: " + emailMatch);
        System.out.println("Fermati gli agenti di matching");
        System.out.println("Avvio la fase di conferma delle comunicazioni");
        this.addCompito(new CompitoConferma(emailMatch));
    }

    @Override
    protected void takedown() {
        this.agenteRecezioneRichieste.doDelete();
        this.agenteRichiesta.doDelete();
    }
}
