package com.example.android.arkanoid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.android.arkanoid.ActivityUtil.MultiFragmentActivity;
import com.example.android.arkanoid.ActivityUtil.PausaEventListener;
import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Editor.Livello;
import com.example.android.arkanoid.FragmentMenu.pausa_generica_fragment;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverListener;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaCreazione;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.Util.QueryExecutor;

public class creazioni_activity extends MultiFragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher, GameOverListener, PausaEventListener, DialogInterface.OnClickListener {
    private ToggleButton pulsanteCreazioni;
    private ToggleButton pulsanteCreazioniLocale;
    private ToggleButton pulsanteTouch;
    private ToggleButton pulsanteGyro;
    private ListView listaModalita;
    private EditText barraRicerca;

    private FrameLayout containerModalita;
    private static String nomeLivello;
    private static ModalitaCreazione modalita;
    private GameLoop gameLoop;
    private AlertDialog dialogoCaricamentoLivello;

    private int vistaAttiva;
    private int modalitaControllo;

    private boolean inGame;
    private boolean inPause;
    private boolean gameOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creazioni);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.gameLoop = new GameLoop(this, 60, 720, 1280);

        this.vistaAttiva = 0;
        this.modalitaControllo = 0;
        this.inGame = false;
        this.inPause = false;
        this.gameOver = false;
        this.raccogliRiferimenti();

        this.dialogoCaricamentoLivello = new AlertDialog.Builder(this)
                .setTitle(this.getResources().getString(R.string.editor_activity_caricamento))
                .setMessage(this.getResources().getString(R.string.editor_activity_caricamento_descrizione))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .create();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.gameLoop.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.impostaStilePulsanti();
        this.inserisciElementiLista();
        this.modificaVista();
        this.ripristinaUltimoStato();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("vistaAttiva", this.vistaAttiva);
        outState.putInt("modalitaControllo", this.modalitaControllo);
        outState.putBoolean("inGame", this.inGame);
        outState.putBoolean("inPause", this.inPause);
        outState.putBoolean("gameOver", this.gameOver);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.vistaAttiva = savedInstanceState.getInt("vistaAttiva", 0);
        this.modalitaControllo = savedInstanceState.getInt("modalitaControllo", 0);
        this.inGame = savedInstanceState.getBoolean("inGame");
        this.inPause = savedInstanceState.getBoolean("inPause");
        this.gameOver = savedInstanceState.getBoolean("gameOver");
    }

    /**
     * Raccoglie i riferimenti alle viste
     */
    private void raccogliRiferimenti(){
        this.pulsanteCreazioni = this.findViewById(R.id.creazioniGlobaliButton);
        this.pulsanteCreazioniLocale = this.findViewById(R.id.creazioniLocaliButton);
        this.pulsanteTouch = this.findViewById(R.id.touchButton);
        this.pulsanteGyro = this.findViewById(R.id.gyroButton);
        this.listaModalita = this.findViewById(R.id.listaPartite);
        this.barraRicerca = this.findViewById(R.id.barraRicercaField);
        this.containerModalita = this.findViewById(R.id.containerModalita);

        if(this.pulsanteCreazioni != null)
            this.pulsanteCreazioni.setOnClickListener(this);
        if(this.pulsanteCreazioniLocale != null)
            this.pulsanteCreazioniLocale.setOnClickListener(this);
        if(this.pulsanteTouch != null)
            this.pulsanteTouch.setOnClickListener(this);
        if(this.pulsanteGyro != null)
            this.pulsanteGyro.setOnClickListener(this);
        if(this.listaModalita != null)
            this.listaModalita.setOnItemClickListener(this);
        if(this.barraRicerca != null)
            this.barraRicerca.addTextChangedListener(this);
        if(this.containerModalita != null)
            this.containerModalita.addView(this.gameLoop);

    }

    /**
     * Modifica la vista in base all'utente collegato
     */
    private void modificaVista(){
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
        if(!recordSalvataggio.isLogin()){
            if(this.pulsanteCreazioniLocale != null)
                this.pulsanteCreazioniLocale.setVisibility(View.GONE);
        }
    }

    /**
     * Imposta lo stile dei pulsanti
     */
    private void impostaStilePulsanti(){
        if(this.pulsanteCreazioni != null && this.pulsanteCreazioniLocale != null){
            switch (this.vistaAttiva){
                case 0:
                    this.pulsanteCreazioni.setChecked(true);
                    this.pulsanteCreazioniLocale.setChecked(false);
                    break;
                case 1:
                    this.pulsanteCreazioni.setChecked(false);
                    this.pulsanteCreazioniLocale.setChecked(true);
                    break;
            }
        }
        if(this.pulsanteTouch != null && this.pulsanteGyro != null){
            switch (this.modalitaControllo){
                case 0:
                    this.pulsanteTouch.setChecked(true);
                    this.pulsanteGyro.setChecked(false);
                    break;
                case 1:
                    this.pulsanteTouch.setChecked(false);
                    this.pulsanteGyro.setChecked(true);
                    break;
            }
        }
    }

    /**
     * Inserisce gli elementi sulla lista
     */
    private void inserisciElementiLista(){
        try{
            if(this.listaModalita != null){
                if( (this.barraRicerca != null && this.barraRicerca.getText().toString().equals("")) || this.vistaAttiva == 1){
                    if(this.vistaAttiva == 0)
                        this.listaModalita.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout, QueryExecutor.recuperoLivelli()));
                    else {
                        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this);
                        this.listaModalita.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout, QueryExecutor.recuperoLivelliCreati(recordSalvataggio.getEmail())));
                    }
                }else if(this.barraRicerca != null && !this.barraRicerca.getText().toString().equals("")){
                    String filtro = this.barraRicerca.getText().toString();
                    this.listaModalita.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_layout, QueryExecutor.recuperoLivelliConFiltro(filtro)));
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Restituisce lo stile che deve essere caricato dalla modalita
     * @param livello Livello da caricare
     * @return Restituisce lo stile della creazione
     */
    private Stile getStile(Livello livello){
        Stile stile = new Stile();

        if(livello.getIndiceStile() == StileAtzeco.ID_STILE)
            stile = new StileAtzeco();
        if(livello.getIndiceStile() == StileSpaziale.ID_STILE)
            stile = new StileSpaziale();
        if(livello.getIndiceStile() == StileFuturistico.ID_STILE)
            stile = new StileFuturistico();

        return stile;
    }

    /**
     * Ripristina l'ultimo stato
     */
    private void ripristinaUltimoStato(){
        if(this.inGame){
            this.gameLoop.start();
            this.gameLoop.addGameComponentNoSetup(creazioni_activity.modalita);
            if(this.containerModalita != null) {
                this.containerModalita.setVisibility(View.VISIBLE);
            }
            if(this.inPause || this.gameOver)
                this.gameLoop.setUpdateRunning(false);
            creazioni_activity.modalita.setGameOverListener(this);
        }
        Fragment fragmentAttivo = this.getSupportFragmentManager().findFragmentById(this.containerFragment.getId());
        if(fragmentAttivo != null){
            ((pausa_generica_fragment)fragmentAttivo).setListener(this);
        }
    }

    /**
     * Carica la modalità
     */
    private void caricaModalita(String nomeLivello){
        try{
            String datiLivello = QueryExecutor.caricaLivello(nomeLivello);
            if(!datiLivello.equals("")){
                creazioni_activity.nomeLivello = nomeLivello;
                Livello livello = new Livello(datiLivello, true);
                if(creazioni_activity.modalita != null) {
                    creazioni_activity.modalita.setGameOverListener(null);
                    this.gameLoop.removeAll();
                }
                creazioni_activity.modalita = new ModalitaCreazione(this.getStile(livello), new GameStatus(5, 0, this.modalitaControllo), livello);
                creazioni_activity.modalita.setGameOverListener(this);
                gameLoop.addGameComponent(creazioni_activity.modalita);
                gameLoop.setShowFPS(false);
                gameLoop.start();

                this.containerModalita.setVisibility(View.VISIBLE);
                this.inGame = true;
                this.inPause = false;
                this.gameOver = false;
            }
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Mostra il menu della pausa
     */
    private void mostraMenuPausa(){
        if(this.inGame && !this.inPause && !this.gameOver){
            pausa_generica_fragment fragment = new pausa_generica_fragment();
            Bundle params = new Bundle();
            params.putInt("tipoPausa", pausa_generica_fragment.PAUSA);
            fragment.setArguments(params);
            fragment.setListener(this);
            this.mostraFragment(fragment, true);

            this.gameLoop.setUpdateRunning(false);
            this.inPause = true;
        }
    }

    /**
     * Nasconde il menu della pausa
     */
    private void nascondiMenuPausa(){
        if(this.inPause){
            this.nascondiFragment(true);

            this.gameLoop.setUpdateRunning(true);
            this.inPause = false;
        }
    }

    /**
     * Mostra il menu di gameOver
     */
    private void mostraMenuGameOver(){
        if(this.inGame && !this.gameOver){
            if(this.inPause) {
                this.nascondiFragment(false);
                this.inPause = false;
            }

            pausa_generica_fragment fragment = new pausa_generica_fragment();
            Bundle params = new Bundle();
            params.putInt("tipoPausa", pausa_generica_fragment.GAMEOVER);
            fragment.setArguments(params);
            fragment.setListener(this);
            this.mostraFragment(fragment, true);

            this.gameLoop.setUpdateRunning(false);
            this.gameOver = true;
        }
    }

    /**
     * Nasconde il menu di gameover
     */
    private void nascondiMenuGameOver(){
        if(this.gameOver) {
            this.nascondiFragment(true);

            this.gameLoop.setUpdateRunning(true);
            this.gameOver = false;
        }

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
            this.startActivity(new Intent(this, main_menu_activity.class));
        }
    }

    @Override
    public void frameContrastoToccato(MotionEvent event) {
        if(this.inPause && !this.gameOver)
            this.nascondiMenuPausa();
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.pulsanteCreazioni))
            this.vistaAttiva = 0;
        if(v.equals(this.pulsanteCreazioniLocale))
            this.vistaAttiva = 1;
        if(v.equals(this.pulsanteTouch))
            this.modalitaControllo = 0;
        if(v.equals(this.pulsanteGyro))
            this.modalitaControllo = 1;

        this.impostaStilePulsanti();
        this.inserisciElementiLista();
    }

    /**
     * Ripristina l'audio
     */
    private void ripristinaAudio(){
        AudioUtil.clear();
        AudioUtil.loadAudio("background_music", R.raw.background_music, AudioUtil.MUSICA, true, this);
        AudioUtil.avviaAudio("background_music");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.equals(this.listaModalita)){
            creazioni_activity.nomeLivello = ((TextView)view).getText().toString();
            this.dialogoCaricamentoLivello.show();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        this.inserisciElementiLista();
    }

    @Override
    public void gameOver(GameStatus status) {
        if(!this.gameOver){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mostraMenuGameOver();
                }
            });
        }
    }

    @Override
    public void event(String eventId) {
        if(this.inPause){
            if(eventId.equals("PRIMO"))
                this.nascondiMenuPausa();
        }
        if(this.gameOver){
            if(eventId.equals("PRIMO")){
                this.gameLoop.stop();
                this.nascondiMenuGameOver();
                this.caricaModalita(creazioni_activity.nomeLivello);
            }
        }

        if(eventId.equals("SECONDO")){
            this.gameOver = false;
            this.inPause = false;
            this.inGame = false;
            this.nascondiFragment(false);
            this.ripristinaAudio();
            this.gameLoop.stop();
            this.gameLoop.setUpdateRunning(true);
            if(this.containerModalita != null)
                this.containerModalita.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog.equals(this.dialogoCaricamentoLivello)){
            this.caricaModalita(nomeLivello);
        }
    }
}