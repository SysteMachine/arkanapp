package com.example.android.arkanoid;

import android.content.pm.ActivityInfo;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ToggleButton;

import com.example.android.arkanoid.Editor.Fragment.info_fragment;
import com.example.android.arkanoid.Editor.Fragment.mappa_fragment;
import com.example.android.arkanoid.Editor.Fragment.parametri_fragment;
import com.example.android.arkanoid.Editor.Fragment.test_fragment;
import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.Editor.Livello;

public class editor_activity extends AppCompatActivity implements View.OnClickListener {
    private ToggleButton infoButton;
    private ToggleButton mappaButton;
    private ToggleButton parametriButton;
    private ToggleButton testButton;

    //------------------------------------------------------
    private int tabCorrente;                    //Tab correntemente visualizzata sull'activitiy
    private Livello livello;                    //Livello sotto creazione
    private int layerCorrente;                  //Layer correntemente modificato

    //Fragment del pannello
    private Fragment infoFragment;              //Fragmen di info
    private Fragment mappaFragment;             //Fragment per la modifica della mappa
    private Fragment parametriFragment;         //Fragment per i parametri
    private Fragment testFragment;              //Fragment per il test

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_activiy);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.prendiRiferimenti();
        this.inizializzaInformazioni();
        this.impostaStile();
    }

    /**
     * Prende i riferimenti agli oggetti grafici
     */
    private void prendiRiferimenti(){
        this.infoButton = this.findViewById(R.id.infoButton);
        this.mappaButton = this.findViewById(R.id.editorMappaButton);
        this.parametriButton = this.findViewById(R.id.editorParametriButton);
        this.testButton = this.findViewById(R.id.testLivelloButton);

        if(this.infoButton != null)
            this.infoButton.setOnClickListener(this);
        if(this.mappaButton != null)
            this.mappaButton.setOnClickListener(this);
        if(this.parametriButton != null)
            this.parametriButton.setOnClickListener(this);
        if(this.testButton != null)
            this.testButton.setOnClickListener(this);
    }

    /**
     * Inizializza i parametri dell'istanza
     */
    private void inizializzaInformazioni(){
        this.infoFragment = new info_fragment();
        this.mappaFragment = new mappa_fragment();
        this.parametriFragment = new parametri_fragment();
        this.testFragment = new test_fragment();

        this.tabCorrente = 0;
        this.layerCorrente = 0;
        this.livello = new Livello("", false);
    }

    private void impostaStile(){
        switch (this.tabCorrente){
            case 0:
                this.infoButton.setChecked(true);
                this.mappaButton.setChecked(false);
                this.parametriButton.setChecked(false);
                this.testButton.setChecked(false);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, this.infoFragment)
                        .commit();
                break;
            case 1:
                this.infoButton.setChecked(false);
                this.mappaButton.setChecked(true);
                this.parametriButton.setChecked(false);
                this.testButton.setChecked(false);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, this.mappaFragment)
                        .commit();
                break;
            case 2:
                this.infoButton.setChecked(false);
                this.mappaButton.setChecked(false);
                this.parametriButton.setChecked(true);
                this.testButton.setChecked(false);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, this.parametriFragment)
                        .commit();
                break;
            case 3:
                this.infoButton.setChecked(false);
                this.mappaButton.setChecked(false);
                this.parametriButton.setChecked(false);
                this.testButton.setChecked(true);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, this.testFragment)
                        .commit();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.infoButton))
            this.tabCorrente = 0;
        if(v.equals(this.mappaButton))
            this.tabCorrente = 1;
        if(v.equals(this.parametriButton))
            this.tabCorrente = 2;
        if(v.equals(this.testButton))
            this.tabCorrente = 3;

        this.impostaStile();
    }

    //Beam
    //Get
    public Livello getLivello() {
        return livello;
    }

    public int getLayerCorrente() {
        return layerCorrente;
    }

    //Set
    public void setLivello(Livello livello) {
        this.livello = livello;
    }

    public void setLayerCorrente(int layerCorrente) {
        this.layerCorrente = layerCorrente;
    }
}