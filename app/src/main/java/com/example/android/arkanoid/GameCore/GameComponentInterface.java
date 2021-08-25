package com.example.android.arkanoid.GameCore;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface GameComponentInterface {
    /**
     * Metodo chiamato ogni volta che il possoessore del gameComponent cambia la risoluzione
     * @param newScreenWidth Nuova larghezza dello schermo
     * @param newScreenHeight Nuova altezza dello schermo
     */
    public void ownerSizeChange(int newScreenWidth, int newScreenHeight);

    /**
     * Effettua il setup delle informazioni del gameComponent
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     */
    public void setup(int screenWidth, int screenHeight);

    /**
     * Aggiorna lo stato del gameComponent
     * @param dt Tempo trascorso tra il disegno di un frame ed un'altro
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     * @param canvas Canvas di disegno
     * @param paint Paint per il disegno
     */
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint);

    /**
     * Disegna il gameComponent
     * @param dt Tempo trascorso tra il disegno di un frame ed un'altro
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     * @param canvas Canvas di disegno
     * @param paint Paint per il disegno
     */
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint);

    /**
     * Finalizza le operazioni del gameComponent
     * @param dt Tempo trascorso tra il disegno di un frame ed un'altro
     * @param screenWidth Larghezza della zona di disegno
     * @param screenHeight Altezza della zona di disegno
     * @param canvas Canvas di disegno
     * @param paint Paint per il disegno
     */
    public void finalStep(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint);
}
