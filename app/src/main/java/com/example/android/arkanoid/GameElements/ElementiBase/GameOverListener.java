package com.example.android.arkanoid.GameElements.ElementiBase;

public interface GameOverListener {
    /**
     * Evento chiamato quando una scena termina l'esecuzione per gameOver
     * @param status GameStatus GameStatus della scena
     */
    public void gameOver(GameStatus status);
}
