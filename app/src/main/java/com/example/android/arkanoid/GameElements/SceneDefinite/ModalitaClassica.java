package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.Ball;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractPowerUpMalus;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.GameElements.BaseElements.GameStatus;
import com.example.android.arkanoid.GameElements.BaseElements.PowerupMalusList;
import com.example.android.arkanoid.GameElements.Brick;
import com.example.android.arkanoid.GameElements.Map;
import com.example.android.arkanoid.GameElements.Paddle;
import com.example.android.arkanoid.GameElements.BaseElements.Stile;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.Util.Util;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.util.ArrayList;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener{
    public final static String EVENTO_BLOCCO_ROTTO = "rotturaBlocco";
    public final static String EVENTO_POWERUP = "powerup";
    public final static String EVENTO_RIMOZIONE_POWERUP = "rimozionePowerup";

    public final static String PARAMETRO_MAPPA = "Mappa";

    protected Ball palla;
    protected Paddle paddle;
    protected Map mappa;

    protected ArrayList<AbstractPowerUpMalus> powerUpMalus;
    protected ArrayList<AbstractPowerUpMalus> powerUpAttivi;

    protected float rotazionePalla;

    protected Sprite sPalla;
    protected Sprite sPaddle;
    protected Sprite[] sBrick;
    protected MultiSprite sCrepe;
    protected Sprite sSfondo;

    protected AnimatedSprite sIndicatoreVita;
    protected Vector2D posizioneIndicatoreVita;
    protected float percentualeDimensioneIndicatore;

    protected Stile stile;
    protected GameStatus status;
    protected PowerupMalusList powerupList;
    protected float percentualeDimensionePowerup;

    protected boolean risorseCaricate;

    public ModalitaClassica(Stile stile, GameStatus status, PowerupMalusList powerupList) {
        super(0);

        this.stile = stile;
        this.status = status;
        this.risorseCaricate = false;

        this.powerupList = powerupList;
        this.powerUpMalus = new ArrayList<AbstractPowerUpMalus>();
        this.powerUpAttivi = new ArrayList<AbstractPowerUpMalus>();

        this.percentualeDimensioneIndicatore = 0.05f;
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

    @Override
    protected ParamList creaParametriEntita() {
        ParamList listaParametri = super.creaParametriEntita();
        listaParametri.add(ModalitaClassica.PARAMETRO_MAPPA, this.mappa);
        return listaParametri;
    }

    /**
     * Disegna lo sfondo della scena
     * @param canvas Cancas di disengo
     * @param paint Paint di disegno
     */
    protected void disegnaSfondo(int screenWidht, int screenHeight, Canvas canvas, Paint paint){
        if(this.sSfondo != null){
            this.sSfondo.drawSprite(
                    (int)(screenWidht * 0.5f),
                    (int)(screenHeight * 0.5f),
                    canvas,
                    paint
            );
        }
    }

    /**
     * Disegna la palla della scena
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaPalla(Canvas canvas, Paint paint){
        if(this.palla != null && this.sPalla != null){
            this.sPalla.setRotazione(this.rotazionePalla);
            this.sPalla.drawSprite(
                    (int)this.palla.getPosition().getPosX(),
                    (int)this.palla.getPosition().getPosY(),
                    canvas,
                    paint
            );
        }
    }

    /**
     * Disegna il paddle della scena
     * @param canvas Cancas di disengo
     * @param paint Paint di disegno
     */
    protected void disegnaPaddle(Canvas canvas, Paint paint){
        if(this.paddle != null && this.sPaddle != null) {
            this.sPaddle.drawSprite(
                    (int) this.paddle.getPosition().getPosX(),
                    (int) this.paddle.getPosition().getPosY(),
                    canvas,
                    paint
            );
        }
    }

    /**
     * Disegna l'indicatore della vita
     * @param canvas Cancas di disengo
     * @param paint Paint di disegno
     */
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

    /**
     * Disegna i brick presenti nella scena
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaMappa(Canvas canvas, Paint paint){
        if(this.mappa != null && this.sBrick != null && this.mappa.isAviable()){

            Brick brick = this.mappa.getNextBrick();
            int posizioneColoreCorrente = 0;    //Per impostare il colore del brick
            while(brick != null){
                //Lavoro sul singolo brick
                if(brick.getHealth() > 0){
                    int colore = posizioneColoreCorrente % this.stile.getColoriCasualiBrick().length;

                    this.sBrick[colore].drawSprite(
                            (int)brick.getPosition().getPosX(),
                            (int)brick.getPosition().getPosY(),
                            canvas,
                            paint
                    );

                    float pesoVita = 1 - ( (float)brick.getHealth() / (float)brick.getMaxHealth() );    //Va da 0 a 1, 0 se la vita è al massimo, altrimenti restituisce 1
                    int indexImmagine = (int)Math.ceil( (this.sCrepe.getnImages() - 1) * pesoVita );
                    this.sCrepe.setCurrentFrame(indexImmagine);

                    this.sCrepe.drawSprite(
                            (int)brick.getPosition().getPosX(),
                            (int)brick.getPosition().getPosY(),
                            canvas,
                            paint
                    );
                }

                brick = this.mappa.getNextBrick();
                posizioneColoreCorrente++;
            }
            this.mappa.azzeraContatori();
        }
    }

    /**
     * Crea i parametri da passare alle alterazioni
     * @return Restituisce i parametri dell'alterazione
     */
    protected ParamList creaParametriAlterazioni(){
        ParamList parametri = new ParamList();
        parametri = this.creaParametriEntita();
        return parametri;
    }

    /**
     * Disegna i powerup sullo schermo
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaPowerup(Canvas canvas, Paint paint){
        for(AbstractPowerUpMalus apum : this.powerUpMalus){
            Sprite sprite = apum.getPowerUpMalusImage();
            sprite.drawSprite(
                    (int)apum.getPosition().getPosX(),
                    (int)apum.getPosition().getPosY(),
                    canvas,
                    paint
            );
        }
    }

    protected void disegnaPowerupAttivi(int screenWidht, Canvas canvas, Paint paint){
        int startX = screenWidht;
        int startY = 0;

        for(AbstractPowerUpMalus apum : this.powerUpAttivi){
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

    /**
     * Logica di gestione delle alterazioni
     */
    protected void logicaAlterazioni(){
        for(AbstractPowerUpMalus apum : this.powerUpAttivi){
            apum.getAlterazione().logica(this.status, this.creaParametriAlterazioni());
        }
    }

    /**
     * Inizializza le risorse della scena
     * @param screenWidht Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    protected void inizializzaRisorse(int screenWidht, int screenHeight){
        this.palla = new Ball(
                this.stile.getVelocitaInizialePalla(),
                this.stile.getPercentualePosizionePalla().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                this.stile.getAngoloDiLancioMassimoPalla(),
                (int)(this.stile.getPercentualeRaggioPalla() * screenWidht)
        );
        this.palla.setOffsetCollisioneSuperiore(80);
        this.rotazionePalla = 0;
        this.sPalla = this.stile.getImmaginePallaStile(this.owner);

        this.paddle = new Paddle(
                this.stile.getVelocitaInizialePaddle(),
                this.stile.getPercentualePosizionePaddle().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                this.stile.getPercentualeDimensionePaddle().prodottoPerVettore(new Vector2D(screenWidht, screenHeight))
        );
        this.sPaddle = this.stile.getImmaginePaddleStile(this.owner);

        this.mappa = new Map(
                this.stile.getNumeroRigheMappa(),
                this.stile.getNumeroColonneMappa(),
                this.stile.getPercentualePosizioneMappa().prodottoPerVettore(new Vector2D(screenWidht, screenHeight)),
                this.stile.getPercentualeDimensioneMappa().prodottoPerVettore(new Vector2D(screenWidht, screenHeight))
        );
        this.sBrick = new Sprite[this.stile.getColoriCasualiBrick().length];
        for(int i = 0; i < this.sBrick.length; i++){
            this.sBrick[i] = this.stile.getImmagineBrickStile(this.owner, i);
        }

        this.sSfondo = this.stile.getImmagineSfondoStile(this.owner);
        this.sCrepe = new MultiSprite(R.drawable.crepebrick, this.owner, 10);

        this.sIndicatoreVita = new AnimatedSprite(R.drawable.indicatori_vita, this.owner, 4, 5);
        this.posizioneIndicatoreVita = new Vector2D(0 + (this.percentualeDimensioneIndicatore * screenWidht), 0 + (this.percentualeDimensioneIndicatore * screenWidht));
    }

    /**
     * Ridimensiona gli elementi della scena
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    protected void resizeImages(int screenWidth, int screenHeight){
        //Ridimensionamento della palla
        this.sPalla.resizeImage(this.palla.getSize());

        //Ridimensionamento del paddle
        this.sPaddle.resizeImage(this.paddle.getSize());

        //Ridimensionamento dei brick
        Brick firstBrick = this.mappa.getNextBrick();
        this.mappa.azzeraContatori();
        for(int i = 0; i < this.sBrick.length; i++){
            this.sBrick[i] = this.stile.getImmagineBrickStile(this.owner, i);
            this.sBrick[i].resizeImage(firstBrick.getSize());
        }

        //Ridimensionamento dello sfondo
        this.sSfondo.resizeImage(screenWidth, screenHeight);
        this.sCrepe.resizeImage(firstBrick.getSize());

        this.sIndicatoreVita.resizeImage((int)(this.percentualeDimensioneIndicatore * screenWidth), (int)(this.percentualeDimensioneIndicatore * screenWidth));
    }

    /**
     * Aggiunge le entità alla scena
     */
    protected void addEntitaScena(){
        this.entita.clear();
        this.addEntita(this.palla);
        this.addEntita(this.paddle);
        Brick brick = this.mappa.getNextBrick();
        while(brick != null){
            this.addEntita(brick);
            brick = this.mappa.getNextBrick();
        }
        this.mappa.azzeraContatori();
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        this.risorseCaricate = false;
        this.inizializzaRisorse(screenWidth, screenHeight);
        this.resizeImages(screenWidth, screenHeight);
        this.addEntitaScena();
        this.risorseCaricate = true;
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        super.update(dt, screenWidth, screenHeight, canvas, paint);
        if(this.palla != null && this.palla.isMoving())
            this.rotazionePalla += this.stile.getVelocitaRotazionePalla() * dt;
        this.logicaAlterazioni();
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(this.risorseCaricate){
            this.disegnaSfondo(screenWidth, screenHeight, canvas, paint);
            this.disegnaPalla(canvas, paint);
            this.disegnaPaddle(canvas, paint);
            this.disegnaMappa(canvas, paint);

            this.disegnaPowerup(canvas, paint);
            this.disegnaIndicatoreVita(canvas, paint);
            this.disegnaPowerupAttivi(screenWidth, canvas, paint);
        }else{
            //TODO schermata di caricamento
        }
    }

    @Override
    public void ownerSizeChange(int newScreenWidth, int newScreenHeight) {
        if(this.lastScreenWidth != 0 && this.lastScreenHeight != 0) {
            float pesoWidht = (float) newScreenWidth / (float) this.lastScreenWidth;
            float pesoHeight = (float) newScreenHeight / (float) this.lastScreenHeight;
            Vector2D scaleVector = new Vector2D(pesoWidht, pesoHeight);

            //Cambio la size per gli elementi della scenaClassica
            this.palla.setOffsetCollisioneSuperiore((int)(this.palla.getOffsetCollisioneSuperiore() * pesoHeight));
            this.posizioneIndicatoreVita = this.posizioneIndicatoreVita.prodottoPerVettore(scaleVector);
        }
        super.ownerSizeChange(newScreenWidth, newScreenHeight);
        resizeImages(newScreenWidth, newScreenHeight);
    }

    @Override
    public void sendEvent(String idEvent, ParamList parametri) {

        if(idEvent.equals(ModalitaClassica.EVENTO_BLOCCO_ROTTO)){
            if(this.powerupList != null){
                Brick brick = parametri.get("brick");
                AbstractPowerUpMalus powerup = this.powerupList.getPowerup(
                        brick.getPosition(),
                        new Vector2D(this.lastScreenWidth * this.percentualeDimensionePowerup, this.lastScreenWidth * this.percentualeDimensionePowerup),
                        this.owner
                );
                if(powerup != null){
                    //Solo se il powerup è stato spawnato
                    this.powerUpMalus.add(powerup);
                    this.addEntita(powerup);
                }
            }
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_RIMOZIONE_POWERUP)){
            AbstractPowerUpMalus powerupMalus = parametri.get("powerup");
            this.powerUpMalus.remove(powerupMalus);
            this.removeEntita(powerupMalus);
        }

        if(idEvent.equals(ModalitaClassica.EVENTO_POWERUP)){
            AbstractPowerUpMalus powerupMalus = parametri.get("powerup");
            powerupMalus.getAlterazione().attivaAlterazione(this.status, this.creaParametriAlterazioni());
            this.powerUpMalus.remove(powerupMalus);
            this.removeEntita(powerupMalus);
            this.powerUpAttivi.add(powerupMalus);
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
