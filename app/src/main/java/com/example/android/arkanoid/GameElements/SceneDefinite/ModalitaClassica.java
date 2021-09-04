package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.app.Service;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.Util.SpriteUtil.AnimatedSprite;
import com.example.android.arkanoid.Util.SpriteUtil.MultiSprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class ModalitaClassica extends AbstractScene implements View.OnTouchListener, SensorEventListener {
    public final static String EVENTO_BLOCCO_COLPITO = "bloccoColpito";
    public final static String EVENTO_BLOCCO_ROTTO = "rotturaBlocco";
    public final static String EVENTO_POWERUP = "powerup";
    public final static String EVENTO_RIMOZIONE_POWERUP = "rimozionePowerup";
    public final static String EVENTO_RIMOZIONE_PARTICELLA = "rimozioneParticella";

    public final static String PARAMETRO_ALTERAZIONE_STILE = "stile";
    public final static String PARAMETRO_ALTERAZIONE_GAMELOOP = "gameLoop";

    protected final int INTERVALLO_MOVIMENTO_GIROSCOPIO_MASSIMO = 30;   //Grado massimo per lo spostamento del paddle

    protected int OFFSET_SUPERIORE_PALLA = 80;                          //Limite superiore della palla a 80 pixel
    protected int PARTICELLE_ROTTURA_BLOCCO = 30;                       //Numero di particelle da spawnare alla distruzione del blocco
    protected float PERCENTUALE_DEATH_ZONE = 0.95f;                     //Percentuale della deathzone in base all'altezza dello schermo, al 95% comincia
    protected float PERCENTUALE_DIMENSIONE_POWERUP = 0.1f;              //Dimensione dei powerup sullo schermo
    protected float PERCENTUALE_DIMENSIONE_INDICATORI = 0.06f;          //Dimensione degli indicatori in alto
    protected float PERCENTUALE_DIMENSIONE_FONT = 0.03f;                //Dimensione del font


    protected int PUNTI_PER_COLPO = 100;                                //Punti per ogni colpo del blocco
    protected int PUNTI_PER_PARTITA = 1500;                             //Punti per ogni partita completata

    //-----------------------------------------------------------------------------------------------------------//

    protected Ball palla;                                       //Palla di scena
    protected Paddle paddle;                                    //Paddle di scena
    protected Map mappa;                                        //Mappa della scena
    protected Sfondo sfondo;                                    //Sfondo della mappa
    protected IndicatoreVita[] indicatoriVita;                  //Indicatori della vita

    protected ArrayList<PM> powerUpAttivi;                      //Lista dei powerup attivi

    protected Stile stile;                                      //Stile della modalita
    protected GameStatus status;                                //Status della modalita
    protected PMList pmList;                                    //Lista dei parametri della modalita
    protected boolean risorseCaricate;                          //Flag di caricamento delle risorse

    //Gestione dei sensori
    protected float rotazioneAsseZ;                             //Rotazione sull'asseZ del disposititvo
    protected long sensoreTimeStamp;                            //Ultimo timeStamp del sensore
    protected float puntoZeroAsseZ;                             //Punto considerato come 0 nella rotazione
    protected int incrementoRotazioneAngolo;                    //Incremento della rotazione quando viene cambiato l'orientamento dello schermo
    protected float angoloRelativo;                             //Angolo relativo per il posizionamento del paddle ad esempio i valori che vanno da 0 a 30 o da 360 a 320 sono considerati come intervalli da 40 a -40
    protected int ultimoOrientamentoConosciuto;                 //Ultimo orientamento in cui si trovava il dispositivo

    public ModalitaClassica(Stile stile, GameStatus status, PMList pmList) {
        super(0);

        this.stile = stile;
        this.status = status;
        this.pmList = pmList;

        this.risorseCaricate = false;
        this.powerUpAttivi = new ArrayList<>();
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
                this.puntoZeroAsseZ = 0;
                this.incrementoRotazioneAngolo = 0;
                this.angoloRelativo = 0;
                this.ultimoOrientamentoConosciuto = gameLoop.getResources().getConfiguration().orientation;
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
     * Cambia il valore della vita del giocatore
     * @param vita Nuovo valore della vita
     */
    protected void cambiaVitaAttuale(int vita){
        if(vita <= this.status.getMaxHealth()){
            this.status.setHealth(vita);
            for(int i = 0; i < this.indicatoriVita.length; i++){
                if(i < this.status.getHealth())
                    this.indicatoriVita[i].setVisible(true);
                else this.indicatoriVita[i].setVisible(false);
            }
        }
    }

    /**
     * Metodo standard di generazione della mappa
     * @param i Posizione della riga da generare
     * @param j Posizione della colonna da generare
     * @return Restituisce true se deve essere posizionato un blocco, altrimenti restituisce false
     */
    public boolean metodoGenerazioneMappa(int i, int j){
        boolean esito = false;

        if(this.mappa != null){
            try{
                Method metodoGenerazioneMappa = this.mappa.getClass().getDeclaredMethod("metodoGenerazioneBase", int.class, int.class);
                esito = (boolean)metodoGenerazioneMappa.invoke(this.mappa, i, j);
            }catch (Exception e){e.printStackTrace();}
        }

        return esito;
    }

    /**
     * Disegna sullo schermo il punteggio del giocatore
     * @param screenWidht Larghezza dello schermo
     * @param screenHeight Altezza dello schermo
     * @param canvas Canvas di disegno
     * @param paint Paint di disegno
     */
    protected void disegnaPunteggio(int screenWidht, int screenHeight, Canvas canvas, Paint paint){
        float mezzoWidth = screenWidht * 0.5f;
        float YPos = this.OFFSET_SUPERIORE_PALLA * 0.5f;
        String messaggio = "P: " + this.status.getPunteggio();

        float lastSize = paint.getTextSize();

        paint.setTextSize(screenHeight * this.PERCENTUALE_DIMENSIONE_FONT);
        Rect bound = new Rect();
        paint.getTextBounds(messaggio, 0, messaggio.length(), bound);

        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(messaggio, mezzoWidth - (bound.right * 0.5f), YPos - (bound.bottom * 0.5f) + 25, paint);

        paint.setTextSize(lastSize);
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
     * Ripristina la posizione degli elementi postMorte
     */
    protected void ripristinaPosizionePostMorte(){
        this.palla.stopPalla();
        this.palla.setPosition(this.palla.getStartPosition());
        this.palla.calcolaDirezioneIniziale();
        this.paddle.setPosition(this.paddle.getStartPosition());
    }

    /**
     * Logica di terminazione della partita
     * Controlla le azioni e le condizioni che portano alla conclusione della partita
     */
    protected void logicaTerminazionePartita(){

    }

    /**
     * Logica di eliminazione della vita
     * Controlla le condizioni che portano alla rimozione di una vita al giocatore
     */
    protected void logicaEliminazioneVita(){
        if(this.palla != null){
            //Controllo della vita della partita

            if(this.palla.getPosition().getPosY() > this.owner.getCanvasHeight() * this.PERCENTUALE_DEATH_ZONE){
                //Se la palla si trova nella zona dello schermo identificata dalla deathzone
                if(this.status.getHealth() - 1 >= 0)
                    this.cambiaVitaAttuale(this.status.getHealth() - 1);

                this.ripristinaPosizionePostMorte();
            }
        }
    }

    /**
     * Logica di avanzamento al prossimo livello
     * Controlla le condizioni che portano al caricamento del prossimo livello da giocare
     */
    protected void logicaAvanzamentoLivello(){
        if(this.mappa.getTotalHealth() == 0 && this.status.getHealth() > 0){
            //Se la vita totale dei blocchi presenti nella scena è 0 allora il giocatore ha terminato il livello

            //Incrementa di 1 la vita del giocatore
            if(this.status.getHealth() + 1 <= this.status.getMaxHealth())
                this.cambiaVitaAttuale(this.status.getHealth() + 1);
            this.status.incrementaPunteggio(this.PUNTI_PER_PARTITA);

            //Incrementiamo o decrementiamo le caratteristiche di livello dei componenti
            this.paddle.setSpeed(this.paddle.getSpeed().prodottoPerScalare(this.stile.getDecrementoVelocitaPaddleLivello()));
            this.palla.setSpeed(this.palla.getSpeed().prodottoPerScalare(this.stile.getIncrementoVelocitaPallaLivello()));
            this.mappa.setVitaBlocchi(this.mappa.getVitaBlocchi() + this.stile.getTassoIncrementoVitaBlocchi());

            try{
                //Generiamo il prossimo livello della mappa
                this.mappa.generaMappa(this.getClass().getDeclaredMethod("metodoGenerazioneMappa", int.class, int.class), this);
                this.mappa.inserisciOstacoli(this.stile.getNumeroBlocchiIndistruttibili());

                //Aggiunge tutti i brick all'interno della scena, si presume che i precedenti siano stati eliminati quando si colpisce un blocco
                Brick brick = this.mappa.getNextBrick();
                while(brick != null){
                    this.addEntita(brick);
                    brick = this.mappa.getNextBrick();
                }
                this.mappa.azzeraContatori();
            }catch (Exception e){e.printStackTrace();}

            //Ripristina la posizione post mote
            this.ripristinaPosizionePostMorte();
        }
    }

    /**
     * Logica della partita
     */
    protected void logicaDiPartita(){
        this.logicaEliminazioneVita();
        this.logicaTerminazionePartita();
        this.logicaAvanzamentoLivello();
    }

    /**
     * Logica di gestione delle alterazioni
     * Rimozione delle alterazioni concluse e riposizionamento delle nuove alterazioni
     */
    protected void logicaAlterazioni(){
        float width = this.PERCENTUALE_DIMENSIONE_INDICATORI * this.owner.getCanvasWidht();
        float startX = this.owner.getCanvasWidht() - (width * 0.5f);               //La posizione X di start è quella estrema destra
        float startY = this.indicatoriVita[0].getPosition().getPosY();              //La posizione Y è la stessa degli indicatori di vita, che mantengono in proporzione la posizione essendo entità gestite dalla scena

        //Rimuove i powerup consumati e il relativo indicatore
        for(Iterator<PM> it = this.powerUpAttivi.iterator(); it.hasNext();){
            PM next = it.next();
            if(!next.getAlterazione().isAlterazioneAttiva()){
                it.remove();
                this.removeEntita(this.getFirstEntityByName(next.getName() + "Indicatore" + next.getId()));
            }
        }

        //Riposiziona i powerupAttivi
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
    }

    /**
     * Logica di rotazione della palla
     * @param dt Tempo trascorso tra gli ultimi frame
     */
    protected void logicaRotazionePalla(float dt){
        if(this.risorseCaricate && this.palla != null && this.palla.isMoving())
            this.palla.setRotazione(this.palla.getRotazione() + this.stile.getVelocitaRotazionePalla() * dt);
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

        this.mappa = new Map(
                this.stile.getNumeroRigheMappa(),
                this.stile.getNumeroColonneMappa(),
                this.stile.getPercentualePosizioneMappa().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getPercentualeDimensioneMappa().prodottoPerVettore(new Vector2D(screenWidth, screenHeight)),
                this.stile.getVitaInizialeBlocco(),
                this.stile.getImmagineBrickStile(this.owner),
                this.stile.getImmagineBrickIndistruttibileStile(this.owner),
                new MultiSprite(R.drawable.crepebrick, this.owner, 10)
        );
        this.mappa.inserisciOstacoli(stile.getNumeroBlocchiIndistruttibili());

        this.sfondo = new Sfondo(
                new Vector2D(screenWidth * 0.5f, screenHeight * 0.5f),
                new Vector2D(screenWidth, screenHeight),
                this.stile.getImmagineSfondoStile(this.owner)
        );

        //Generazione degli indicatori della vita
        this.indicatoriVita = new IndicatoreVita[this.status.getMaxHealth()];
        int width = (int)(this.PERCENTUALE_DIMENSIONE_INDICATORI * screenWidth);
        int startPosX = (int)( width * 0.5f );  //la posizione di start deve essere pari alla metà della larghezza calcolata, questo perchè lo sprite disegna partendo dal centro
        int startPosY = (int)( this.OFFSET_SUPERIORE_PALLA * 0.5f );

        for(int i = 0; i < this.indicatoriVita.length; i++){
            //Creazione degli indicatori della vita e incremento della larghezza per posizionari affianco su una singola riga
            this.indicatoriVita[i] = new IndicatoreVita(
                    new Vector2D(startPosX, startPosY),
                    new Vector2D(width, width),
                    new AnimatedSprite(R.drawable.indicatori_vita, this.owner, 4, (int)( 5 + Math.random() * 5 ))
            );
            startPosX += width;
        }

        //Caricamento dell'audio
        AudioUtil.clear();
        AudioUtil.loadAudio("background", R.raw.background1, this.owner.getContext());
    }

    /**
     * Aggiunge le entità alla scena
     */
    protected void addEntitaScena(){
        //Cancellazione di eventuali entita presenti e setup
        this.entita.clear();
        this.addEntita(this.sfondo);

        //Aggiunta dei brick
        Brick brick = this.mappa.getNextBrick();
        while(brick != null){
            this.addEntita(brick);
            brick = this.mappa.getNextBrick();
        }
        this.mappa.azzeraContatori();

        //Aggiunta della palla e paddle
        this.addEntita(this.palla);
        this.addEntita(this.paddle);

        //Aggiunta degli indicatori della vita
        for(IndicatoreVita iv : this.indicatoriVita)
            this.addEntita(iv);
    }

    @Override
    public void setup(int screenWidth, int screenHeight) {
        this.risorseCaricate = false;
        this.inizializzaRisorse(screenWidth, screenHeight);
        this.addEntitaScena();

        //Avvia l'audio di background
        AudioUtil.getMediaPlayer("background").setLooping(true);
        AudioUtil.getMediaPlayer("background").start();

        this.risorseCaricate = true;
    }

    @Override
    public void update(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(this.risorseCaricate){
            super.update(dt, screenWidth, screenHeight, canvas, paint);
            this.logicaRotazionePalla(dt);
            this.logicaAlterazioni();
            this.logicaDiPartita();
        }
    }

    @Override
    public void render(float dt, int screenWidth, int screenHeight, Canvas canvas, Paint paint) {
        if(this.risorseCaricate){
            super.render(dt, screenWidth, screenHeight, canvas, paint);
            this.disegnaPunteggio(screenWidth, screenHeight, canvas, paint);
        }
    }

    /**
     * Evento usato quando la palla colpisce un blocco
     * @param paramList Lista dei parametri
     */
    protected void eventoColpoBlocco(ParamList paramList){
        this.status.incrementaPunteggio(this.PUNTI_PER_COLPO);
    }

    /**
     * Evento usato quando la palla rompe un blocco
     * @param parametri Lista dei parametri
     */
    protected void eventoRotturaBlocco(ParamList parametri){
        Brick brick = parametri.get("brick");
        if(brick != null){
            //Distrugge il blocco e inserisce delle particelle
            for(int i = 0; i < this.PARTICELLE_ROTTURA_BLOCCO; i++){
                this.addEntita(new Particella(
                        brick.getPosition(),
                        new Vector2D(5, 5),
                        Color.GRAY,
                        1500
                ));
            }

            this.removeEntita(brick);

            if(this.pmList != null){
                //Se la lista dei powerup non è vuota
                PM powerup = this.pmList.getPowerup(
                        brick.getPosition(),
                        new Vector2D(
                                this.owner.getCanvasWidht() * this.PERCENTUALE_DIMENSIONE_POWERUP,
                                this.owner.getCanvasWidht() * this.PERCENTUALE_DIMENSIONE_POWERUP
                        ),
                        this.owner
                );

                //Aggiunge il powerup
                if(powerup != null)
                    this.addEntita(powerup);
            }
        }
    }

    /**
     * Evento di rimozione di una particella
     * @param parametri Parametri passati
     */
    protected void eventoRimozioneParticella(ParamList parametri){
        Entity e = parametri.get("particella");
        if(e != null)
            this.removeEntita(e);
    }

    /**
     * Evento di rimozione dell'entità fisica powerup
     * @param parametri Parametri passati
     */
    protected void eventoRimozionePowerup(ParamList parametri){
        PM pm = parametri.get("powerup");
        if(pm != null)
            this.removeEntita(pm);
    }

    /**
     * Evento di raccolta del powerup
     * @param parametri Parametri passati
     */
    protected void eventoPowerup(ParamList parametri){
        //Rimuove l'entità dalla scena
        PM pm = parametri.get("powerup");
        if(pm != null){
            pm.getAlterazione().attivaAlterazione(this.status, this.creaParametriAlterazioni());    //Attiviamo l'alterazione
            this.eventoRimozionePowerup(parametri); //Rimuoviamo l'entità fisica del powerup

            //Aggiunge l'immagine del powerup e l'indicatore
            this.powerUpAttivi.add(pm);
            float widthIndicatore = this.owner.getCanvasWidht() * this.PERCENTUALE_DIMENSIONE_INDICATORI;
            this.addEntita(
                    pm.getIndicatorePM(
                        new Vector2D(widthIndicatore, widthIndicatore)
                    )
            );
        }
    }

    @Override
    public void sendEvent(String idEvent, ParamList parametri) {
        if(idEvent.equals(ModalitaClassica.EVENTO_BLOCCO_COLPITO))
            this.eventoColpoBlocco(parametri);

        if(idEvent.equals(ModalitaClassica.EVENTO_BLOCCO_ROTTO))
            this.eventoRotturaBlocco(parametri);

        if(idEvent.equals(ModalitaClassica.EVENTO_RIMOZIONE_PARTICELLA))
            this.eventoRimozioneParticella(parametri);

        if(idEvent.equals(ModalitaClassica.EVENTO_RIMOZIONE_POWERUP))
            this.eventoRimozionePowerup(parametri);

        if(idEvent.equals(ModalitaClassica.EVENTO_POWERUP))
            this.eventoPowerup(parametri);
    }

    //Eventi

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(this.palla != null && !this.palla.isMoving() && event.getAction() == MotionEvent.ACTION_UP)
            this.palla.startPalla();

        if(this.status != null && this.status.getModalitaControllo() == GameStatus.TOUCH){
            if(this.paddle != null){
                float target = event.getX() * (this.owner.getCanvasWidht() / (float)v.getWidth());
                this.paddle.setTargetX(target);
            }
        }

        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(this.status.getModalitaControllo() == GameStatus.GYRO && event.sensor.getType() == Sensor.TYPE_GYROSCOPE && this.owner != null){
            //Cambio del valore sul giroscopio
            if(this.sensoreTimeStamp != -1){
                //Se è stato osservato una volta il cambiamento del sensore
                float dt = (System.currentTimeMillis() - this.sensoreTimeStamp) / 1000.0f;
                float spostamento = (event.values[2] * dt * 180) / (float)Math.PI;    //Calcolato in gradi, velocitaZ + dt è in radianti

                float ultimaRotazione = this.rotazioneAsseZ;
                this.rotazioneAsseZ += spostamento;
                if(this.owner.getResources().getConfiguration().orientation != this.ultimoOrientamentoConosciuto){
                    //Se lo schermo ha cambiato orientamento dall'ultima volta, cambiamo l'angolo di rotazione di +-90 in base alla rotazione
                    //Questo valore è usato per capire dove localizzare il punto 0 della misurazione
                    if(ultimaRotazione > this.rotazioneAsseZ)
                        this.incrementoRotazioneAngolo -= 90;
                    else if(ultimaRotazione < this.rotazioneAsseZ)
                        this.incrementoRotazioneAngolo += 90;

                    this.ultimoOrientamentoConosciuto = this.owner.getResources().getConfiguration().orientation;
                }

                //Calcolo dell'angolo relativo, in modo tale che in base all'orientamento se il dispositivo è in posizione di riposo, l'angolo risulti 0
                //TODO: attenzione, si rompe tutto se si ritorna in landscape quando si ruota il telefono oltre i 180°, speriamo che non capit, ma al massimo inseriamo il reset della posizione
                this.angoloRelativo = this.rotazioneAsseZ - (this.incrementoRotazioneAngolo + this.puntoZeroAsseZ);

                //Calcolo della posizione della paddle
                int larghezzaMezzi = this.owner.getCanvasWidht() / 2;
                float peso = this.angoloRelativo / this.INTERVALLO_MOVIMENTO_GIROSCOPIO_MASSIMO;    //Con questo ci viene restituito un valore che va da 0 a 1(Se facciamo modifiche sotto)
                if(Math.abs(peso) > 1)
                    peso = peso / Math.abs(peso);   //Portiamo a 1 il peso mantenendo il segno

                float posizionePaddle = larghezzaMezzi - (peso * larghezzaMezzi);
                this.paddle.setTargetX(posizionePaddle);
            }

            this.sensoreTimeStamp = System.currentTimeMillis();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

}