package com.example.android.arkanoid.GameElements.ElementiBase;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.Objects;

public class Entity {
    private static int counterId = 0;                   //Contatore degli id dell'entity

    protected int id;                                   //Id dell'entity
    protected String name;                              //Nome dell'enetità
    protected final Sprite sprite;                      //Sprite dell'entità
    protected Vector2D position;                        //Posizione
    protected Vector2D direction;                       //Direzione
    protected Vector2D size;                            //Dimensione
    protected Vector2D speed;                           //Velocità

    protected float rotazione;                          //Rotazione dell'entità
    protected boolean isVisible;                        //Se true l'entità sarà visibile

    public Entity(String name, Vector2D position, Vector2D direction, Vector2D size, Vector2D speed, Sprite sprite){
        this.id = Entity.counterId ++;
        this.sprite = sprite;
        this.setName(name);
        this.setPosition(position);
        this.setDirection(direction);
        this.setSize(size);
        this.setSpeed(speed);

        this.isVisible = true;
        this.rotazione = 0;
    }

    /**
     * Inizializza lo stato dell'entità
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param params Parametri aggiuntivi passati all'entità
     */
    public void setup(int screenWidth, int screenHeight, ParamList params){}

    /**
     * Avvia la logica dell'entità
     * @param dt Tempo trascorso tra il disegno di un frame e un altro
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param params Parametri aggiuntivi necessari all'entità
     */
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params){}

    /**
     * Disegna lo sprite dell'entità sulla canvas
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    public void drawEntity(Canvas canvas, Paint paint){
        if(this.sprite != null && this.sprite.isAviable() && this.isVisible ){
            //Se lo sprite esiste ed è visibile
            this.sprite.drawSprite(
                    (int)this.position.getPosX(),
                    (int)this.position.getPosY(),
                    canvas,
                    paint
            );
        }
    }

    /**
     * Restituisce il prossimo passo dell'entità
     * @param dt Deltatime
     * @return Restituisce il prossimo passo dell'entità
     */
    public Vector2D getNextStep(float dt){
        return Vector2D.sommaVettoriale(this.position, this.direction.prodottoPerVettore(this.speed.prodottoPerScalare(dt)));
    }

    /**
     * Restituisce la collisionBox dell'entità
     * @return Restituisce un rect di collisione
     */
    public Rect getBounds(){
       Rect esito = new Rect();

       if(this.position != null)
           esito = this.getBounds(this.position.getPosX(), this.position.getPosY());

       return esito;
    }

    /**
     * Restituisce la collisionBox dell'entità calcolata partendo da una posizione diversa
     * @param startX Posizione di calcolo X
     * @param startY Posizione di calcolo Y
     * @return Restituisce un rect di collisione
     */
    public Rect getBounds(float startX, float startY){
        Rect esito = new Rect();

        if(this.size != null){
            esito = new Rect(
                    (int)( startX - (this.size.getPosX() * 0.5) ),
                    (int)( startY - (this.size.getPosY() * 0.5) ),
                    (int)( startX + (this.size.getPosX() * 0.5) ),
                    (int)( startY + (this.size.getPosY() * 0.5) )
            );
        }

        return esito;
    }

    //Beam
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sprite getSprite() {
        return sprite;
    }

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

    public Vector2D getSize() {
        return size;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setSize(Vector2D size) {
        this.size = size;
        if(this.sprite != null){
            this.sprite.resizeImage(size);
        }
    }

    public Vector2D getSpeed() {
        return speed;
    }

    public void setSpeed(Vector2D speed) {
        this.speed = speed;
    }

    public float getRotazione() {
        return rotazione;
    }

    public void setRotazione(float rotazione) {
        this.rotazione = rotazione;
        if(this.sprite != null)
            this.sprite.setRotazione(rotazione);
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    //Altro

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity that = (Entity) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}