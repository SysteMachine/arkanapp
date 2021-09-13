package com.example.android.arkanoid.Multiplayer;

import com.example.android.arkanoid.AgentSystem.DF;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.RecordClient;
import com.example.android.arkanoid.Util.Util;

public class ServerMultiplayer implements Runnable{
    public final static String CLIENT_ALIVE = "CLIENT_ALIVE";

    private int MASSIMO_TEMPO_VITA = 10000;     //Tampo massimo entro il quale un giocatore può essere giudicato vivo

    private String emailGiocatore1;             //Email del primo giocatore
    private String emailGiocatore2;             //Email del secondo giocatore
    private String ipGiocatore1;                //Ip del giocatore 1
    private String ipGiocatore2;                //Ip del giocatore 2
    private int portaGiocatore1;                //Porta del giocatore 1
    private int portaGiocatore2;             //Porta del giocatore 2
    private long lastTimestampGiocatore1;       //Ultimo riferimento temporale di connessione del giocatore1
    private long lastTimestampGiocatore2;       //Ultimo riferimento temporale di connessione del giocatore2;

    private Thread threadLoop;                  //Thread del loop del server
    private boolean running;                    //Flag di running del loop del server

    public ServerMultiplayer(String emailGiocatore1, String emailGiocatore2, int porta){
        System.out.println("Avvio un server multiplayer sulla porta: " + porta);
        DF df = (DF)GA.container.findAgenteByName("DF");

        String myIp = Util.getMyIp();
        RecordClient recordGiocatore1 = df.getClient(emailGiocatore1);
        RecordClient recordGiocatore2 = df.getClient(emailGiocatore2);
        if(recordGiocatore1 != null && recordGiocatore2 != null && !myIp.equals("")){
            //Identifichiamo gli ip dei giocatori
            this.ipGiocatore1 = recordGiocatore1.getIp().equals(myIp) ? recordGiocatore1.getLocalIp() : recordGiocatore1.getIp();
            this.ipGiocatore2 = recordGiocatore2.getIp().equals(myIp) ? recordGiocatore2.getLocalIp() : recordGiocatore2.getIp();
            this.portaGiocatore1 = recordGiocatore1.getPorta() + 1;
            this.portaGiocatore2 = recordGiocatore2.getPorta() + 1;

            System.out.println("SERVER: gli indirizzi ip sono per g1 e g2 rispettivamente: " + this.ipGiocatore1 + ":" + this.portaGiocatore1 + ", " + this.ipGiocatore2 + ":" + this.portaGiocatore2);
        }
    }

    /**
     * Controllo della vita del giocatore 1
     * @return Restituisce true se il giocatore 1 è vivo, altrimenti restituisce false
     */
    private boolean isGiocatore1Vivo(){
        return System.currentTimeMillis() - this.lastTimestampGiocatore1 < MASSIMO_TEMPO_VITA;
    }

    /**
     * Controllo della vita del giocatore 2
     * @return Restituisce true se il giocatore 2 è vivo, altrimenti restituisce false
     */
    private boolean isGiocatore2Vivo(){
        return System.currentTimeMillis() - this.lastTimestampGiocatore2 < MASSIMO_TEMPO_VITA;
    }

    @Override
    public void run() {
        System.out.println("Avviato thread interno del server");
        while(this.running){

        }
        System.out.println("Terminato thread interno del server");
    }
}
