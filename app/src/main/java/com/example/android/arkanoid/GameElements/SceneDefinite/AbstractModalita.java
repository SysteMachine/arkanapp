package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.app.Service;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractScene;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverListener;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class AbstractModalita extends AbstractScene implements View.OnTouchListener, SensorEventListener {
    public final static String PARAMETRO_ALTERAZIONE_STILE = "stile";               //PARAMETRO PER LE ALTERAZIONI -> STILE
    public final static String PARAMETRO_ALTERAZIONE_GAMELOOP = "gameLoop";         //PARAMETRO PER LE ALTERAZIONI -> GAME_LOOP

    protected int DIMENSIONE_ZONA_PUNTEGGIO = 80;                                   //Zona dello schermo dedicata al punteggio
    protected float PERCENTUALE_DIMENSIONE_INDICATORI = 0.06f;                      //Dimensione degli indicatori in alto
    protected float PERCENTUALE_DIMENSIONE_FONT = 0.03f;                            //Dimensione del font

    //-----------------------------------------------------------------------------------------------------------//
    protected int codiceModalita;                                       //Codice della modalita corrente

    protected ArrayList<AbstractAlterazione> alterazioniAttive;         //Lista delle alterazioni attive
    protected HashMap<String, Method> eventiCollegati;                  //Lista degli eventi collegati alla modalita

    protected Stile stile;                                              //Stile della modalita
    protected GameStatus status;                                        //Status della modalita
    protected boolean risorseCaricate;                                  //Flag di caricamento delle risorse

    protected GameOverListener gameOverListener;                        //Listener per il gameOver

    //Gestione dei sensori
    protected float rotazioneAsseZ;                                     //Rotazione sull'asseZ del disposititvo
    protected long sensoreTimeStamp;                                    //Ultimo timeStamp del sensore

    protected Sprite spriteIndicatoreVita;                              //Sprite per l'indicatore della vita

    protected int screenWidth;                                          //Larghezza dello schermo
    protected int screenHeight;                                         //Altezza dello schermo
    public AbstractModalita(Stile stile, GameStatus status) {
        super(0);

        this.stile = stile;
        this.status = status;

        this.risorseCaricate = false;

        this.alterazioniAttive = new ArrayList<>();
        this.eventiCollegati = new HashMap<>();

        this.screenWidth = 0;
        this.screenHeight = 0;

        this.codiceModalita = 0;
    }

    @Override
    protected void setGameLoop(GameLoop gameLoop) {
        super.setGameLoop(gameLoop);
        gameLoop.setOnTouchListener(this);
        if(this.status != null && this.status.getModalitaControllo() == GameStatus.GYRO){
            //Aggiunta del sensore giroscopio
            SensorManager sensorManager = (SensorManager) gameLoop.getContext().getSystemService(Service.SENSOR_SERVICE);
            Sensor giroscopio = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            if(giroscopio == null) {
                this.status.setModalitaControllo(GameStatus.TOUCH);
            }else {
                this.rotazioneAsseZ = 0;
                this.sensoreTimeStamp = -1;
                sensorManager.registerListener(this, giroscopio, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    @Override
    protected void removeGameLoop() {
        this.owner.setOnTouchListener(null);
        if(this.status.getModalitaControllo() == GameStatus.GYRO){
            SensorManager sensorManager = (SensorManager) this.owner.getContext().getSystemService(Service.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
        }
        super.removeGameLoop();
    }

    /**
     * Crea i parametri da passare alle alterazioni
     * @return Restituisce i parametri dell'alterazione
     */
    protected ParamList creaParametriAlterazioni(){
        ParamList pl = this.creaParametriEntita();

        pl.add(AbstractModalita.PARAMETRO_ALTERAZIONE_STILE, this.stile);
        pl.add(AbstractModalita.PARAMETRO_ALTERAZIONE_GAMELOOP, this.owner);

        return pl;
    }

    /**
     * Disegna sullo schermo il punteggio del giocatore
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaPunteggio(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        float lastSize = paint.getTextSize();
        paint.setTextSize(screenHeight * this.PERCENTUALE_DIMENSIONE_FONT);

        String messaggio = "P: " + this.status.getPunteggio();
        Rect bound = new Rect();
        paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

        float startX = (screenWidth * 0.5f) - (bound.right * 0.5f);
        float startY = (this.DIMENSIONE_ZONA_PUNTEGGIO * 0.5f) - (bound.bottom * 0.5f) + 25;

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(messaggio, startX, startY, paint);
        paint.setTextSize(lastSize);
    }

    /**
     * Disegna sullo schermo la vita del giocatore
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaVita(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        float startX = this.spriteIndicatoreVita.getWidth() * 0.5f;
        float startY = this.DIMENSIONE_ZONA_PUNTEGGIO * 0.5f;

        for(int i = 0; i < this.status.getHealth(); i++){
            this.spriteIndicatoreVita.drawSprite(
                    (int)(startX + (this.spriteIndicatoreVita.getWidth() * i)),
                    (int)startY,
                    canvas,
                    paint
            );
        }
    }

    /**
     * Disegna sullo schermo le alterazioni attive nella modalità
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaAlterazioni(int screenWidth, int screenHeight, Canvas canvas, Paint paint){
        float size = screenWidth * this.PERCENTUALE_DIMENSIONE_INDICATORI;

        float startX = screenWidth - (size * 0.5f);
        float startY = this.DIMENSIONE_ZONA_PUNTEGGIO * 0.5f;

        for(int i = 0; i < this.alterazioniAttive.size(); i++){
            Sprite sprite = this.alterazioniAttive.get(i).getSpriteIcona();
            if(sprite != null){
                sprite.resizeImage(new Vector2D(size, size));
                sprite.drawSprite(
                        (int)(startX - (size * i)),
                        (int)startY,
                        canvas,
                        paint
                );
            }
        }
    }

    /**
     * logica del ripristino delle posizioni
     */
    protected abstract void logicaRipristinoPosizioni();

    /**
     * Logica di terminazione della partita
     * Controlla le azioni e le condizioni che portano alla conclusione della partita
     */
    protected void logicaTerminazionePartita(){
        if(this.status.getHealth() == 0 && this.gameOverListener != null)
            this.gameOverListener.gameOver(this.status);
    }

    /**
     * Logica di eliminazione della vita
     * Controlla le condizioni che portano alla rimozione di una vita al giocatore
     */
    protected abstract void logicaEliminazioneVita();

    /**
     * Logica di avanzamento al prossimo livello
     * Controlla le condizioni che portano al caricamento del prossimo livello da giocare
     */
    protected abstract void logicaAvanzamentoLivello();

    /**
     * Logica di gestione delle alterazioni
     * Controllo della valità delle alterazioni
     */
    protected void logicaAlterazioni(){
        //Rimuove i powerup consumati e il relativo indicatore
        for(Iterator<AbstractAlterazione> it = this.alterazioniAttive.iterator(); it.hasNext();){
            AbstractAlterazione next = it.next();
            if(this.status != null)
                next.logica(this.status, this.creaParametriAlterazioni());

            //Se l'alterazione è consumata allora viene eliminata
            if(!next.isAlterazioneAttiva())
                it.remove();
        }
    }

    /**
     * Logica della partita
     */
    protected void logicaDiPartita(){
        this.logicaEliminazioneVita();
        this.logicaTerminazionePartita();
        this.logicaAvanzamentoLivello();
        this.logicaAlterazioni();
    }

    /**
     * Inizializza le componenti grafiche globali della modalita
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    protected void inizializzaElementiGlobali(int screenWidth, int screenHeight){
        float size = screenWidth * this.PERCENTUALE_DIMENSIONE_INDICATORI;
        this.spriteIndicatoreVita = this.stile.getIndicatoreVitaStile(this.owner);
        this.spriteIndicatoreVita.resizeImage(new Vector2D(size, size));
    }

    /**
     * Esegue l'inizializzazione di tutte le entità di gioco
     * @param screenWidth Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     */
    protected abstract void iniziallizzazioneEntita(int screenWidth, int screenHeight);

    /**
     * Esegue l'inizializzazione dell'audio della scena
     */
    protected abstract void inizializzazioneAudio();

    /**
     * Esegue l'inserimento delle entità all'interno della scena
     */
    protected abstract void inserimentoEntita();

    @Override
    public void setup(int screenWidth, int screenHeight) {
        this.risorseCaricate = false;

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.inizializzaElementiGlobali(screenWidth, screenHeight);
        this.iniziallizzazioneEntita(screenWidth, screenHeight);
        this.inizializzazioneAudio();
        this.inserimentoEntita();

        this.risorseCaricate = true;
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(this.risorseCaricate){
            super.update(dt, screenWidth, screenHeight, canvas, paint);
            this.logicaDiPartita();
        }
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(this.risorseCaricate){
            super.render(dt, screenWidth, screenHeight, canvas, paint);
            this.disegnaPunteggio(screenWidth, screenHeight, canvas, paint);
            this.disegnaVita(screenWidth, screenHeight, canvas, paint);
            this.disegnaAlterazioni(screenWidth, screenHeight, canvas, paint);
        }
    }

    /**
     * Collega un evento all'interno della modalità
     * @param idEvento Id dell'evento da inserire
     * @param funzioneEvento Funzione dell'evento (La funzione deve avere un solo parametro e deve essere un ParamList)
     */
    protected void collegaEventoScena(String idEvento, Method funzioneEvento){
        if(funzioneEvento != null){
            Class[] parametri = funzioneEvento.getParameterTypes();
            if(parametri.length == 1 && parametri[0].equals(ParamList.class))
                this.eventiCollegati.put(idEvento, funzioneEvento);
        }
    }

    @Override
    public void sendEvent(String idEvent, ParamList parametri) {
        Method funzione = this.eventiCollegati.get(idEvent);
        if(funzione != null && parametri != null) {
            try {
                funzione.invoke(this, parametri);
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    //Eventi

    /**
     * Evento chiamato quando lo schermo viene toccato
     * @param position Posizione dello schermo toccata
     * @param v Vista toccata
     * @param e Motion event
     */
    public abstract void touchEvent(Vector2D position, View v, MotionEvent e);

    /**
     * Evento invocato quando la rotazione sull'asse z del dispositivo è modificata
     * @param degree Orientamento attuale del dispositivo
     */
    public abstract void orientationChanged(float degree);


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.owner.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            System.out.println("1");
            this.touchEvent(
                    new Vector2D(
                            event.getX() * (this.screenWidth / (float) v.getWidth()),
                            event.getY() * (this.screenHeight / (float) v.getHeight())
                    ),
                    v,
                    event
            );
        }else{
            float realWidth = v.getHeight() * (9.0f / 16.0f);                   //Calcola la larghezza della canvas
            float startX = (v.getWidth() * 0.5f) - (realWidth * 0.5f);          //Calcola la posizione di start
            if(event.getX() >= startX && event.getX() <= startX + realWidth){
                //Se il tocco avviene all'interno tra startX e startX + realWidth

                float allineataX = event.getX() - startX;                                //Calcola la posizione X allineata
                float posX = (allineataX / realWidth) * 720.0f;
                float posY = (event.getY() / (float)v.getHeight()) * 1280.0f;
                this.touchEvent(
                        new Vector2D(
                                posX,
                                posY
                        ),
                        v,
                        event
                );
            }
        }
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(this.status.getModalitaControllo() == GameStatus.GYRO && event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
            //Cambio del valore sul giroscopio
            if(this.sensoreTimeStamp != -1){
                //Se è stato osservato una volta il cambiamento del sensore
                float dt = (System.currentTimeMillis() - this.sensoreTimeStamp) / 1000.0f;
                float spostamento = (event.values[2] * dt * 180) / (float)Math.PI;    //Calcolato in gradi, velocitaZ + dt è in radianti
                this.rotazioneAsseZ += spostamento;

                this.orientationChanged(this.rotazioneAsseZ);
            }

            this.sensoreTimeStamp = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    //Beam
    /**
     * Imposta il GameOverListener invocato al termine di una partita
     * @param gol GameOverInterface
     */
    public void setGameOverListener(GameOverListener gol){
        this.gameOverListener = gol;
    }


    //Beam
    public Stile getStile() {
        return stile;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getCodiceModalita() {
        return codiceModalita;
    }
}