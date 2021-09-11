package com.example.android.arkanoid.AgentSystem;

import java.util.Iterator;
import java.util.LinkedHashSet;

public class Container {
    private LinkedHashSet<Agente> agentiLocali;     //Agenti locali creati

    public Container(){
        this.agentiLocali = new LinkedHashSet<>();
    }

    /**
     * Aggiunge un agente al container
     * @param agente Agente da aggiungere al container
     * @return Restituisce true se l'agente viene aggiuno con successo, altrimenti restituisce false
     */
    public synchronized boolean addAgente(Agente agente){
        return this.agentiLocali.add(agente);
    }

    /**
     * Rimuove l'agente dal container degli agenti
     * @param agente Agente da rimuovere
     * @return Restituisce l'esito della rimozione
     */
    public synchronized boolean removeAgente(Agente agente){
        boolean esito = false;
        if(this.agentiLocali.contains(agente))
            esito = this.agentiLocali.remove(agente);
        return esito;
    }

    /**
     * Restituisce l'agente partendo dal nome
     * @param nome Nome dell'agente da prelevare
     * @return Restituisce l'agente se trovato, altrimenti restituisce null
     */
    public Agente findAgenteByName(String nome){
        Agente esito = null;
        for(Iterator<Agente> it = this.agentiLocali.iterator(); it.hasNext() && esito == null;){
            Agente controllo = it.next();
            if(controllo.getNomeAgente().equals(nome))
                esito = controllo;
        }
        return esito;
    }
}
