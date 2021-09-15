package com.example.android.arkanoid.Multiplayer;

import com.example.android.arkanoid.AgentSystem.DF;
import com.example.android.arkanoid.AgentSystem.GA;
import com.example.android.arkanoid.AgentSystem.RecordClient;
import com.example.android.arkanoid.Util.LoopTimer;
import com.example.android.arkanoid.Util.TimerListener;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;


public class ServerMultiplayer implements Runnable, TimerListener {
    private final int SCREEN_WIDTH = 720;
    private final int SCREEN_HEIGHT = 1280;
    private final int ZONA_PUNTEGGIO = 60;

    public final static String CLIENT_ALIVE = "CLIENT_ALIVE";
    public final static String SERVER_ALIVE = "SERVER_ALIVE";
    public final static String INVIO_MESSAGGIO_SERVER = "INVIO_MESSAGGIO_SERVER";
    public final static String INVIO_START = "START";
    public final static String INVIO_STOP = "STOP";
    public final static String RICHIESTA_POSIZIONE_DIREZIONE_PALLA = "RICHIESTA_POSIZIONE_DIREZIONE_PALLA";
    public final static String RISPOSTA_POSIZIONE_DIREZIONE_PALLA = "RISPOSTA_POSIZIONE_DIREZIONE_PALLA";
    public final static String AGGIORNA_POSIZIONE_DIREZIONE_PALLA = "AGGIORNA_POSIZIONE_DIREZIONE_PALLA";
    public final static String START_OWNER = "START_OWNER";
    public final static String AGGIORNA_TARGET_X_PADDLE = "AGGIORNA_TARGET_X_PADDLE";
    public final static String TARGET_X_AVVERSARIO = "TARGET_X_AVVERSARIO";
    public final static String ESITO_PUNTEGGIO = "ESITO_PUNTEGGIO";
    public final static String PUNTO = "PUNTO";
    public final static String FINE_PARTITA = "FINE_PARTITA";

    private final int MASSIMO_TEMPO_VITA = 10000;     //Tampo massimo entro il quale un giocatore può essere giudicato vivo

    private String emailGiocatore1;             //Email del giocatore1
    private String emailGiocatore2;             //Email del giocatore2
    private String ipGiocatore1;                //Ip del giocatore 1
    private String ipGiocatore2;                //Ip del giocatore 2
    private int portaGiocatore1;                //Porta del giocatore 1
    private int portaGiocatore2;                //Porta del giocatore 2
    private long lastTimestampGiocatore1;       //Ultimo riferimento temporale di connessione del giocatore1
    private long lastTimestampGiocatore2;       //Ultimo riferimento temporale di connessione del giocatore2;

    private Thread threadLoop;                  //Thread del loop del server
    private boolean running;                    //Flag di running del loop del server
    private DatagramSocket socket;              //Socket di connessione

    //Elementi di gioco
    private Vector2D posizionePalla;            //Posizione della palla nel server
    private Vector2D direzionePalla;            //Direzione della palla nel server
    private int puntiGiocatore1;                //Punti del giocatore 1
    private int puntiGiocatore2;                //Punti del giocatore 2

    private LoopTimer timerIsAlive;             //Timer per l'invio del segnale di isAlive

