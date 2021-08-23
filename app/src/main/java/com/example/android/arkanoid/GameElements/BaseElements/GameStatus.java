package com.example.android.arkanoid.GameElements.BaseElements;

public class GameStatus {
    protected int health;
    protected int punteggio;

    public GameStatus(int health, int punteggio) {
        this.health = health;
        this.punteggio = punteggio;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getPunteggio() {
        return punteggio;
    }

    public void setPunteggio(int punteggio) {
        this.punteggio = punteggio;
    }
}
