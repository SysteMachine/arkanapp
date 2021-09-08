package com.example.android.arkanoid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.FragmentMenu.game_over_fragment;
import com.example.android.arkanoid.FragmentMenu.pausa_fragment;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverListener;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.SceneDefinite.AbstractModalita;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaClassica;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;
import com.example.android.arkanoid.Util.AudioUtil;

import java.lang.reflect.Constructor;

public class modalita_activity extends MultiFragmentActivity implements View.OnClickListener, GameOverListener, Runnable {
    public static String EXTRA_MODALITA = "MODALITA";

    public static final int CODICE_MODALITA_CLASSICA = 0;                           //Codice per avviare la modalità classica
    private final float[] MOLTIPLICATORI_PER_DIFFICOLTA = {0.8f, 1, 1.2f};          //Moltiplicatori per le difficoltà

    public static AbstractModalita modalita;                                       //Modalita caricata

    private TextView labelModalita;
    private ToggleButton modalitaFacileButton;
    private ToggleButton modalitaNormaleButton;
    private ToggleButton modalitaDifficileButton;
    private ToggleButton touchButton;
    private ToggleButton gyroButton;
    private Button startButton;

    private FrameLayout containerModalita;                                          //Container della modalità eseguita
    private GameLoop gameLoop;                                                      //GameLoop di esecuzione

    private int difficoltaSelezionata;                                              //Difficoltà di gioco selezionata
    private int modalitaControllo;                                                  //Modalità di controllo selezionata
    private int codiceModalitaSelezionata;                                          //Codice della modalita selezionata

    private boolean inPause;                                                        //Flag per il controllo della pausa
    private boolean inGame;                                                         //Flag per il controllo della partenza del gioco
    private boolean gameOver;                                                       //Flag per il controllo del gameOver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modalita);

