package com.example.android.arkanoid.Multiplayer;

import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.Util.LoopTimer;
import com.example.android.arkanoid.Util.TimerListener;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class ClientMultiplayer implements Runnable, TimerListener {
    private final int MASSIMO_TEMPO_VITA = 10000;   //Massimo tempo di vita del server uwu

    private AbstractScene scena;            //Scena sotto il controllo del client
    private String ipServer;                //Ip del server
    private int portaServer;                //Porta del server
    private long lastServerTimeStamp;       //Ultimo timestamp del server

    private boolean running;                //flag di running
    private Thread threadRecezione;         //Thread di recezione
    private DatagramSocket socket;         //Socket di comunicazione

    private ClientListener listener;        //Listener del client
    private LoopTimer timerIsAlive;         //Timer di isAlive

    public ClientMultiplayer(String ipServer, int portaServer){
        System.out.println("Avviato il client sulla porta: " + ( GA.channel.getPorta() + 1 ) + " il server è: " + ipServer + ":" + portaServer);

        this.ipServer = ipServer;
        this.portaServer = portaServer;
        this.lastServerTimeStamp = System.currentTimeMillis();

        try{
            this.socket = new DatagramSocket(GA.channel.getPorta() + 1);
            this.socket.setSoTimeout(1500);
            this.running = true;
            this.threadRecezione = new Thread(this);
            this.threadRecezione.start();
            this.timerIsAlive = new LoopTimer(this, 2000);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Imposta la scena del client
     * @param scena Scena da impostare sul client
     */
    public void setScena(AbstractScene scena){
        this.scena = scena;
    }

    /**
     * Controlla lo stato della vita del server
     * @return Restituisce l'esito del controllo
     */
    public boolean isServerAlive(){
        return System.currentTimeMillis() - this.lastServerTimeStamp < this.MASSIMO_TEMPO_VITA;
    }

    /**
     * Controlla lo stato di vita del server
     * @param messaggio Messaggio ricevuto
     */
    private void controlloIsAlive(DatagramPacket messaggio){
        String dati = new String(messaggio.getData()).substring(0, messaggio.getLength()).trim();
        if(dati.equals(ServerMultiplayer.SERVER_ALIVE)) {
            this.lastServerTimeStamp = System.currentTimeMillis();
        }
    }

    /**
     * Invia il messaggio di isAlive al server
     */
    private void inviaMessaggioIsAlive(){
        byte[] buffer = ServerMultiplayer.CLIENT_ALIVE.getBytes();
        try{
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipServer), this.portaServer);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Ferma il client
     */
    public void stopClient(){
        this.running = false;
    }

    /**
     * Imposta il client listener
     * @param listener Lisener da associar
     */
    public void setClientListener(ClientListener listener){
        this.listener = listener;
    }

    @Override
    public void run() {
        System.out.println("Avviato il cliclo interno del client");
        while(this.running){
            byte[] buffer = new byte[60000];
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length);

            try{
                this.socket.receive(pacchetto);
                this.controlloIsAlive(pacchetto);
                if(this.listener != null)
                    this.listener.clientMessage(new String(pacchetto.getData()).substring(0, pacchetto.getLength()).trim());
            }catch (SocketTimeoutException e){}
            catch (Exception e){e.printStackTrace();}

            if(!this.isServerAlive())
                this.running = false;
        }
        System.out.println("Terminato il ciclo di running del client");
        this.timerIsAlive.stop();
        this.socket.close();
    }

    /**
     * Invia un messaggio al server
     */
    public void inviaMessaggioServer(String messaggio){
        byte[] buffer = messaggio.getBytes();
        try{
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipServer), this.portaServer);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void timeIsZero() {
        if(this.running)
            this.inviaMessaggioIsAlive();
    }
}