    public ServerMultiplayer(String emailGiocatore1, String emailGiocatore2, int porta){
        System.out.println("Avvio un server multiplayer sulla porta: " + porta);
        DF df = (DF)GA.container.findAgenteByName("DF");

        this.emailGiocatore1 = emailGiocatore1;
        this.emailGiocatore2 = emailGiocatore2;
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
                socket.setSoTimeout(1500);
                this.running = true;
                this.threadLoop = new Thread(this);
                this.threadLoop.start();
                this.timerIsAlive = new LoopTimer(this, 2000);
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Esegue il setup delle componenti del server
     */
    private void setup(){
        int schermoAvviabile = this.SCREEN_HEIGHT - this.ZONA_PUNTEGGIO;
        this.posizionePalla = new Vector2D(this.SCREEN_WIDTH * 0.5f, this.ZONA_PUNTEGGIO + schermoAvviabile * 0.5f);
        this.direzionePalla = new Vector2D(1, 0).ruotaVettore((int)(Math.random() * 360));
        this.puntiGiocatore1 = 0;
        this.puntiGiocatore2 = 0;

        //Creiamo il thread per non bloccare l'esecuzione
        new Thread(){
            @Override
            public void run() {
                try{
                    Thread.sleep(5000);//Attesa di connessione
                    inviaMessaggioClient("Start in 3", 1000);
                    Thread.sleep(1000);
                    inviaMessaggioClient("Start in 2", 1000);
                    Thread.sleep(1000);
                    inviaMessaggioClient("Start in 1", 1000);
                    Thread.sleep(1000);
                    inviaMessaggioClient("GO!", 5000);
                    inviaStart();
                    inviaStartOwner(ipGiocatore1, portaGiocatore1);
                    inviaEsitoPunteggio();
                }catch (Exception e){e.printStackTrace();}
            }
        }.start();
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
            this.socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1));
            this.socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2));
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void run() {
        System.out.println("Avviato thread interno del server");
        this.setup();
        while(this.running){
            byte[] buffer = new byte[60000];
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length);

            try{
                this.socket.receive(pacchetto);
                this.controlloIsAlive(pacchetto);
                this.richiestaPosizionePalla(pacchetto);
                this.aggiornaPosizioneDirezionePalla(pacchetto);
                this.aggiornamentoTargetXGiocatore(pacchetto);
                this.riceviPunto(pacchetto);

            }catch (SocketTimeoutException e){}
            catch (Exception e){e.printStackTrace();}

            //Controlliamo la vita dei giocatori e nel caso uno dei due si sconnette, chiudiamo il server
            if(!this.isGiocatore1Vivo() || !this.isGiocatore2Vivo())
                this.running = false;
        }
        System.out.println("Terminato thread interno del server");
        this.timerIsAlive.stop();
        this.socket.close();
    }

    //Richieste del client e funzioni del server

    /**
     * Invia un messaggio ai client
     * @param messaggio Messaggio da inviare ai client
     * @param attesa Attesa del messaggio
     */
    private void inviaMessaggioClient(String messaggio, int attesa){
        String dati  = ServerMultiplayer.INVIO_MESSAGGIO_SERVER + "=" + messaggio + ":" + attesa;
        byte[] buffer = dati.getBytes();

        try{
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
            this.socket.send(pacchetto);
            pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Controlla se il messaggio contiene una richiesta della posizione della palla
     * @param messaggio Messaggio ricevuto dal client
     */
    private void richiestaPosizionePalla(DatagramPacket messaggio){
        String dati = new String(messaggio.getData()).substring(0, messaggio.getLength()).trim();
        if(dati.equals(ServerMultiplayer.RICHIESTA_POSIZIONE_DIREZIONE_PALLA)){
            this.inviaPosizioneDirezionePalla(messaggio.getAddress().getHostAddress(), messaggio.getPort());
        }
    }

    /**
     * Invia il segnale di start ai client
     */
    private void inviaStart(){
        String dati  = ServerMultiplayer.INVIO_START;
        byte[] buffer = dati.getBytes();

        try{
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
            this.socket.send(pacchetto);
            pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Invia il segnale di start ai client
     */
    private void inviaStop(){
        String dati  = ServerMultiplayer.INVIO_STOP;
        byte[] buffer = dati.getBytes();

        try{
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
            this.socket.send(pacchetto);
            pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Invia il segnale di start owner ad un client
     * @param ipOwner Ip del client
     * @param portaOwner porta del client
     */
    private void inviaStartOwner(String ipOwner, int portaOwner){
        byte[] bufferStartOwner = ServerMultiplayer.START_OWNER.getBytes();
        try{
            this.socket.send(new DatagramPacket(bufferStartOwner, bufferStartOwner.length, InetAddress.getByName(ipOwner), portaOwner));
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Invia l'esito del punteggio
     */
    private void inviaEsitoPunteggio(){
        String messaggio = ServerMultiplayer.ESITO_PUNTEGGIO + "=" + this.puntiGiocatore1 + " - " + this.puntiGiocatore2;
        byte[] buffer = messaggio.getBytes();
        try{
            DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
            this.socket.send(pacchetto);
            pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2);
            this.socket.send(pacchetto);
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Qualcuno fa un punto
     * @param messaggio
     */
    private void riceviPunto(DatagramPacket messaggio){
        String dati = new String(messaggio.getData()).substring(0, messaggio.getLength()).trim();
        if(dati.startsWith(ServerMultiplayer.PUNTO)){
            this.inviaStop();
            boolean esito = Boolean.valueOf(dati.split("=")[1]);
            if(esito){
                this.puntiGiocatore1 ++;
            }else {
                this.portaGiocatore2++;
            }
            this.inviaMessaggioClient("POINT", 2000);
            try{
                this.posizionePalla.setPosY(this.SCREEN_HEIGHT / 2);
                this.inviaPosizioneDirezionePalla(this.ipGiocatore1, this.portaGiocatore1);
                this.inviaPosizioneDirezionePalla(this.ipGiocatore2, this.portaGiocatore2);
                Thread.sleep(2000);
                if(this.puntiGiocatore1 == 3 || this.puntiGiocatore2 == 3){
                    this.inviaMessaggioClient("THE END", 1000);
                    Thread.sleep(1000);
                    byte[] buffer = ServerMultiplayer.FINE_PARTITA.getBytes();
                    DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
                    this.socket.send(pacchetto);
                    pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore1);
                    this.socket.send(pacchetto);
                    this.running = false;
                }else
                    this.inviaStart();
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Aggiorna il targetX del paddle di un giocatore
     * @param messaggio Messaggio raggiunto
     */
    private void aggiornamentoTargetXGiocatore(DatagramPacket messaggio){
        String dati = new String(messaggio.getData()).substring(0, messaggio.getLength()).trim();
        if(dati.startsWith(ServerMultiplayer.AGGIORNA_TARGET_X_PADDLE)){
            float targetX = Float.valueOf(dati.split("=")[1]);
            targetX = this.SCREEN_WIDTH - targetX;
            String risposta = ServerMultiplayer.TARGET_X_AVVERSARIO + "=" + targetX;
            byte[] buffer = risposta.getBytes();
            try{
                if(messaggio.getAddress().getHostAddress().equals(this.ipGiocatore1)) {
                    DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore2), this.portaGiocatore2);
                    this.socket.send(pacchetto);
                }else {
                    DatagramPacket pacchetto = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(this.ipGiocatore1), this.portaGiocatore1);
                    this.socket.send(pacchetto);
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Aggiorna la posizione del server della palla e la sua direzione
     * @param messaggio Messaggio raggiunto
     */
    private void aggiornaPosizioneDirezionePalla(DatagramPacket messaggio){
        String dati = new String(messaggio.getData()).substring(0, messaggio.getLength()).trim();
        if(dati.startsWith(ServerMultiplayer.AGGIORNA_POSIZIONE_DIREZIONE_PALLA)){
            String[] parti = dati.split("=")[1].split(":");
            int schermoAvviabile = this.SCREEN_HEIGHT - this.ZONA_PUNTEGGIO;
            float posX = Float.valueOf(parti[0]);
            float posY = Float.valueOf(parti[1]);
            float dirX = Float.valueOf(parti[2]);
            float dirY = Float.valueOf(parti[3]);
            if(!messaggio.getAddress().getHostAddress().equals(this.ipGiocatore1)){
                //Se il mittente è il giocatore 1 allora facciamo gli aggiustamenti della posizione
                posY = this.ZONA_PUNTEGGIO + schermoAvviabile - (posY - this.ZONA_PUNTEGGIO);
                posX = this.SCREEN_WIDTH - posX;
                Vector2D vettoreDirezione = new Vector2D(dirX, dirY).ruotaVettore(180);
                dirX = vettoreDirezione.getPosX();
                dirY = vettoreDirezione.getPosY();
            }
            this.posizionePalla = new Vector2D(posX, posY);
            this.direzionePalla = new Vector2D(dirX, dirY);
            if(messaggio.getAddress().getHostAddress().equals(this.ipGiocatore1))
                this.inviaPosizioneDirezionePalla(this.ipGiocatore2, this.portaGiocatore2);
            else
                this.inviaPosizioneDirezionePalla(this.ipGiocatore1, this.portaGiocatore1);
        }
    }

    /**
     * Invia la posizione e la direzione della palla al destinatario
     * @param ipDestinatario Indirizzo ip del destinatario
     * @param portaDestinatario Porta del destinatario
     */
    private void inviaPosizioneDirezionePalla(String ipDestinatario, int portaDestinatario){
        int schermoAvviabile = this.SCREEN_HEIGHT - this.ZONA_PUNTEGGIO;
        float posX = this.posizionePalla.getPosX();
        float posY = this.posizionePalla.getPosY();
        float dirX = this.direzionePalla.getPosX();
        float dirY = this.direzionePalla.getPosY();
        if(!ipDestinatario.equals(this.ipGiocatore1)){
            //Se il destinatario è il giocatore 1 facciamo le operazioni di inversione
            posY = this.ZONA_PUNTEGGIO + schermoAvviabile - (posY - this.ZONA_PUNTEGGIO);
            posX = this.SCREEN_WIDTH - posX;
            Vector2D vettoreDirezione = this.direzionePalla.ruotaVettore(180);
            dirX = vettoreDirezione.getPosX();
            dirY = vettoreDirezione.getPosY();
        }
        String dati = ServerMultiplayer.RISPOSTA_POSIZIONE_DIREZIONE_PALLA + "=" + posX + ":" + posY + ":" + dirX + ":" + dirY;
        byte[] buffer = dati.getBytes();
        try{
            DatagramPacket risposta = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ipDestinatario), portaDestinatario);
            this.socket.send(risposta);
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void timeIsZero() {
        if(this.running)
            this.invioMessaggioIsAlive();
    }
}