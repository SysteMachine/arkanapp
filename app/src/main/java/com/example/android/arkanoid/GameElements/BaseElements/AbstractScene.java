package com.example.android.arkanoid.GameElements.BaseElements;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameCore.AbstractGameComponent;
import com.example.android.arkanoid.Util.ParamList;

import java.util.ArrayList;
import java.util.LinkedList;

public abstract class AbstractScene extends AbstractGameComponent {
    protected LinkedList<AbstractEntity> entita;

    public AbstractScene(int zIndex) {
        super(zIndex);
        this.entita = new LinkedList<AbstractEntity>();
    }

    /**
     * Aggiunge un'entità all'interno della scena
     * @param entita Entità da inserire all'interno della scena
     * @return Restituisce l'esito dell'inserimento
     */
    protected boolean addEntita(AbstractEntity entita){
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
    protected void removeEntita(AbstractEntity entita){
        this.entita.remove(entita);
    }

    /**
     * Rimuove tutte le entità dalla scena
     */
    protected void clearEntita(){
        this.entita.clear();
    }

    /**
     * Restituisce le entità che corrispondono al nome inserito
     * @param name Nome dell'entità da trovare
     * @return Restituisce la lista delle entità che rispettano il nome inserito
     */
    protected AbstractEntity[] getEntityByName(String name){
        ArrayList<AbstractEntity> trovati = new ArrayList<AbstractEntity>();
        for(AbstractEntity ae : this.entita){
            if(ae.getName().equals(name)){
                trovati.add(ae);
            }
        }

        return trovati.toArray(new AbstractEntity[trovati.size()]);
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        for(AbstractEntity ae : this.entita)
            ae.setup(screenWidth, screenHeight, new ParamList());
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        for(AbstractEntity ae : this.entita)
            ae.logica(dt, screenWidth, screenHeight, new ParamList());
    }

    @Override
    public abstract void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint);
}
