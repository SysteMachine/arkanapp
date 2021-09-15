package com.example.android.arkanoid.Multiplayer;

public class MessaggioServerRecord {
    private String messaggio;
    private int durata;
    private long startTime;

    public MessaggioServerRecord(String messaggio, int durata){
        this.messaggio = messaggio;
        this.durata = durata;
        this.startTime = System.currentTimeMillis();
    }

    /**
     * Restituisce la validità del messaggio
     * @return Restituisce true se il messaggio è valido, altrimenti restituisce false
     */
    public boolean isValid(){
        return System.currentTimeMillis() - this.startTime < this.durata;
    }

    public String getMessaggio() {
        return messaggio;
    }
}
