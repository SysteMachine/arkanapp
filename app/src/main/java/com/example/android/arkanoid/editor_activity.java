package com.example.android.arkanoid;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.arkanoid.ActivityUtil.SoundControlActivity;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Editor.Fragment.info_fragment;
import com.example.android.arkanoid.Editor.Fragment.mappa_fragment;
import com.example.android.arkanoid.Editor.Fragment.parametri_fragment;
import com.example.android.arkanoid.Editor.Fragment.test_fragment;
import com.example.android.arkanoid.Editor.Livello;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.QueryExecutor;

public class editor_activity extends SoundControlActivity implements View.OnClickListener, DialogInterface.OnClickListener{
    private ToggleButton infoButton;
    private ToggleButton mappaButton;
    private ToggleButton parametriButton;
    private ToggleButton testButton;

    private Button resetLayerButton;
    private Button salvaLivelloButton;

    private AlertDialog dialogoSovrascrizione;
    private AlertDialog dialogoResetLayer;

    //------------------------------------------------------
    private int tabCorrente;                    //Tab correntemente visualizzata sull'activitiy
    private Livello livello;                    //Livello sotto creazione
    private int layerCorrente;                  //Layer correntemente modificato

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor_activiy);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        this.prendiRiferimenti();
        this.inizializzaInformazioni();
        this.impostaStile();
        this.avviaAudio();
    }

    /**
     * Avvia l'audio dell'editor
     */
    private void avviaAudio(){
        if(!AudioUtil.exists("editor_background")){
            AudioUtil.clear();
            AudioUtil.loadAudio("editor_background", R.raw.editor_background, AudioUtil.MUSICA, true, this);
            AudioUtil.avviaAudio("editor_background");
        }
    }

    /**
     * Prende i riferimenti agli oggetti grafici
     */
    private void prendiRiferimenti(){
        this.infoButton = this.findViewById(R.id.infoButton);
        this.mappaButton = this.findViewById(R.id.editorMappaButton);
        this.parametriButton = this.findViewById(R.id.editorParametriButton);
        this.testButton = this.findViewById(R.id.testLivelloButton);
        this.resetLayerButton = this.findViewById(R.id.resettaLayerButton);
        this.salvaLivelloButton = this.findViewById(R.id.salvaLivelloButton);

        if(this.infoButton != null)
            this.infoButton.setOnClickListener(this);
        if(this.mappaButton != null)
            this.mappaButton.setOnClickListener(this);
        if(this.parametriButton != null)
            this.parametriButton.setOnClickListener(this);
        if(this.testButton != null)
            this.testButton.setOnClickListener(this);
        if(this.resetLayerButton != null)
            this.resetLayerButton.setOnClickListener(this);
        if(this.salvaLivelloButton != null)
            this.salvaLivelloButton.setOnClickListener(this);
    }

    /**
     * Inizializza i parametri dell'istanza
     */
    private void inizializzaInformazioni(){
        this.tabCorrente = 0;
        this.layerCorrente = 0;
        this.livello = new Livello("", false);
        this.dialogoSovrascrizione = new AlertDialog.Builder(this)
                .setTitle(this.getResources().getString(R.string.editor_activity_sovrascrizione))
                .setMessage(this.getResources().getString(R.string.editor_activity_sovrascrizione_descrizione))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .create();
        this.dialogoResetLayer = new AlertDialog.Builder(this)
                .setTitle(this.getResources().getString(R.string.editor_activity_reset))
                .setMessage(this.getResources().getString(R.string.editor_activity_reset_descrizione))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .create();
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
                        .replace(R.id.containerFragment, new info_fragment())
                        .commit();
                break;
            case 1:
                this.infoButton.setChecked(false);
                this.mappaButton.setChecked(true);
                this.parametriButton.setChecked(false);
                this.testButton.setChecked(false);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, new mappa_fragment())
                        .commit();
                break;
            case 2:
                this.infoButton.setChecked(false);
                this.mappaButton.setChecked(false);
                this.parametriButton.setChecked(true);
                this.testButton.setChecked(false);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, new parametri_fragment())
                        .commit();
                break;
            case 3:
                this.infoButton.setChecked(false);
                this.mappaButton.setChecked(false);
                this.parametriButton.setChecked(false);
                this.testButton.setChecked(true);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.containerFragment, new test_fragment())
                        .commit();
                break;
        }

        this.avviaAudio();
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
        if(v.equals(this.resetLayerButton)) {
            this.dialogoResetLayer.show();
            this.onClick(this.infoButton);
        }
        if(v.equals(this.salvaLivelloButton))
            try{
                this.salvaLivello();
            }catch (Exception e){e.printStackTrace();}

        this.impostaStile();
    }

    /**
     * Salva il livello sul database
     */
    private void salvaLivello() throws Exception{
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
        if(recordSalvataggio.isLogin() && !this.livello.getNomeLivello().equals("")) {
            //Operazione consentita solo durante il login
            if(QueryExecutor.controlloEsistenzaNomeLivello(this.livello.getNomeLivello())){
                //Se il nome del livello non compare da nessuna parte inserisce il nuovo livello normalmente
                if(QueryExecutor.inserisciLivello(this.livello.getNomeLivello(), this.livello.getSalvataggio(), recordSalvataggio.getEmail()))
                    Toast.makeText(this, this.getResources().getString(R.string.editor_activity_conferma_salvataggio), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, this.getResources().getString(R.string.editor_activity_errore_salvataggio), Toast.LENGTH_LONG).show();
            }else{
                //Se esiste già un livello chiamato in questo modo, controlla il proprietario ese è l'utente sovrascrive il salvataggio
                if(recordSalvataggio.getEmail().equals(QueryExecutor.controlloProprietarioLivello(this.livello.getNomeLivello())))
                    this.dialogoSovrascrizione.show();
                else
                    Toast.makeText(this, this.getResources().getString(R.string.editor_activity_errore_salvataggio), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Torna al menu principale
     */
    private void tornaAlMenu(){
        Intent intent = new Intent(this, main_menu_activity.class);
        AudioUtil.clear();
        AudioUtil.loadAudio("background_music", R.raw.background_music, AudioUtil.MUSICA, true, this);
        AudioUtil.avviaAudio("background_music");
        this.startActivity(intent);
    }

    /**
     * Carica il livello dato un nome
     * @param nome Nome del livello da caricare
     */
    public void caricaLivello(String nome){
        try{
            String esito = QueryExecutor.caricaLivello(nome);
            if(esito != null){
                this.livello = new Livello(esito, true);
                this.layerCorrente = 0;
                this.impostaStile();
            }
        }catch (Exception e){e.printStackTrace();}
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

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog.equals(this.dialogoSovrascrizione)){
            try{
                if(QueryExecutor.aggiornaLivello(this.livello.getNomeLivello(), this.livello.getSalvataggio()))
                    Toast.makeText(this, this.getResources().getString(R.string.editor_activity_conferma_salvataggio), Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(this, this.getResources().getString(R.string.editor_activity_errore_salvataggio), Toast.LENGTH_LONG).show();

            }catch (Exception e){e.printStackTrace();}
        }

        if(dialog.equals(this.dialogoResetLayer)){
            this.livello.getLayerLivello(this.getLayerCorrente()).reset();
        }
    }

    @Override
    public void onBackPressed() {
        this.tornaAlMenu();
    }
}