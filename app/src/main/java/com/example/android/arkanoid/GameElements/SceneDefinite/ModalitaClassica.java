package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiBase.PM;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.PMList;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.IndicatoreVita;
import com.example.android.arkanoid.GameElements.ElementiGioco.Map;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Particella;
import com.example.android.arkanoid.GameElements.ElementiGioco.Sfondo;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.ArrayList;
import java.util.Iterator;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener{
    public final static String EVENTO_BLOCCO_ROTTO = "rotturaBlocco";
    public final static String EVENTO_POWERUP = "powerup";
    public final static String EVENTO_RIMOZIONE_POWERUP = "rimozionePowerup";
    public final static String EVENTO_RIMOZIONE_PARTICELLA = "rimozioneParticella";

    public final static String PARAMETRO_ALTERAZIONE_STILE = "stile";
    public final static String PARAMETRO_ALTERAZIONE_GAMELOOP = "gameLoop";

    private final int OFFSET_SUPERIORE_PALLA = 80;
    private final int PARTICELLE_ROTTURA_BLOCCO = 10;

    //-----------------------------------------------------------------------------------------------------------//

    protected Ball palla;                                       //Palla di scena
    protected Paddle paddle;                                    //Paddle di scena
    protected Map mappa;                                        //Mappa della scena
    protected Sfondo sfondo;                                    //Sfondo della mappa
    protected IndicatoreVita[] indicatoriVita;                  //Indicatori della vita

    protected ArrayList<PM> powerUpAttivi;              //Lista dei powerup attivi

    protected float percentualeDimensionePowerup;               //Dimensione dei powerup sullo schermo
    protected float percentualeDimensioneIndicatori;            //Dimensione degli indicatori in alto

    protected Stile stile;                                      //Stile della modalita
    protected GameStatus status;                                //Status della modalita
    protected PMList pmList;                                    //Lista dei parametri della modalita
    protected boolean risorseCaricate;                          //Flag di caricamento delle risorse

    public ModalitaClassica(Stile stile, GameStatus status, PMList pmList) {
        super(0);

        this.stile = stile;
        this.status = status;
        this.pmList = pmList;

        this.risorseCaricate = false;

        this.powerUpAttivi = new ArrayList<>();

        this.percentualeDimensioneIndicatori = 0.06f;
        this.percentualeDimensionePowerup = 0.1f;
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        super.setGameLoop(gameLoop);
        gameLoop.setOnTouchListener(this);
    }

    @Override
    protected void removeGameLoop() {
        super.removeGameLoop();
        this.owner.setOnTouchListener(null);
    }

    /**
     * Crea i parametri da passare alle alterazioni
     * @return Restituisce i parametri dell'alterazione
     */
    protected ParamList creaParametriAlterazioni(){
        ParamList pl = this.creaParametriEntita();

        pl.add(ModalitaClassica.PARAMETRO_ALTERAZIONE_STILE, this.stile);
        pl.add(ModalitaClassica.PARAMETRO_ALTERAZIONE_GAMELOOP, this.owner);

        return pl;
    }

    /**
     * Logica di gestione delle alterazioni
     */
    protected void logicaAlterazioni(){
        if(this.risorseCaricate){
            float width = this.percentualeDimensioneIndicatori * this.lastScreenWidth;
            float startX = this.lastScreenWidth - (width * 0.5f);
            float startY = this.indicatoriVita[0].getPosition().getPosY();

            for(int i = 0; i < this.powerUpAttivi.size(); i++){
                PM pm = this.powerUpAttivi.get(i);
                AbstractAlterazione alterazione = pm.getAlterazione();
                if(alterazione != null)
                    alterazione.logica(this.status, this.creaParametriAlterazioni());
                //Posizioniamo gli indicatori
                Entity e = this.getFirstEntityByName(pm.getName() + "Indicatore" + pm.getId());
                if(e != null){
                    e.setPosition(new Vector2D(startX - (width * i), startY));
                }
            }

            //Rimuove i powerup consumati e il relativo indicatore
            for(Iterator<PM> it = this.powerUpAttivi.iterator(); it.hasNext();){
                PM next = it.next();
                if(!next.getAlterazione().isAlterazioneAttiva()){
                    it.remove();
                    this.removeEntita(this.getFirstEntityByName(next.getName() + "Indicatore" + next.getId()));
                }
            }
        }
    }

    /**
     * Inizializza le risorse della scena
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    protected void inizializzaRisorse(int screenWidth, int screenHeight){
        this.palla = new Ball(
                this.stile.getPercentualePosizionePalla().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePalla(), this.stile.getVelocitaInizialePalla()),
                this.stile.getImmaginePallaStile(this.owner),
                (int)(this.stile.getPercentualeRaggioPalla() * screenWidth),
                this.stile.getAngoloDiLancioMassimoPalla()
        );
        this.palla.setOffsetCollisioneSuperiore(this.OFFSET_SUPERIORE_PALLA);

        this.paddle = new Paddle(
                this.stile.getPercentualePosizionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePaddle(), this.stile.getVelocitaInizialePaddle()),
                this.stile.getImmaginePaddleStile(this.owner)
        );

        Sprite[] coloriBrick = new Sprite[this.stile.getColoriCasualiBrick().length];
        for(int i = 0; i < coloriBrick.length; i++){
            coloriBrick[i] = this.stile.getImmagineBrickStile(this.owner, i);
        }

        this.mappa = new Map(
                this.stile.getNumeroRigheMappa(),
                this.stile.getNumeroColonneMappa(),
                this.stile.getPercentualePosizioneMappa().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getPercentualeDimensioneMappa().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                coloriBrick,
                new MultiSprite(R.drawable.crepebrick, this.owner, 10)
        );

        this.sfondo = new Sfondo(
                new Vector2D(screenWidth * 0.5f, screenHeight * 0.5f),
                new Vector2D(screenWidth, screenHeight),
                this.stile.getImmagineSfondoStile(this.owner)
        );

        this.indicatoriVita = new IndicatoreVita[this.status.getHealth()];
        int width = (int)(this.percentualeDimensioneIndicatori * screenWidth);
        int startPosX = (int)( width * 0.5f );
        int startPosY = (int)( this.OFFSET_SUPERIORE_PALLA * 0.5f );
        for(int i = 0; i < this.indicatoriVita.length; i++){
            this.indicatoriVita[i] = new IndicatoreVita(
                    new Vector2D(startPosX, startPosY),
                    new Vector2D(width, width),
                    new AnimatedSprite(R.drawable.indicatori_vita, this.owner, 4, (int)( 5 + Math.random() * 5 ))
            );
            startPosX += width;
        }
    }

    /**
     * Aggiunge le entità alla scena
     */
    protected void addEntitaScena(){
        this.entita.clear();
        this.addEntita(this.sfondo);

        Brick brick = this.mappa.getNextBrick();
        while(brick != null){
            this.addEntita(brick);
            brick = this.mappa.getNextBrick();
        }
        this.mappa.azzeraContatori();

        this.addEntita(this.palla);
        this.addEntita(this.paddle);

        for(IndicatoreVita iv : this.indicatoriVita)
            this.addEntita(iv);
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        this.risorseCaricate = false;
        this.inizializzaRisorse(screenWidth, screenHeight);
        this.addEntitaScena();
        this.risorseCaricate = true;
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.update(dt, screenWidth, screenHeight, canvas, paint);
        if(this.palla != null && this.palla.isMoving())
            this.palla.setRotazione(this.palla.getRotazione() + this.stile.getVelocitaRotazionePalla() * dt);
        this.logicaAlterazioni();
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(!this.risorseCaricate){

        }else{
            super.render(dt, screenWidth, screenHeight, canvas, paint);
        }
    }

    @Override
    public void ownerSizeChange(int newScreenWidth, int newScreenHeight) {
        float pesoWidht = (float) newScreenWidth / (float) this.lastScreenWidth;
        float pesoHeight = (float) newScreenHeight / (float) this.lastScreenHeight;
        Vector2D scaleVector = new Vector2D(pesoWidht, pesoHeight);

        //Cambio la size per gli elementi della scenaClassica
        this.palla.setOffsetCollisioneSuperiore((int)(this.palla.getOffsetCollisioneSuperiore() * pesoHeight));

        super.ownerSizeChange(newScreenWidth, newScreenHeight);
    }

    @Override
    public void sendEvent(String idEvent, ParamList parametri) {

        if(idEvent.equals(ModalitaClassica.EVENTO_BLOCCO_ROTTO)){
            if(this.pmList != null){
                Brick brick = parametri.get("brick");
                for(int i = 0; i < this.PARTICELLE_ROTTURA_BLOCCO; i++){
                    //Spawn delle particelle di rottura
                    this.addEntita(new Particella(
                            brick.getPosition(),
                            new Vector2D(5, 5),
                            Color.GRAY,
                            1000
                    ));
                }
                this.removeEntita(brick);
                PM powerup = this.pmList.getPowerup(
                        brick.getPosition(),
                        new Vector2D(this.lastScreenWidth * this.percentualeDimensionePowerup, this.lastScreenWidth * this.percentualeDimensionePowerup),
                        this.owner
                );
                if(powerup != null)
                    this.addEntita(powerup);
            }
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_RIMOZIONE_PARTICELLA)){
            Entity e = parametri.get("particella");
            this.removeEntita(e);
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_RIMOZIONE_POWERUP)){
            PM pm = parametri.get("powerup");
            this.removeEntita(pm);
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_POWERUP)){
            PM pm = parametri.get("powerup");
            pm.getAlterazione().attivaAlterazione(this.status, this.creaParametriAlterazioni());
            this.removeEntita(pm);

            //Aggiunge l'immagine del powerup e l'indicatore
            this.powerUpAttivi.add(pm);
            float widthIndicatore = this.lastScreenWidth * this.percentualeDimensioneIndicatori;
            this.addEntita(pm.getIndicatorePM(
                new Vector2D(0, 0),
                new Vector2D(widthIndicatore, widthIndicatore)
            ));
        }
    }

    //Eventi

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.palla != null && !this.palla.isMoving())
            this.palla.startPalla();

        if(this.paddle != null){
            this.paddle.setTargetX(event.getX());
        }

        return true;
    }
}