package com.example.android.arkanoid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.FragmentMenu.game_over_fragment;
import com.example.android.arkanoid.FragmentMenu.pausa_fragment;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverInterface;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.PMList;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BallSpeedDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BallSpeedUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.BrickHealthUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.MultiBall;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleSpeedDown;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleSpeedUp;
import com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti.PaddleUp;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;
import com.example.android.arkanoid.Util.AudioUtil;

import java.lang.reflect.Constructor;

public class modalita_activity extends MultiFragmentActivity implements View.OnClickListener, GameOverInterface {
    public static final int CODICE_MODALITA_CLASSICA = 0;                           //Codice per avviare la modalità classica
    private final float[] MOLTIPLICATORI_PER_DIFFICOLTA = {0.8f, 1, 1.2f};          //Moltiplicatori per le difficoltà

    //----------------------------------------------------------//

    //Campi statici per il mantenimento dello stato della partita
    private static GameLoop gameLoop;                  //GameLoop
    private static ModalitaClassica modalita;          //Modalita caricata
    //----------------------------------------------------------//

    private ToggleButton easyButton;
    private ToggleButton normalButton;
    private ToggleButton hardButton;
    private int difficultyButtonIndex;
    private ToggleButton touchButton;
    private ToggleButton gyroButton;
    private int controlButtonIndex;
    private int codiceModalita;
    private TextView labelModalita;
    private Button startButton;

    private FrameLayout containerModalita;      //Container della modalità eseguita

    private boolean inPause;                    //Flag per il controllo della pause
    private boolean inGame;                     //Flag per il controllo del game
    private boolean gameOver;                   //Flag per il controllo del gameOver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modalita);
        this.loadEssentials(savedInstanceState);
        if(savedInstanceState != null){
            //Ripristiniamo lo stato della vista
            this.difficultyButtonIndex = savedInstanceState.getInt("DIFFICULTY_BUTTON_INDEX", 0);
            this.controlButtonIndex = savedInstanceState.getInt("CONTROL_BUTTON_INDEX", 0);
            this.codiceModalita = savedInstanceState.getInt("MODE", 0);
            this.inGame = savedInstanceState.getBoolean("IN_GAME");
            this.inPause = savedInstanceState.getBoolean("IN_PAUSE");
            this.gameOver = savedInstanceState.getBoolean("GAME_OVER");
            if(this.inGame && this.containerModalita != null){
                this.containerModalita.setVisibility(View.VISIBLE);
                this.aggiungiGameLoop();

                if(this.inPause)
                    this.mostraMenuPausa();
            }
        }else{
            //Inizializzazione della vista
            this.difficultyButtonIndex = 0;
            this.controlButtonIndex = 0;
            if(this.getIntent() != null)
                this.codiceModalita = this.getIntent().getIntExtra("MODE", 0);
            else this.codiceModalita = 0;
            this.inGame = false;
            this.inPause = false;
            this.gameOver = false;
        }

