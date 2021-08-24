package com.example.android.arkanoid.GameElements.BaseElements;

import com.example.android.arkanoid.Util.SpriteUtil.Sprite;

public interface PowerUpMalusInterface {
    /**
     * Restituisce l'alterazione del powerup/malus
     * @return Restituisce un alterazione
     */
    public Alterazione getAlterazione();

    /**
     * Restituisce l'immagine del powerup/malus
     * @return Restituisce lo sprite del powerup
     */
    public Sprite getPowerUpMalusImage();
}
