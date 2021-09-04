package com.example.android.arkanoid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.arkanoid.GameCore.GameLoop;
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

import java.lang.reflect.Constructor;

public class modalita_activity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modalita);

        this.caricaRiferimentoView();

        if(savedInstanceState != null){
            this.difficultyButtonIndex = savedInstanceState.getInt("DIFFICULTY_BUTTON_INDEX", 0);
            this.controlButtonIndex = savedInstanceState.getInt("CONTROL_BUTTON_INDEX", 0);
            this.codiceModalita = savedInstanceState.getInt("MODE", 0);
            if(modalita_activity.gameLoop != null && this.containerModalita != null){
                System.out.println("Ciao");
                this.containerModalita.setVisibility(View.VISIBLE);
                this.containerModalita.addView(modalita_activity.gameLoop);
            }

        }else{
            this.difficultyButtonIndex = 0;
            this.controlButtonIndex = 0;
            if(this.getIntent() != null)
                this.codiceModalita = this.getIntent().getIntExtra("MODE", 0);
            else this.codiceModalita = 0;
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
    protected void onResume() {
        super.onResume();
        if(modalita_activity.gameLoop != null)
            modalita_activity.gameLoop.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(modalita_activity.gameLoop != null){
            if(modalita_activity.gameLoop.isRunning())
                modalita_activity.gameLoop.stop();

            if(this.containerModalita != null) {
                this.containerModalita.removeView(modalita_activity.gameLoop);
                System.out.println("Rimossa");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("MODE", this.codiceModalita);
        outState.putInt("DIFFICULTY_BUTTON_INDEX", this.difficultyButtonIndex);
        outState.putInt("CONTROL_BUTTON_INDEX", this.controlButtonIndex);
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
    protected void caricaModalita(){
        Stile stile = this.generaStile();
        GameStatus status = this.generaGameStatus();
        PMList pmList = this.generaPM();
        Class<? extends  ModalitaClassica> classe = this.getModalita();

        if(classe != null){
            try{
                Constructor<? extends ModalitaClassica> costruttore = classe.getConstructor(Stile.class, GameStatus.class, PMList.class);
                modalita_activity.modalita = costruttore.newInstance(stile, status, pmList);
                modalita_activity.gameLoop = new GameLoop(this, 60, 720, 1280);

                if(this.containerModalita != null){
                    //Aggiungiamo nella vista il gameLoop ed inseriamo la modalita
                    this.containerModalita.setVisibility(View.VISIBLE);
                    this.containerModalita.addView(modalita_activity.gameLoop);

                    modalita_activity.gameLoop.start();
                    modalita_activity.gameLoop.addGameComponentWithSetup(modalita_activity.modalita);
                }
            }catch (Exception e){e.printStackTrace();}
        }
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
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}