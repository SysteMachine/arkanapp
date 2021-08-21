package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.Ball;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractScene;
import com.example.android.arkanoid.GameElements.Brick;
import com.example.android.arkanoid.GameElements.Map;
import com.example.android.arkanoid.GameElements.Paddle;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener{
    public final static String EVENTO_BLOCCO_ROTTO = "rotturaBlocco";

    public final static String MAPPA = "Mappa";

    private Ball palla;
    private Paddle paddle;
    private Map mappa;

    //Sprite degli elementi
    private Sprite sPalla;
    private Sprite sPaddle;
    private Sprite[] sBrick;
    private MultiSprite sCrepe;
    private int[] coloriBrick = {
            Color.GREEN,
            Color.YELLOW,
            Color.rgb(255, 165, 0),
            Color.RED
    };

    public ModalitaClassica() {
        super(0);
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        super.setGameLoop(gameLoop);
        gameLoop.setOnTouchListener(this);
    }

    @Override
    protected void removeGameLoop() {
        this.owner.setOnTouchListener(null);
        super.removeGameLoop();
    }

    @Override
    protected ParamList creaParametriEntita() {
        ParamList listaParametri = super.creaParametriEntita();
        listaParametri.add(ModalitaClassica.MAPPA, this.mappa);
        return listaParametri;
    }

    /**
     * Disegna la palla della scena
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    private void disegnaPalla(Canvas canvas, Paint paint){
        if(this.palla != null && this.sPaddle != null){
            this.sPalla.drawSprite(
                    (int)this.palla.getPosition().getPosX(),
                    (int)this.palla.getPosition().getPosY(),
                    (int)this.palla.getSize().getPosX(),
                    (int)this.palla.getSize().getPosY(),
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
    private void disegnaPaddle(Canvas canvas, Paint paint){
        if(this.paddle != null && this.sPaddle != null){
            this.sPaddle.drawSprite(
                    (int)this.paddle.getPosition().getPosX(),
                    (int)this.paddle.getPosition().getPosY(),
                    (int)this.paddle.getSize().getPosX(),
                    (int)this.paddle.getSize().getPosY(),
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
    private void disegnaMappa(Canvas canvas, Paint paint){
        if(this.mappa != null && this.sBrick != null){
            for(int i = 0; i < this.mappa.getnRighe(); i++){
                for(int j = 0; j < this.mappa.getnColonne(); j++){
                    Brick brick = this.mappa.getElementiMappa()[i][j];
                    if(brick != null && brick.getHealth() > 0){
                        int colore = (int)( (i + j) % this.coloriBrick.length);
                        this.sBrick[colore].drawSprite(
                                (int)brick.getPosition().getPosX(),
                                (int)brick.getPosition().getPosY(),
                                (int)brick.getSize().getPosX(),
                                (int)brick.getSize().getPosY(),
                                canvas,
                                paint
                        );
                        this.sCrepe.setCurrentFrame(Map.MAX_HEALTH_BRICK - brick.getHealth());
                        this.sCrepe.drawSprite(
                                (int)brick.getPosition().getPosX(),
                                (int)brick.getPosition().getPosY(),
                                (int)brick.getSize().getPosX(),
                                (int)brick.getSize().getPosY(),
                                canvas,
                                paint
                        );
                    }
                }
            }
        }
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        this.palla = new Ball(800, new Vector2D(screenWidth * 0.5f, screenHeight * 0.6f), 40, 30);
        this.sPalla = new Sprite(R.drawable.palla_palla1, this.owner);

        this.paddle = new Paddle(1000, new Vector2D(screenWidth * 0.5f, screenHeight * 0.7f), 300, 40);
        this.sPaddle = new Sprite(R.drawable.paddle_paddle1, this.owner);

        this.mappa = new Map(6, 8, 0, (int)(screenHeight * 0.2), screenWidth, (int)(screenWidth * 0.5));
        this.sBrick = new MultiSprite[this.coloriBrick.length];
        for(int i = 0; i < this.coloriBrick.length; i++){
            this.sBrick[i] = new MultiSprite(R.drawable.brick_birk1, this.owner, 3);
            this.sBrick[i].replaceColor(Color.WHITE, this.coloriBrick[i], 200);
        }
        this.sCrepe = new MultiSprite(R.drawable.birk_crepe, this.owner, 10);

        //Inserimento delle entità per la logica di base
        this.addEntita(this.palla);
        this.addEntita(this.paddle);

        //Cambio di stile
        this.sPalla.replaceColor(Color.WHITE, Color.RED, 200);
        this.sPaddle.replaceColor(Color.WHITE, Color.rgb(200, 100, 25), 200);

        MediaPlayer mp = MediaPlayer.create(this.owner.getContext(), R.raw.audio_test1);
        try{
            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    mp.start();
                }
            });
        }catch(Exception e){e.printStackTrace();}
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        this.disegnaPalla(canvas, paint);
        this.disegnaPaddle(canvas, paint);
        this.disegnaMappa(canvas, paint);
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
