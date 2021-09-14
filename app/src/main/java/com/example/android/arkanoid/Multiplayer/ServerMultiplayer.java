package com.example.android.arkanoid.Multiplayer;

import com.example.android.arkanoid.AgentSystem.DF;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.RecordClient;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiGioco.ModalitaMultiplayer.ServerBall;
import com.example.android.arkanoid.GameElements.ElementiGioco.ModalitaMultiplayer.ServerPaddle;
import com.example.android.arkanoid.Util.LoopTimer;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.TimerListener;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class ServerMultiplayer implements Runnable, TimerListener {
    public final static String CLIENT_ALIVE = "CLIENT_ALIVE";
    public final static String SERVER_ALIVE = "SERVER_ALIVE";

    private final int MASSIMO_TEMPO_VITA = 10000;     //Tampo massimo entro il quale un giocatore può essere giudicato vivo
    private final int TICK_SERVER = 20;               //Tick del server

    private String ipGiocatore1;                //Ip del giocatore 1
    private String ipGiocatore2;                //Ip del giocatore 2
    private int portaGiocatore1;                //Porta del giocatore 1
    private int portaGiocatore2;                //Porta del giocatore 2
    private long lastTimestampGiocatore1;       //Ultimo riferimento temporale di connessione del giocatore1
    private long lastTimestampGiocatore2;       //Ultimo riferimento temporale di connessione del giocatore2;

    private Thread threadLoop;                  //Thread del loop del server
    private boolean running;                    //Flag di running del loop del server
    private DatagramSocket socket;              //Socket di connessione

    //Gestione della logica del server
    private LoopTimer tickTimer;                    //Tick del server
    private ServerPaddle paddleGiocatore1;          //Paddle del giocatore 1
    private ServerPaddle paddleGiocatore2;          //Paddle del giocatore 2
    private ServerBall palla;                       //Palla del server
    private ArrayList<Entity> entita;               //Entità del server
    //--------------------------------

    public ServerMultiplayer(String emailGiocatore1, String emailGiocatore2, int porta){
        System.out.println("Avvio un server multiplayer sulla porta: " + porta);
        DF df = (DF)GA.container.findAgenteByName("DF");

        RecordClient recordGiocatore1 = df.getClient(emailGiocatore1);
        RecordClient recordGiocatore2 = df.getClient(emailGiocatore2);
        if(recordGiocatore1 != null && recordGiocatore2 != null){
            //Identifichiamo gli ip dei giocatori
            this.ipGiocatore1 = recordGiocatore1.getClientip().equals(recordGiocatore1.getIp()) ? recordGiocatore1.getLocalIp() : recordGiocatore1.getIp();
            this.ipGiocatore2 = recordGiocatore2.getClientip().equals(recordGiocatore2.getIp()) ? recordGiocatore2.getLocalIp() : recordGiocatore2.getIp();
            this.portaGiocatore1 = recordGiocatore1.getPorta() + 1;
            this.portaGiocatore2 = recordGiocatore2.getPorta() + 1;
            this.lastTimestampGiocatore1 = System.currentTimeMillis();
            this.lastTimestampGiocatore2 = System.currentTimeMillis();

            System.out.println("SERVER: gli indirizzi ip sono per g1 e g2 rispettivamente: " + this.ipGiocatore1 + ":" + this.portaGiocatore1 + ", " + this.ipGiocatore2 + ":" + this.portaGiocatore2);
            System.out.println("Apro il socket");
            try{
                this.socket = new DatagramSocket(porta);
                socket.setSoTimeout(2000);
                this.running = true;
                this.threadLoop = new Thread(this);
                this.threadLoop.start();
                this.avviaElementiServer();
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Avvia gli elementi di server
     */
    private void avviaElementiServer(){
        this.entita = new ArrayList<>();
        this.paddleGiocatore1 = new ServerPaddle(new Vector2D(720 / 2, 30), new Vector2D(40, 10), new Vector2D(800, 800));
        this.paddleGiocatore2 = new ServerPaddle(new Vector2D(720 / 2, 1280 - 30), new Vector2D(40, 10), new Vector2D(800, 800));
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

    /**
     * Aggiorna lo stato di vita di un client
     * @param messaggio Messaggio arrivato dal socket
     */
    private void controlloIsAlive(DatagramPacket messaggio){
        String dati = new String(messaggio.getData()).substring(0, messaggio.getLength()).trim();
        if(dati.equals(ServerMultiplayer.CLIENT_ALIVE)){
            //Se il client è vivo
            if(messaggio.getAddress().getHostAddress().equals(this.ipGiocatore1)) {
                this.lastTimestampGiocatore1 = System.currentTimeMillis();
            }else {
                this.lastTimestampGiocatore2 = System.currentTimeMillis();
            }
        }
    }

    /**
     * Invia il messaggio di isAlive ai client
     */
    private void invioMessaggioIsAlive(){
        byte[] buffer = ServerMultiplayer.SERVER_ALIVE.getBytes();
        try{
            System.out.println("Invia il messaggio di server alive ai client");
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
            this.socket.send(pacchetto);
            pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void run() {
        System.out.println("Avviato thread interno del server");
        this.tickTimer = new LoopTimer(this, this.TICK_SERVER);
        while(this.running){
            byte[] buffer = new byte[60000];
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length);

            this.invioMessaggioIsAlive();

            try{
                this.socket.receive(pacchetto);
                this.controlloIsAlive(pacchetto);

            }catch (SocketTimeoutException e){}
            catch (Exception e){e.printStackTrace();}

            //Controlliamo la vita dei giocatori e nel caso uno dei due si sconnette, chiudiamo il server
            if(!this.isGiocatore1Vivo() || !this.isGiocatore2Vivo())
                this.running = false;
        }
        System.out.println("Terminato thread interno del server");
        this.tickTimer.stop();
        this.socket.close();
    }

    //Logica del server
    //------ --- ------

    /**
     * Metodo invocato quando è necessario inviare un evento
     * @param evento Identificativo dell'evento
     * @param parametri Parametri dell'evento
     */
    public void sendEvent(String evento, ParamList parametri){

    }

    @Override
    public void timeIsZero() {
        //Gestisce il tick del server
    }
}
