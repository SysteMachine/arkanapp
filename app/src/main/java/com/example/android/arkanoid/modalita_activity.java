package com.example.android.arkanoid;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;

public class modalita_activity extends AppCompatActivity implements View.OnClickListener {
    public static final int CODICE_MODALITA_CLASSICA = 0;                           //Codice per avviare la modalità classica

    private final float[] MOLTIPLICATORI_PER_DIFFICOLTA = {0.8f, 1, 1.2f};          //Moltiplicatori per le difficoltà

    //----------------------------------------------------------//

    private ToggleButton easyButton;
    private ToggleButton normalButton;
    private ToggleButton hardButton;
    private static int difficultyButtonIndex;

    private ToggleButton touchButton;
    private ToggleButton gyroButton;
    private static int controlButtonIndex;

    private static int codiceModalita;
    private TextView labelModalita;

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modalita);
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.easyButton = this.findViewById(R.id.difficoltaFacileButton);
        if(this.easyButton != null)
            this.easyButton.setOnClickListener(this);
        this.normalButton = this.findViewById(R.id.difficoltaNormaleButton);
        if(this.normalButton != null)
            this.normalButton.setOnClickListener(this);
        this.hardButton = this.findViewById(R.id.difficoltaDifficileButton);
        if(this.hardButton != null)
            this.hardButton.setOnClickListener(this);

        this.difficultyButtonIndex = 0;

        this.touchButton = this.findViewById(R.id.touchButton);
        if(this.touchButton != null)
            this.touchButton.setOnClickListener(this);
        this.gyroButton = this.findViewById(R.id.gyroButton);
        if(this.gyroButton != null)
            this.gyroButton.setOnClickListener(this);

        this.controlButtonIndex = 0;

        this.startButton = this.findViewById(R.id.startButton);
        if(this.startButton != null)
            this.startButton.setOnClickListener(this);

        this.illuminaPulsanteControllo(this.controlButtonIndex);
        this.illuminaPulsanteDifficolta(this.difficultyButtonIndex);

        this.labelModalita = this.findViewById(R.id.nomeModalitaLabel);

        Intent intent = this.getIntent();
        if(intent != null){
            this.codiceModalita = intent.getIntExtra("MODE", 0);
        }
        this.impostaNomeModalita(this.codiceModalita);
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

        GameStatus gameStatus = new GameStatus(health, punteggio, modalitaInput);

        return gameStatus;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.easyButton))
            this.difficultyButtonIndex = 0;
        if(v.equals(this.normalButton))
            this.difficultyButtonIndex = 1;
        if(v.equals(this.hardButton))
            this.difficultyButtonIndex = 2;

        this.illuminaPulsanteDifficolta(this.difficultyButtonIndex);

        if(v.equals(this.touchButton))
            this.controlButtonIndex = 0;
        if(v.equals(this.gyroButton))
            this.controlButtonIndex = 1;

        this.illuminaPulsanteControllo(this.controlButtonIndex);
    }
}