        this.difficoltaSelezionata = 0;
        this.modalitaControllo = 0;
        this.codiceModalitaSelezionata = 0;
        this.inGame = false;
        this.inPause = false;
        this.gameOver = false;
        if(this.getIntent() != null)
            this.codiceModalitaSelezionata = this.getIntent().getIntExtra(modalita_activity.EXTRA_MODALITA, 0);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.difficoltaSelezionata = savedInstanceState.getInt("difficoltaSelezionata", 0);
        this.modalitaControllo = savedInstanceState.getInt("modalitaControllo", 0);
        this.codiceModalitaSelezionata = savedInstanceState.getInt("codiceModalitaSelezionata", 0);
        this.inGame = savedInstanceState.getBoolean("inGame");
        this.inPause = savedInstanceState.getBoolean("inPause");
        this.gameOver = savedInstanceState.getBoolean("gameOver");
    }

    @Override
    protected void onResume() {
        super.onResume();
        caricaRiferimentoView();
        this.impostaStatoComponentiGrafiche();

        this.gameLoop = new GameLoop(this, 60, 720, 1280);
        if(this.containerModalita != null)
            this.containerModalita.addView(this.gameLoop);
        this.gameLoop.start();

        this.ripristinaUltimoStato();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gameLoop.stop();
        if(this.containerModalita != null)
            this.containerModalita.removeView(this.gameLoop);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("codiceModalitaSelezionata", this.codiceModalitaSelezionata);
        outState.putInt("difficoltaSelezionata", this.difficoltaSelezionata);
        outState.putInt("modalitaControllo", this.modalitaControllo);
        outState.putBoolean("inPause", this.inPause);
        outState.putBoolean("inGame", this.inGame);
        outState.putBoolean("gameOver", this.gameOver);
    }


    /**
     * Carica il riferimento alle view presenti all'interno dell'activity
     */
    private void caricaRiferimentoView(){
        this.labelModalita = this.findViewById(R.id.nomeModalitaLabel);
        this.modalitaFacileButton = this.findViewById(R.id.difficoltaFacileButton);
        this.modalitaNormaleButton = this.findViewById(R.id.difficoltaNormaleButton);
        this.modalitaDifficileButton = this.findViewById(R.id.difficoltaDifficileButton);
        this.gyroButton = this.findViewById(R.id.gyroButton);
        this.touchButton = this.findViewById(R.id.touchButton);
        this.startButton = this.findViewById(R.id.startButton);

        if(this.modalitaFacileButton  != null)
            this.modalitaFacileButton .setOnClickListener(this);
        if(this.modalitaNormaleButton != null)
            this.modalitaNormaleButton.setOnClickListener(this);
        if(this.modalitaDifficileButton != null)
            this.modalitaDifficileButton.setOnClickListener(this);
        if(this.touchButton != null)
            this.touchButton.setOnClickListener(this);
        if(this.gyroButton != null)
            this.gyroButton.setOnClickListener(this);
        if(this.startButton != null)
            this.startButton.setOnClickListener(this);

        this.containerModalita = this.findViewById(R.id.containerModalita);
    }

    /**
     * Ripristina l'ultimo stato del gioco
     */
    private void ripristinaUltimoStato(){
        if(this.inGame && this.gameLoop != null){
            if(this.containerModalita != null)
                this.containerModalita.setVisibility(View.VISIBLE);
            if(modalita_activity.modalita != null)
                this.gameLoop.addGameComponentNoSetup(modalita_activity.modalita);
            if(this.inPause || this.gameOver)
                this.gameLoop.setUpdateRunning(false);
        }
    }

    /**
     * Imposta lo stato delle componenti grafiche sulla base dei parametri
     */
    private void impostaStatoComponentiGrafiche(){
        if(this.modalitaFacileButton != null && this.modalitaNormaleButton != null && this.modalitaDifficileButton != null){
            switch(this.difficoltaSelezionata){
                case 0:
                    this.modalitaFacileButton.setChecked(true);
                    this.modalitaNormaleButton.setChecked(false);
                    this.modalitaDifficileButton.setChecked(false);
                    break;
                case 1:
                    this.modalitaFacileButton.setChecked(false);
                    this.modalitaNormaleButton.setChecked(true);
                    this.modalitaDifficileButton.setChecked(false);
                    break;
                case 2:
                    this.modalitaFacileButton.setChecked(false);
                    this.modalitaNormaleButton.setChecked(false);
                    this.modalitaDifficileButton.setChecked(true);
                    break;
            }
        }

        if(this.touchButton != null && this.gyroButton != null){
            switch(this.modalitaControllo){
                case 0:
                    this.touchButton.setChecked(true);
                    this.gyroButton.setChecked(false);
                    break;
                case 1:
                    this.touchButton.setChecked(false);
                    this.gyroButton.setChecked(true);
                    break;
            }
        }

        if(this.labelModalita != null){
            switch (this.codiceModalitaSelezionata){
                case modalita_activity.CODICE_MODALITA_CLASSICA:
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
        Stile stile = new Stile();
        switch ((int)Math.round( Math.random() * 3 )){
            case 1:
                stile = new StileAtzeco();
                break;
            case 2:
                stile = new StileFuturistico();
                break;
            case 3:
                stile = new StileSpaziale();
                break;
        }

        float moltiplicatore = this.MOLTIPLICATORI_PER_DIFFICOLTA[this.difficoltaSelezionata];

        stile.setVelocitaInizialePalla(stile.getVelocitaInizialePalla() * moltiplicatore);
        stile.setVelocitaInizialePaddle(stile.getVelocitaInizialePaddle() * moltiplicatore);
        stile.setIncrementoVelocitaPallaLivello(stile.getIncrementoVelocitaPallaLivello() * moltiplicatore);
        stile.setDecrementoVelocitaPaddleLivello(stile.getDecrementoVelocitaPaddleLivello() * moltiplicatore);
        stile.setNumeroBlocchiIndistruttibili(this.difficoltaSelezionata * 2);

        return stile;
    }

    /**
     * Genera il gameStatus per la modalita da eseguire
     * @return Restituisce il gameStatus
     */
    private GameStatus generaGameStatus(){
        int health = 5 - this.difficoltaSelezionata;
        int modalitaInput = this.modalitaControllo == 0 ? GameStatus.TOUCH : GameStatus.GYRO;

        return new GameStatus(health, 0, modalitaInput);
    }

    /**
     * Restituisce la classe della modalità che deve essere caricata
     * @return Restituisce la classe della modalità oppure null
     */
    private Class<? extends AbstractModalita> getClasseModalita(){
        Class<? extends AbstractModalita> classeModalita = null;

        switch (this.codiceModalitaSelezionata){
            case 0:
                classeModalita = ModalitaClassica.class;
                break;
        }

        return classeModalita;
    }

    /**
     * Operazione di caricamento della modalità
     */
    public void caricaModalita(){
        Class<? extends  AbstractModalita> classe = this.getClasseModalita();
        if(classe != null){
            try{
                Constructor<? extends AbstractModalita> costruttore = classe.getConstructor(Stile.class, GameStatus.class);
                modalita_activity.modalita = costruttore.newInstance(this.generaStile(), this.generaGameStatus());
                modalita_activity.modalita.setGameOverListener(this);

                if(this.containerModalita != null && this.gameLoop != null){
                    this.containerModalita.setVisibility(View.VISIBLE);
                    this.nascondiMenuGameOver();
                    this.nascondiMenuPausa();
                    this.gameLoop.removeAll();
                    this.gameLoop.setShowFPS(true);
                    this.gameLoop.addGameComponent(modalita_activity.modalita);
                    this.inGame = true;
                }
            }catch (Exception e){e.printStackTrace();}
        }
    }

    /**
     * Mostra il menu di GameOVer
     */
    public void mostraMenuGameOver(){
        if(this.inGame && !this.gameOver){
            this.nascondiFragment(false);
            this.mostraFragment(new game_over_fragment(), true);
            if(this.gameLoop != null)
                this.gameLoop.setUpdateRunning(false);
            this.gameOver = true;
        }
    }

    /**
     * Nasconde il menu di GameOver
     */
    public void nascondiMenuGameOver(){
        if(this.inGame && this.gameOver){
            this.nascondiFragment(true);
            if(this.gameLoop != null)
                this.gameLoop.setUpdateRunning(true);
            this.gameOver = false;
        }
    }

    /**
     * Mostra il menu di pausa
     */
    public void mostraMenuPausa(){
        if(this.inGame && !this.inPause && !this.gameOver){
            this.mostraFragment(new pausa_fragment(), true);
            if(this.gameLoop != null)
                this.gameLoop.setUpdateRunning(false);
            this.inPause = true;
        }
    }

    /**
     * Nasconde il menu di pasua
     */
    public void nascondiMenuPausa(){
        if(this.inPause && this.inGame){
            this.nascondiFragment(true);
            if(this.gameLoop != null)
                this.gameLoop.setUpdateRunning(true);
            this.inPause = false;
        }
    }

    /**
     * Torna al menu principale
     */
    public void tornaAlMenu(){
        Intent intent = new Intent(this, main_menu_activity.class);
        AudioUtil.setGlobalAudio(100);
        AudioUtil.clear();
        AudioUtil.loadAudio("background_music", R.raw.background_music, this);
        AudioUtil.getMediaPlayer("background_music").setLooping(true);
        AudioUtil.getMediaPlayer("background_music").start();
        this.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.modalitaFacileButton))
            this.difficoltaSelezionata = 0;
        if(v.equals(this.modalitaNormaleButton))
            this.difficoltaSelezionata = 1;
        if(v.equals(this.modalitaDifficileButton))
            this.difficoltaSelezionata = 2;
        if(v.equals(this.touchButton))
            this.modalitaControllo = 0;
        if(v.equals(this.gyroButton))
            this.modalitaControllo = 1;
        if(v.equals(this.startButton))
            this.caricaModalita();

        this.impostaStatoComponentiGrafiche();
    }

    @Override
    public void frameContrastoToccato(MotionEvent event) {
        if(this.inPause && !this.gameOver)
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
        this.runOnUiThread(this);
    }

    @Override
    public void run() {
        //Chiamato solo quando l'evento di gameOver è chiamato per eseguire il calcolo sul thread dell'UI
        this.mostraMenuGameOver();
    }
}