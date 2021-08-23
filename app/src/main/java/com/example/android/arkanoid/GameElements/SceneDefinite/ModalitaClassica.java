package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.Ball;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.GameElements.Brick;
import com.example.android.arkanoid.GameElements.Map;
import com.example.android.arkanoid.GameElements.Paddle;
import com.example.android.arkanoid.GameElements.Stile;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener{
    public final static String EVENTO_BLOCCO_ROTTO = "rotturaBlocco";
    public final static String MAPPA = "Mappa";

    protected Ball palla;
    protected Paddle paddle;
    protected Map mappa;

    protected Sprite sPalla;
    protected Sprite sPaddle;
    protected Sprite[] sBrick;
    protected MultiSprite sCrepe;
    protected Sprite sSfondo;
    protected Sprite sBottom;
    protected Sprite sZonaPunteggio;

    protected Stile stile;

    protected boolean risorseCaricate;

    public ModalitaClassica(Stile stile) {
        super(0);

        this.stile = stile;
        this.risorseCaricate = false;
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
        listaParametri.add(ModalitaClassica.MAPPA, this.mappa);
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
                    int indexImmagine = (int)( (this.sCrepe.getnImages() - 1) * pesoVita );
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

        this.sBottom = this.stile.getImmagineBottomStile(this.owner);
        this.sZonaPunteggio = this.stile.getImmagineZonaPunteggioStile(this.owner);
    }

    /**
     * Ridimensiona gli elementi della scena
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    private void resizeImages(int screenWidth, int screenHeight){
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

        //TODO ridimensionare crepe bottom e zonaPunteggio -> controllare le sottoclassi di sprite per il ridimensionamento
    }

    /**
     * Aggiunge le entità alla scena
     */
    private void addEntitaScena(){
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
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(this.risorseCaricate){
            this.disegnaSfondo(screenWidth, screenHeight, canvas, paint);
            this.disegnaPalla(canvas, paint);
            this.disegnaPaddle(canvas, paint);
            this.disegnaMappa(canvas, paint);
        }else{
            //TODO schermata di caricamento
        }
    }

    @Override
    public void ownerSizeChange(int newScreenWidth, int newScreenHeight) {
        super.ownerSizeChange(newScreenWidth, newScreenHeight);
        resizeImages(newScreenWidth, newScreenHeight);
    }

    @Override
    public void sendEvent(String idEvent, ParamList parametri) {
        System.out.println("Evento: " + idEvent);
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
