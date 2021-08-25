package com.example.android.arkanoid.GameElements.ElementiBase;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameCore.AbstractGameComponent;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class AbstractScene extends AbstractGameComponent {
    //Identificatori dei parametri
    public static String PARAMETRO_ENTITA = "Entita";
    public static String PARAMETRO_SCENA = "Scena";
    //----------------------------

    protected int lastScreenWidth;
    protected int lastScreenHeight;

    protected LinkedList<Entity> entita;

    public AbstractScene(int zIndex) {
        super(zIndex);
        this.entita = new LinkedList<>();
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        //Inizializza le dimensioni dello schermo
        this.lastScreenWidth = gameLoop.getCanvasWidht();
        this.lastScreenHeight = gameLoop.getCanvasHeight();
        super.setGameLoop(gameLoop);
    }

    /**
     * Restituisce le entità che corrispondono al nome inserito
     * @param name Nome dell'entità da trovare
     * @return Restituisce la lista delle entità che rispettano il nome inserito
     */
    public Entity[] getEntityByName(String name){
        ArrayList<Entity> trovati = new ArrayList<>();
        for(Entity ae : this.entita){
            if(ae.getName() != null && ae.getName().equals(name)){
                trovati.add(ae);
            }
        }

        return trovati.toArray(new Entity[trovati.size()]);
    }

    /**
     * Restituisce la prima entità che corrisponde al nome inserito
     * @param name Nome dell'entità da trovare
     * @return Restituisce la lista delle entità che rispettano il nome inserito
     */
    public <T extends Entity> T getFirstEntityByName(String name){
        T esito = null;

        Entity[] ricerca = this.getEntityByName(name);
        if(ricerca.length >= 1)
            esito = (T)ricerca[0];

        return esito;
    }

    /**
     * Aggiunge un'entità all'interno della scena
     * @param entita Entità da inserire all'interno della scena
     * @return Restituisce l'esito dell'inserimento
     */
    public boolean addEntita(Entity entita){
        boolean esito = false;
        if(!this.entita.contains(entita)){
            esito = this.entita.add(entita);
        }
        return esito;
    }

    /**
     * Rimuove un entità dalla scena
     * @param entita Entità da rimovere dalla scena
     */
    public void removeEntita(Entity entita){
        this.entita.remove(entita);
    }

    /**
     * Rimuove tutte le entità dalla scena
     */
    public void clearEntita(){
        this.entita.clear();
    }

    /**
     * Crea i parametri che devono essere passati alle entità nel momento di eseguire la logice
     * @return Restituisce un paramList di elementi che devono essere usati dalle entità
     */
    protected ParamList creaParametriEntita(){
        ParamList parametri = new ParamList();

        parametri.add(AbstractScene.PARAMETRO_ENTITA, this.entita.toArray(new Entity[this.entita.size()]));
        parametri.add(AbstractScene.PARAMETRO_SCENA, this);

        return parametri;
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        ParamList parametri = this.creaParametriEntita();
        for(Entity ae : this.entita)
            ae.setup(screenWidth, screenHeight, parametri);
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        ParamList parametri = this.creaParametriEntita();
        for(Entity ae : this.entita)
            ae.logica(dt, screenWidth, screenHeight, parametri);
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        for(Entity ae : this.entita){
            ae.drawEntity(canvas, paint);
        }
    }

    @Override
    public void ownerSizeChange(int newScreenWidth, int newScreenHeight) {
        float pesoWidht = (float)newScreenWidth / (float)this.lastScreenWidth;
        float pesoHeight = (float)newScreenHeight / (float)this.lastScreenHeight;
        Vector2D scaleVector = new Vector2D(pesoWidht, pesoHeight);
        for(Entity ae : this.entita){
            //Quando c'è un ridimensionamento dello schermo cambia il dimensionamento delle entita in posizione, dimensione e velocita
            ae.setPosition(ae.getPosition().prodottoPerVettore(scaleVector));
            ae.setSize(ae.getSize().prodottoPerVettore(scaleVector));
            ae.setSpeed(ae.getSpeed().prodottoPerVettore(scaleVector));
        }

        this.lastScreenWidth = newScreenWidth;
        this.lastScreenHeight = newScreenHeight;
    }

    /**
     * Permette alle entità di inviare un evento alla scena
     * @param idEvent Identificatore testuale dell'evento
     * @param parametri Parametri dell'evento
     */
    public abstract void sendEvent(String idEvent, ParamList parametri);
}
