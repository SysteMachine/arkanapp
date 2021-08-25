package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiGioco.Ball;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractPM;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.PMList;
import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.GameElements.ElementiGioco.Map;
import com.example.android.arkanoid.GameElements.ElementiGioco.Paddle;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.ElementiGioco.Sfondo;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.ArrayList;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener{
    public final static String EVENTO_BLOCCO_ROTTO = "rotturaBlocco";
    public final static String EVENTO_POWERUP = "powerup";
    public final static String EVENTO_RIMOZIONE_POWERUP = "rimozionePowerup";

    private final int OFFSET_SUPERIORE_PALLA = 80;

    //-----------------------------------------------------------------------------------------------------------//

    protected Ball palla;                                       //Palla di scena
    protected Paddle paddle;                                    //Paddle di scena
    protected Map mappa;                                        //Mappa della scena
    protected Sfondo sfondo;                                    //Sfondo della mappa

    protected ArrayList<AbstractPM> powerUpAttivi;              //Lista dei powerup attivi

    protected AnimatedSprite sIndicatoreVita;                   //Sprite per l'indicatore della vita

    protected float percentualePosizioneYIndicatori;
    protected float percentualeDimensioneIndicatore;
    protected float percentualeDimensionePowerup;

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

        this.percentualeDimensionePowerup = 0.05f;
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
        return this.creaParametriEntita();
    }

    /*
    protected void disegnaIndicatoreVita(Canvas canvas, Paint paint){
        if(this.sIndicatoreVita != null && this.status != null){
            int startX = (int)this.posizioneIndicatoreVita.getPosX();
            int startY = (int)this.posizioneIndicatoreVita.getPosY();
            int step = this.sIndicatoreVita.getSingleWidht();

            for(int i = 0; i < this.status.getHealth(); i++){
                this.sIndicatoreVita.drawSprite(
                        startX,
                        startY,
                        canvas,
                        paint
                );
                startX += step;
            }
        }
    }

    protected void disegnaPowerupAttivi(int screenWidht, Canvas canvas, Paint paint){
        int startX = screenWidht;
        int startY = 0;

        for(AbstractPM apum : this.powerUpAttivi){
            if(apum.getAlterazione().isAlterazioneAttiva()){
                //Solo se l'alterazione è attiva viene disegnata
                startX -= apum.getSize().getPosX() * 0.5f;
                Sprite sprite = apum.getPowerUpMalusImage();
                sprite.drawSprite(
                        startX,
                        startY + (int)(apum.getSize().getPosY() * 0.5f),
                        canvas,
                        paint
                );
            }
        }
    }
     */

    /**
     * Logica di gestione delle alterazioni
     */
    protected void logicaAlterazioni(){
        for(AbstractPM apum : this.powerUpAttivi){
            AbstractAlterazione alterazione = apum.getAlterazione();
            if(alterazione != null)
                alterazione.logica(this.status, this.creaParametriAlterazioni());
        }
    }

    /**
     * Inizializza le risorse della scena
     * @param screenWidht Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    protected void inizializzaRisorse(int screenWidht, int screenHeight){
        this.palla = new Ball(
                this.stile.getPercentualePosizionePalla().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                new Vector2D(this.stile.getVelocitaInizialePalla(), this.stile.getVelocitaInizialePalla()),
                this.stile.getImmaginePallaStile(this.owner),
                (int)(this.stile.getPercentualeRaggioPalla() * screenWidht),
                this.stile.getAngoloDiLancioMassimoPalla()
        );
        this.palla.setOffsetCollisioneSuperiore(this.OFFSET_SUPERIORE_PALLA);

        this.paddle = new Paddle(
                this.stile.getPercentualePosizionePaddle().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
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
                this.stile.getPercentualePosizioneMappa().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                this.stile.getPercentualeDimensioneMappa().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                coloriBrick,
                new MultiSprite(R.drawable.crepebrick, this.owner, 10)
        );

        this.sfondo = new Sfondo(
                new Vector2D(screenWidht * 0.5f, screenHeight * 0.5f),
                new Vector2D(screenWidht, screenHeight),
                this.stile.getImmagineSfondoStile(this.owner)
        );
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
                this.removeEntita(brick);
                AbstractPM powerup = this.pmList.getPowerup(
                        brick.getPosition(),
                        new Vector2D(this.lastScreenWidth * this.percentualeDimensionePowerup, this.lastScreenWidth * this.percentualeDimensionePowerup),
                        this.owner
                );
                if(powerup != null)
                    this.addEntita(powerup);
            }
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_RIMOZIONE_POWERUP)){
            AbstractPM pm = parametri.get("powerup");
            this.removeEntita(pm);
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_POWERUP)){
            AbstractPM pm = parametri.get("powerup");
            pm.getAlterazione().attivaAlterazione(this.status, this.creaParametriAlterazioni());
            this.removeEntita(pm);
            this.powerUpAttivi.add(pm);
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