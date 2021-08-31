package com.example.android.arkanoid.GameElements.ElementiBase;

public class GameStatus {
    public static final int TOUCH = 0;
    public static final int GYRO = 1;

    protected int maxHealth;
    protected int health;
    protected int punteggio;
    protected int modalitaControllo;

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
