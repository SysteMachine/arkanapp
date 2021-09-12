package com.example.android.arkanoid.AgentSystem;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class Channel implements Runnable{
    private final int MIN_PORTA = 44550;            //Minimo della porta consentito
    private final int MAX_PORTA = 50000;            //Massimo della porta consentito

    private int porta;
    private DatagramSocket socket;
    private Thread threadRecezione;

    public Channel(){
        this.generaPorta();
        try{
            this.socket = new DatagramSocket(porta);
            this.socket.setSoTimeout(1500);
            this.threadRecezione = new Thread(this);
            threadRecezione.start();
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Invia un messaggio al destinatario
     * @param messaggio Messaggio da inviare
     * @return Restituisce l'esito dell'invio
     */
    public boolean inviaMessaggio(MessageBox messaggio){
        boolean esito = false;

        System.out.println("Invio: " + messaggio.getStringaMessaggio());

        DF df =  (DF)GA.container.findAgenteByName("DF");
        RecordClient rc = df.getClient(messaggio.getTo());

        String stringaMessaggio = messaggio.getStringaMessaggio();
        String ipMessaggio = rc.getIp();
        int portaMessaggio = rc.getPorta();

        byte[] bufferMessaggio = stringaMessaggio.getBytes();  //Preleva il buffer dei byte per il messaggio
        try{
            InetAddress address = Inet4Address.getByName(ipMessaggio);
            DatagramPacket pacchetto = new DatagramPacket(bufferMessaggio, bufferMessaggio.length, address, portaMessaggio);
            if(this.socket != null) {
                this.socket.send(pacchetto);
                esito = true;
            }
        }catch (Exception e){e.printStackTrace();}

        return esito;
    }

    /**
     * Invia un messaggio in broadcast ai giocatori collegati
     * @param messaggio Messaggio da inviare
     * @return Restituisce l'esito dell'invio
     */
    public boolean inviaBroadcast(MessageBox messaggio){
        boolean esito = true;

        DF df =  (DF)GA.container.findAgenteByName("DF");

        for(RecordClient rc : df.getListaClient()){
            MessageBox nuovoMessaggio = new MessageBox(messaggio.getStringaMessaggio());
            nuovoMessaggio.setTo(rc.getEmail());

            if(!nuovoMessaggio.getFrom().equals(nuovoMessaggio.getTo()) || !nuovoMessaggio.getFromAgentName().equals(nuovoMessaggio.getToAgentName())){
                //Se il ricevitore è lo stesso che ha inviato il messaggio non lo invia
                esito = esito && this.inviaMessaggio(nuovoMessaggio);
            }
        }

        return esito;
    }

    /**
     * Genera la porta di connessione del canale
     */
    private void generaPorta(){
        this.porta = (int)(this.MIN_PORTA + (Math.random() * (this.MAX_PORTA - this.MIN_PORTA)));
    }

    //Beam

    public int getPorta() {
        return porta;
    }

    /**
     * Instrada il messaggio all'agente
     * @param messaggio Messaggio da instradare all'agente
     */
    private void instradaMessaggio(MessageBox messaggio){
        if(!messaggio.getMessageType().equals(MessageBox.TYPE_IM_ALIVE)) {
            System.out.println("Messaggio: " + messaggio.getContent());
            Agente agente = GA.container.findAgenteByName(messaggio.getToAgentName());
            if (agente != null)
                agente.riceviMessaggio(messaggio);
        }
    }

    @Override
    public void run() {
        System.out.println("Avviato thread di recezione del canale");
        while(true){
            if(this.socket != null){
                byte[] bufferRecezione = new byte[60000];           //Buffer di recezione
                DatagramPacket recezione = new DatagramPacket(bufferRecezione, bufferRecezione.length);
                try{
                    this.socket.receive(recezione);
                    if(recezione.getLength() != 0){
                        String messaggio = new String(recezione.getData());
                        messaggio = messaggio.substring(0, recezione.getLength());
                        MessageBox messageBox = new MessageBox(messaggio);

                        this.instradaMessaggio(messageBox);
                    }

                }catch (SocketTimeoutException e){}
                catch (Exception e){e.printStackTrace();}
            }
        }
    }
}