        //Inizializza l'aspetto grafico
        this.impostaNomeModalita(this.codiceModalita);
        this.illuminaPulsanteDifficolta(this.difficultyButtonIndex);
        this.illuminaPulsanteControllo(this.controlButtonIndex);
    }

    /**
     * Carica il riferimento alle view presenti all'interno dell'activity
     */
    protected void caricaRiferimentoView(){
        //Pulsanti della difficoltà
        this.easyButton = this.findViewById(R.id.difficoltaFacileButton);
        if(this.easyButton != null)
            this.easyButton.setOnClickListener(this);
        this.normalButton = this.findViewById(R.id.difficoltaNormaleButton);
        if(this.normalButton != null)
            this.normalButton.setOnClickListener(this);
        this.hardButton = this.findViewById(R.id.difficoltaDifficileButton);
        if(this.hardButton != null)
            this.hardButton.setOnClickListener(this);

        //Pulsanti per il controllo
        this.touchButton = this.findViewById(R.id.touchButton);
        if(this.touchButton != null)
            this.touchButton.setOnClickListener(this);
        this.gyroButton = this.findViewById(R.id.gyroButton);
        if(this.gyroButton != null)
            this.gyroButton.setOnClickListener(this);

        //Pulsante di start
        this.startButton = this.findViewById(R.id.startButton);
        if(this.startButton != null)
            this.startButton.setOnClickListener(this);

        //Label per la modalita
        this.labelModalita = this.findViewById(R.id.nomeModalitaLabel);

        //Container delle modalita
        this.containerModalita = this.findViewById(R.id.containerModalita);
        this.containerModalita.setVisibility(View.GONE);
        if(this.containerModalita != null)
            this.containerModalita.setOnTouchListener(this);


    }

    @Override
    protected void loadEssentials(Bundle savedInstanceState) {
        //Viene caricato il fragment layout e il frame di contrasto
        this.caricaRiferimentoView();
        this.loadFragmentLayout(R.id.containerFragment);
        this.loadFrameContrasto(R.id.frameContrasto);
        super.loadEssentials(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(this.inGame) {
            modalita_activity.gameLoop.start();
            if(this.containerModalita != null) {
                //Reinseriamo il gameLoop e rendiamo visibile il container della modalità
                this.containerModalita.setVisibility(View.VISIBLE);
                this.aggiungiGameLoop();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(this.inGame){
            //Se la partita è stata avviata ferma il gameloop
            if(this.inGame)
                modalita_activity.gameLoop.stop();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("MODE", this.codiceModalita);
        outState.putInt("DIFFICULTY_BUTTON_INDEX", this.difficultyButtonIndex);
        outState.putInt("CONTROL_BUTTON_INDEX", this.controlButtonIndex);
        outState.putBoolean("IN_PAUSE", this.inPause);
        outState.putBoolean("IN_GAME", this.inGame);
        outState.putBoolean("GAME_OVER", this.gameOver);
    }

    /**
     * Illumina il pulsante corretto per la difficoltà
     * @param difficultyButtonIndex Pulsante di difficoltà selezionato
     */
    private void illuminaPulsanteDifficolta(int difficultyButtonIndex){
        if(this.easyButton != null && this.normalButton != null && this.hardButton != null){
            //Se i pulsanti non sono vuoti

            switch(difficultyButtonIndex){
                case 0:
                    this.easyButton.setChecked(true);
                    this.normalButton.setChecked(false);
                    this.hardButton.setChecked(false);
                    break;
                case 1:
                    this.easyButton.setChecked(false);
                    this.normalButton.setChecked(true);
                    this.hardButton.setChecked(false);
                    break;
                case 2:
                    this.easyButton.setChecked(false);
                    this.normalButton.setChecked(false);
                    this.hardButton.setChecked(true);
                    break;
                default:
                    this.easyButton.setChecked(false);
                    this.normalButton.setChecked(false);
                    this.hardButton.setChecked(false);
                    break;
            }
        }
    }

    /**
     * Illumina il pulsante di controllo
     * @param controlButtonIndex Indice del pulsante di controllo
     */
    private void illuminaPulsanteControllo(int controlButtonIndex){
        if(this.touchButton != null && this.gyroButton != null){
            //Se i pulsanti non sono vuoti

            switch(controlButtonIndex){
                case 0:
                    this.touchButton.setChecked(true);
                    this.gyroButton.setChecked(false);
                    break;
                case 1:
                    this.touchButton.setChecked(false);
                    this.gyroButton.setChecked(true);
                    break;
                default:
                    this.touchButton.setChecked(false);
                    this.gyroButton.setChecked(false);
                    break;
            }
        }
    }

    /**
     * Imposta il nome della modalià
     * @param codiceModalita Codice della modalita
     */
    private void impostaNomeModalita(int codiceModalita){
        if(this.labelModalita != null){
            switch (codiceModalita){
                case modalita_activity.CODICE_MODALITA_CLASSICA:
                    this.labelModalita.setText(this.getResources().getText(R.string.fragment_selezione_modalita_modalita_classica));
                    break;
                default:
                    this.labelModalita.setText(this.getResources().getText(R.string.fragment_selezione_modalita_modalita_classica));
                    break;
            }
        }
    }

    /**
     * Genera lo stile per la modalità da eseguire
     * @return Restituisce lo stile
     */
    private Stile generaStile(){
        Stile base = new Stile();
        switch ((int)Math.round( Math.random() * 3 )){
            case 1:
                base = new StileAtzeco();
                break;
            case 2:
                base = new StileFuturistico();
                break;
            case 3:
                base = new StileSpaziale();
                break;
        }

        float moltiplicatore = this.MOLTIPLICATORI_PER_DIFFICOLTA[this.difficultyButtonIndex];

        base.setVelocitaInizialePalla(base.getVelocitaInizialePalla() * moltiplicatore);
        base.setVelocitaInizialePaddle(base.getVelocitaInizialePaddle() * moltiplicatore);

        base.setIncrementoVelocitaPallaLivello(base.getIncrementoVelocitaPallaLivello() * moltiplicatore);
        base.setDecrementoVelocitaPaddleLivello(base.getDecrementoVelocitaPaddleLivello() * moltiplicatore);
        base.setNumeroBlocchiIndistruttibili(this.difficultyButtonIndex * 2);

        return base;
    }

    /**
     * Genera il gameStatus per la modalita da eseguire
     * @return Restituisce il gameStatus
     */
    private GameStatus generaGameStatus(){
        int health = 5 - this.difficultyButtonIndex;
        int punteggio = 0;
        int modalitaInput = this.controlButtonIndex == 0 ? GameStatus.TOUCH : GameStatus.GYRO;

        return new GameStatus(health, punteggio, modalitaInput);
    }

    /**
     * Genera la lista dei powerup e dei malus
     * @return Restituisce la lista di powerup e malus
     */
    protected PMList generaPM(){
        PMList powerupList = new PMList();

        powerupList.addPowerupMalus(BallSpeedUp.class, 10);
        powerupList.addPowerupMalus(BallSpeedDown.class, 5);
        powerupList.addPowerupMalus(MultiBall.class, 2);
        powerupList.addPowerupMalus(BrickHealthUp.class, 20);
        powerupList.addPowerupMalus(PaddleUp.class, 10);
        powerupList.addPowerupMalus(PaddleDown.class, 5);
        powerupList.addPowerupMalus(PaddleSpeedUp.class, 10);
        powerupList.addPowerupMalus(PaddleSpeedDown.class, 5);

        return powerupList;
    }

    /**
     * Restituisce la classe della modalità che deve essere caricata
     * @return Restituisce la classe della modalità oppure null
     */
    protected Class<? extends ModalitaClassica> getModalita(){
        Class<? extends ModalitaClassica> esito = null;

        switch (this.codiceModalita){
            case 0:
                esito = ModalitaClassica.class;
                break;
            default:
                esito = ModalitaClassica.class;
                break;
        }

        return esito;
    }

    /**
     * Operazione di caricamento della modalità
     */
    public void caricaModalita(){
        Stile stile = this.generaStile();
        GameStatus status = this.generaGameStatus();
        PMList pmList = this.generaPM();
        Class<? extends  ModalitaClassica> classe = this.getModalita();

        if(classe != null){
            try{
                Constructor<? extends ModalitaClassica> costruttore = classe.getConstructor(Stile.class, GameStatus.class, PMList.class);
                modalita_activity.modalita = costruttore.newInstance(stile, status, pmList);
                modalita_activity.modalita.setGameOverInterface(this);
                if(modalita_activity.gameLoop != null)
                    modalita_activity.gameLoop.stop();
                modalita_activity.gameLoop = new GameLoop(this, 60, 720, 1280);

                if(this.containerModalita != null){
                    //Aggiungiamo nella vista il gameLoop ed inseriamo la modalita
                    this.containerModalita.setVisibility(View.VISIBLE);
                    this.containerModalita.addView(modalita_activity.gameLoop);

                    modalita_activity.gameLoop.start();
                    modalita_activity.gameLoop.setShowFPS(true);
                    modalita_activity.gameLoop.addGameComponent(modalita_activity.modalita);
                    this.inGame = true;
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Aggiunge il gameLoop al container della modalita
     */
    protected void aggiungiGameLoop(){
        if(modalita_activity.gameLoop != null){
            if(modalita_activity.gameLoop.getParent() != null){
                ((ViewGroup)modalita_activity.gameLoop.getParent()).removeView(modalita_activity.gameLoop);
            }
            if(this.containerModalita != null)
                this.containerModalita.addView(modalita_activity.gameLoop);
        }
    }

    /**
     * Mostra il menu di GameOVer
     */
    protected void mostraMenuGameOver(){
        if(this.inGame && !this.gameOver){
            this.showFragment(game_over_fragment.class, true);
            this.gameOver = true;
            modalita_activity.gameLoop.setUpdateRunning(false);
        }
    }

    /**
     * Nasconde il menu di GameOver
     */
    public void nascondiMenuGameOver(){
        if(this.inGame && this.gameOver){
            this.hideFragment(true);
            this.gameOver = false;
            modalita_activity.gameLoop.setUpdateRunning(false);
        }
    }

    /**
     * Mostra il menu di pausa
     */
    protected void mostraMenuPausa(){
        if(this.inGame && !this.inPause){
            this.showFragment(pausa_fragment.class, true);
            this.inPause = true;
            modalita_activity.gameLoop.setUpdateRunning(false);
        }
    }

    /**
     * Nasconde il menu di pasua
     */
    public void nascondiMenuPausa(){
        if(this.inPause && this.inGame){
            this.hideFragment(true);
            this.inPause = false;
            modalita_activity.gameLoop.setUpdateRunning(true);
        }
    }

    /**
     * Torna al menu principale
     */
    public void tornaAlMenu(){
        Intent intent = new Intent(this, main_menu_activity.class);
        //Ripristina lo stato dell'audio
        AudioUtil.setGlobalAudio(100);
        AudioUtil.clear();
        AudioUtil.loadAudio("background_music", R.raw.background_music, this);
        AudioUtil.getMediaPlayer("background_music").setLooping(true);
        AudioUtil.getMediaPlayer("background_music").start();
        this.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.easyButton)){
            this.difficultyButtonIndex = 0;
            this.illuminaPulsanteDifficolta(this.difficultyButtonIndex);
        }

        if(v.equals(this.normalButton)){
            this.difficultyButtonIndex = 1;
            this.illuminaPulsanteDifficolta(this.difficultyButtonIndex);
        }

        if(v.equals(this.hardButton)){
            this.difficultyButtonIndex = 2;
            this.illuminaPulsanteDifficolta(this.difficultyButtonIndex);
        }

        if(v.equals(this.touchButton)){
            this.controlButtonIndex = 0;
            this.illuminaPulsanteControllo(this.controlButtonIndex);
        }

        if(v.equals(this.gyroButton)) {
            this.controlButtonIndex = 1;
            this.illuminaPulsanteControllo(this.controlButtonIndex);
        }

        if(v.equals(this.startButton))
            this.caricaModalita();
    }

    @Override
    protected void onFrameContrastoTouched(View v, MotionEvent e) {
        if(this.inPause)
            this.nascondiMenuPausa();
    }

    @Override
    public void onBackPressed() {
        if(this.inGame){
            if(!this.gameOver){
                if(!this.inPause)
                    this.mostraMenuPausa();
                else this.nascondiMenuPausa();
            }
        }else{
            Intent intent = new Intent(this, main_menu_activity.class);
            this.startActivity(intent);
        }
    }

    @Override
    public void gameOver(GameStatus status) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Mostriamo il menu e impostiamo il punteggio
                mostraMenuGameOver();
                if(fragmentAttivo != null){
                    ((game_over_fragment)fragmentAttivo).setRiepilogoPunti(status.getPunteggio());
                }
            }
        });

    }
}