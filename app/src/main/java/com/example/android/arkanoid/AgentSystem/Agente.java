package com.example.android.arkanoid.AgentSystem;

import java.util.ArrayList;
import java.util.Objects;

public class Agente implements Runnable{
    private static int idCounter;

    private int idAgente;
    private String nomeAgente;

    private boolean agenteVivo;
    private Thread threadAgente;

    private ArrayList<Compito> compitiAgente;

    private Agente(String nomeAgente){
        this.idAgente = Agente.idCounter++;
        this.nomeAgente = nomeAgente;

        this.compitiAgente = new ArrayList<>();
    }

    /**
     * Crea un nuovo agente
     * @param nomeAgente Nome dell'agente da creare
     * @return Restituisce l'esito della creazione, in caso di problemi restituisce null
     */
    public static Agente newAgente(String nomeAgente){
        Agente esito = null;

        if(!nomeAgente.equals("")) {
            esito = new Agente(nomeAgente);
            if(!GA.container.addAgente(esito))
                esito = null;
        }

        return esito;
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

    }
}