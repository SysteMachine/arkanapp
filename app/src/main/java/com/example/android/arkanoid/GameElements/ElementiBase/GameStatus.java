package com.example.android.arkanoid.GameElements.ElementiBase;

public class GameStatus {
    public static final int TOUCH = 0;
    public static final int GYRO = 1;

    protected int maxHealth;            //Vita massima del giocatore
    protected int health;               //Vita corrente del giocatore
    protected int punteggio;            //Punteggio del giocatore
    protected int modalitaControllo;    //Modalità di controllo del giocatore

    public GameStatus(int health, int punteggio, int modalitaControllo) {
        this.maxHealth = health;
        this.health = health;
        this.punteggio = punteggio;
        this.modalitaControllo = modalitaControllo;
    }

    /**
     * Incrementa il punteggio dello status
     * @param incremento Incremento da aggiungere
     */
    public void incrementaPunteggio(int incremento){
        this.punteggio += incremento;
    }

    /**
     * Incrementa la vita del giocatore
     * @param incremento Incremento della vita
     */
    public void incrementaVita(int incremento){
        if(this.health + incremento <= this.maxHealth)
            this.health += incremento;
        else
            this.health = this.maxHealth;
    }

    /**
     * Decrementa la vita del giocatore
     * @param decremento Decremento della vita
     */
    public void decrementaVita(int decremento){
        if(this.health - decremento >= 0)
            this.health -= decremento;
        else
            this.health = 0;
    }

    //Beam
    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }

    public int getModalitaControllo() { return modalitaControllo; }

    public void setModalitaControllo(int modalitaControllo) { this.modalitaControllo = modalitaControllo; }
}
