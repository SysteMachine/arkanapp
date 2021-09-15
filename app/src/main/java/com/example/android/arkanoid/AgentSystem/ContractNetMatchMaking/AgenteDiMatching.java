package com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking;

import com.example.android.arkanoid.AgentSystem.Agente;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoConferma;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoRecezione;
import com.example.android.arkanoid.AgentSystem.ContractNetMatchMaking.Compiti.CompitoRichiesta;
import com.example.android.arkanoid.AgentSystem.DF;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.RecordClient;
import com.example.android.arkanoid.Multiplayer.ServerMultiplayer;
import com.example.android.arkanoid.Util.Util;
import com.example.android.arkanoid.multiplayer_activity;

public class AgenteDiMatching extends Agente implements MatchListener{
    private AgenteRichiesta agenteRichiesta;                            //Agente di richiesta
    private AgenteRecezioneRichieste agenteRecezioneRichieste;          //Agente di recezione delle richieste

    private CompitoRichiesta compitoRichiesta;                          //Compito dell'agente di richiesta
    private CompitoRecezione compitoRecezione;                          //Compito dell'agente di recezione

    private String emailMatch;                                          //Email di match
    private multiplayer_activity activity;                              //Activity di multiplayer

    public AgenteDiMatching(multiplayer_activity activity) {
        super("AgenteMatching");
        this.activity = activity;
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
            //Per convenzione il giocatore 1 è l'host
        }

        if(statoConferma){
            //Avviamo il client con una chiamata di callback all'activity
            DF df = (DF)GA.container.findAgenteByName("DF");
            String ip = "";
            int porta = -1;
            if(host){
                //Se l'host è questo giocatore
                RecordClient client = df.getClient(GA.salvataggio.getEmail());
                if(client != null)
                    ip = client.getLocalIp();
                    porta = GA.channel.getPorta() + 2;
            }else{
                //Se l'host è l'altro giocatore
                RecordClient client = df.getClient(this.emailMatch);
                if(client != null) {
                    ip = client.getClientip().equals(client.getIp()) ? client.getLocalIp() : client.getIp();
                    porta = client.getPorta() + 2;
                }
            }
            this.activity.avviaModalita(ip, porta);
            //String ipServer
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
