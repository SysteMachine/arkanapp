package com.example.android.arkanoid.GameElements.BaseElements;

import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.Objects;

public abstract class AbstractEntity {
    private static int counterId = 0;

    protected int id;
    protected String name;

    protected Vector2D position;
    protected Vector2D direction;
    protected float speed;

    public AbstractEntity(){
        this.id = AbstractEntity.counterId ++;
    }

    public AbstractEntity(String name){
        this();
        this.name = name;
    }

    /**
     * Inizializza lo stato dell'entità
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param params Parametri aggiuntivi passati all'entità
     */
    public void setup(int screenWidth, int screenHeight, ParamList params){
        //Il setup non è un metodo obbligatorio da implementare a differenza della logica di un'entità
    }

    /**
     * Avvia la logica dell'entità
     * @param dt Tempo trascorso tra il disegno di un frame e un altro
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param params Parametri aggiuntivi necessari all'entità
     */
    public abstract void logica(float dt, int screenWidth, int screenHeight, ParamList params);

    //Beam
    public Vector2D getPosition() {
        return position;
    }

    public void setPosition(Vector2D position) {
        this.position = position;
    }

    public Vector2D getDirection() {
        return direction;
    }

    public void setDirection(Vector2D direction) {
        this.direction = direction;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //Altro
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity that = (AbstractEntity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}