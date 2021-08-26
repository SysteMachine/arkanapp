package com.example.android.arkanoid.GameElements.ElementiBase;

public class GameStatus {
    protected int maxHealth;
    protected int health;
    protected int punteggio;

    public GameStatus(int health, int punteggio) {
        this.maxHealth = health;
        this.health = health;
        this.punteggio = punteggio;
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
}
