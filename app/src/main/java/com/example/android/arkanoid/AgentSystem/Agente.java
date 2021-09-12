package com.example.android.arkanoid.AgentSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class Agente implements Runnable{
    private final int MS_DELAY = 200;                //Tempo di attesa tra l'esecuzione dei compiti dell'agente

    private static int idCounter;

    private final int idAgente;                             //Id dell'agente
    private final String nomeAgente;                        //Nome dell'agente
    private boolean agenteVivo;                             //Flag di vita dell'agente, se true l'agente è vivo ed esegue il thread
    private Thread threadAgente;                            //Thread di esecuzione dei compiti

    private final ArrayList<Compito> compitiAgente;        //Compiti inseriti all'interno dell'agente
    private final ArrayList<MessageBox> bufferMessaggi;    //Buffer dei messaggi in ingresso

    public Agente(String nomeAgente){
        this.idAgente = Agente.idCounter++;
        this.nomeAgente = nomeAgente;

        this.compitiAgente = new ArrayList<>();
        this.bufferMessaggi = new ArrayList<>();
        this.setupCore();
    }

    /**
     * Aggiunge un compito all'agente
     * @param compito Compito da aggiungere all'agente
     * @return Restituisce l'esito dell'inserimento
     */
    public synchronized boolean addCompito(Compito compito){
        compito.setAgent(this);
        return this.compitiAgente.add(compito);
    }

    /**
     * Rimuove il compito dall'agente
     * @param compito Compito da rimuovere dall'agente
     */
    public synchronized void removeCompito(Compito compito){
        compito.removeAgent();
        this.compitiAgente.remove(compito);
    }

    /**
     * Termina l'esecuzione dell'agente
     */
    public void doDelete(){
        this.takedownCore();
    }

    /**
     * Esegue tutte le operazioni di setup
     */
    private final void setupCore(){
        this.setup();
        this.agenteVivo = true;
        this.threadAgente = new Thread(this);
        this.threadAgente.start();
    }

    /**
     * Operazioni di setup Aggiuntive
     */
    protected void setup(){}

    /**
     * Esegue tutte le operazioni di takedown
     */
    private final void takedownCore(){
        this.agenteVivo = false;
        try{
            this.threadAgente.join();
        }catch (Exception e){e.printStackTrace();}
        this.takedonw();
    }

    /**
     * Operazioni di takedown aggiuntive
     */
    protected void takedonw(){}

    /**
     * Invia il messaggio
     * @param messaggio Messaggio da inviare
     */
    public void inviaMessaggio(MessageBox messaggio){
        if(messaggio.getTo().equals(MessageBox.BROADCAST_MESSAGE))
            GA.channel.inviaBroadcast(messaggio);
        else
            GA.channel.inviaMessaggio(messaggio);
    }

    /**
     * Riceve e gestisce un messaggio
     * @param messaggio Messaggio da gestire
     */
    public void riceviMessaggio(MessageBox messaggio){
        this.bufferMessaggi.add(messaggio);
    }

    /**
     * Preleva il prossimo messaggio nella coda
     * @return Restituisce un messaggio o null nel caso non ci siano messaggi in sospeso
     */
    public MessageBox prelevaMessaggio(){
        MessageBox esito = null;

        if(this.bufferMessaggi.size() > 0)
            esito = this.bufferMessaggi.remove(0);

        return esito;
    }


    //Beam
    public int getIdAgente() {
        return idAgente;
    }

    public String getNomeAgente() {
        return nomeAgente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agente agente = (Agente) o;
        return idAgente == agente.idAgente &&
                nomeAgente.equals(agente.nomeAgente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAgente, nomeAgente);
    }

    @Override
    public void run() {
        System.out.println("Avviato l'agente: " + this.nomeAgente);
        while(this.agenteVivo){
            for(Iterator<Compito> it = this.compitiAgente.iterator(); it.hasNext();) {
                Compito compito = it.next();
                compito.action();
                if(compito.done())
                    it.remove();
            }

            try{
                Thread.sleep(this.MS_DELAY);
            }catch (Exception e){e.printStackTrace();}
        }
    }
}