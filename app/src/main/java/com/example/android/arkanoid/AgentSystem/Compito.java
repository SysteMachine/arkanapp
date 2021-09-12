package com.example.android.arkanoid.AgentSystem;

public class Compito {
    private String nomeCompito;             //Nome del compito
    protected Agente myAgent;               //Agente ospitante

    public Compito(String nomeCompito){
        this.nomeCompito = nomeCompito;
        System.out.println("Creato un compito: " + this.nomeCompito);
    }

    /**
     * Associa l'agente al compito
     * @param agente Agente ospite del compito
     */
    protected void setAgent(Agente agente){
        this.myAgent = agente;
    }

    /**
     * Rimuove l'agente ospite
     */
    protected void removeAgent(){
        this.myAgent = null;
    }

    /**
     * Azione invocata dall'Agente
     */
    public void action(){};

    /**
     * Restituisce true se l'attività del Compito è conclusa
     * @return Restituisce l'esito della conclusione delle attivita
     */
    public boolean done(){return false;};
}
