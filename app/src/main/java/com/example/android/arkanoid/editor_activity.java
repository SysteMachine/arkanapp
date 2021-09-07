package com.example.android.arkanoid;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Editor.Fragment.info_fragment;
import com.example.android.arkanoid.Editor.Fragment.mappa_fragment;
import com.example.android.arkanoid.Editor.Fragment.parametri_fragment;
import com.example.android.arkanoid.Editor.Fragment.test_fragment;
import com.example.android.arkanoid.Editor.Livello;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.DBUtil;

import org.json.JSONObject;

public class editor_activity extends AppCompatActivity implements View.OnClickListener, DialogInterface.OnClickListener{
    private final String QUERY_CONTROLLO_NOME_LIVELLO = "SELECT COUNT(*) AS N FROM creazioni WHERE creazioni_nome LIKE NAME";
    private final String QUERY_CONTROLLO_PROPRIETARIO_LIVELLO = "SELECT creazioni_user_email AS EMAIL FROM creazioni WHERE creazioni_nome LIKE NAME";
    private final String QUERY_INSERIMENTO_LIVELLO = "INSERT INTO creazioni VALUES(NOME, DATI, EMAIL)";
    private final String QUERY_AGGIORNAMENTO_LIVELLO = "UPDATE creazioni SET creazioni_dati = DATI WHERE creazioni_nome LIKE NOME";

    private ToggleButton infoButton;
    private ToggleButton mappaButton;
    private ToggleButton parametriButton;
    private ToggleButton testButton;

    private Button resetLayerButton;
    private Button salvaLivelloButton;

    private AlertDialog dialogoSovrascrizione;

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
        if(AudioUtil.getMediaPlayer("editor_background") == null){
            AudioUtil.clear();
            AudioUtil.loadAudio("editor_background", R.raw.editor_background, this);
            AudioUtil.getMediaPlayer("editor_background").setLooping(true);
            AudioUtil.getMediaPlayer("editor_background").start();
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
            this.livello.getLayerLivello(this.layerCorrente).reset();
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
            String queryControlloNome = DBUtil.repalceJolly(this.QUERY_CONTROLLO_NOME_LIVELLO, "NAME", this.livello.getNomeLivello());
            String risultato = DBUtil.executeQuery(queryControlloNome);
            if (!risultato.equals("ERROR")) {
                if (new JSONObject(risultato).getInt("N") == 0) {
                    //Se non ci sono record inseriti si aggiunge normalmente
                    String queryInserimento = DBUtil.repalceJolly(this.QUERY_INSERIMENTO_LIVELLO, "NOME", this.livello.getNomeLivello());
                    queryInserimento = DBUtil.repalceJolly(queryInserimento, "EMAIL", recordSalvataggio.getEmail());
                    queryInserimento = DBUtil.repalceJolly(queryInserimento, "DATI", this.livello.getSalvataggio());
                    risultato = DBUtil.executeQuery(queryInserimento);
                    Toast toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
                    if(!risultato.equals("ERROR"))
                        toast.setText(this.getResources().getString(R.string.editor_activity_conferma_salvataggio));
                    else
                        toast.setText(this.getResources().getString(R.string.editor_activity_errore_salvataggio));
                    toast.show();
                } else {
                    //Se esiste un altro elemento si controlla il proprietario
                    String queryControlloProprietario = DBUtil.repalceJolly(this.QUERY_CONTROLLO_PROPRIETARIO_LIVELLO, "NAME", this.livello.getNomeLivello());
                    risultato = DBUtil.executeQuery(queryControlloProprietario);
                    if(!risultato.equals("ERROR") && new JSONObject(risultato).getString("EMAIL").equals(recordSalvataggio.getEmail()))  {
                        //Se il precedente salvataggio è del giocatore chiede la sovrascrizione
                        this.dialogoSovrascrizione.show();
                    }else
                        Toast.makeText(this, this.getResources().getString(R.string.editor_activity_errore_salvataggio), Toast.LENGTH_LONG).show();
                }
            }
        }
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
            String queryAggiornamento = DBUtil.repalceJolly(this.QUERY_AGGIORNAMENTO_LIVELLO, "NOME", this.livello.getNomeLivello());
            queryAggiornamento = DBUtil.repalceJolly(queryAggiornamento, "DATI", this.livello.getSalvataggio());

            try {
                String esito = DBUtil.executeQuery(queryAggiornamento);
                Toast toast = Toast.makeText(this, "", Toast.LENGTH_LONG);
                if(!esito.equals("ERROR"))
                    toast.setText(this.getResources().getString(R.string.editor_activity_conferma_salvataggio));
                else
                    toast.setText(this.getResources().getString(R.string.editor_activity_errore_salvataggio));
                toast.show();
            }catch (Exception e){e.printStackTrace();}
        }
    }
}