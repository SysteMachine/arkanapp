package com.example.android.arkanoid.GameCore;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Objects;

public abstract class AbstractGameComponent implements GameComponentInterface{
    private static int idCounter = 0;                           //Contatore degli id per l'identificazione degli elementi

    protected int componentId;                                  //Identificativo dell'id
    protected int zIndex;                                       //Indice di profondità nella scena dell'elemento

    protected GameLoop owner;                                   //GameLoop che si occupa di renderizzare la componente

    public AbstractGameComponent(int zIndex){
        this.componentId = AbstractGameComponent.idCounter++;
        this.zIndex = zIndex;
    }

    /**
     * Imposta il gameLoop della componente
     * @param gameLoop Game loop proprietario della componente
     */
    void setGameLoop(GameLoop gameLoop){
        if(gameLoop != null)
            this.owner = gameLoop;
    }

    /**
     * Rimuove il gameLoop della componente
     */
    void removeGameLoop(){
        this.owner = null;
    }

    @Override
    public abstract void setup(int screenWidth, int screenHeight);

    @Override
    public abstract void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint);

    @Override
    public abstract void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint);

    //Beam
    public int getzIndex() {
        return zIndex;
    }

    public void setzIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int getComponentId() {
        return componentId;
    }

    //Altro
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractGameComponent that = (AbstractGameComponent) o;
        return componentId == that.componentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentId);
    }
}
