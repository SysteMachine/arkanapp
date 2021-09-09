package com.example.android.arkanoid;

import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverListener;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.SceneDefinite.ModalitaCreazione;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;
import com.example.android.arkanoid.Util.QueryExecutor;

public class creazioni_activity extends MultiFragmentActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, TextWatcher, GameOverListener, PausaEventListener {
    private ToggleButton pulsanteCreazioni;
    private ToggleButton pulsanteCreazioniLocale;
    private ToggleButton pulsanteTouch;
    private ToggleButton pulsanteGyro;
    private ListView listaModalita;
    private EditText barraRicerca;

    private FrameLayout containerModalita;
    private GameLoop gameLoop;

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
            this.listaModalita.setOnItemLongClickListener(this);
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
            if(this.containerModalita != null)
                this.containerModalita.setVisibility(View.VISIBLE);
            if(this.inPause || this.gameOver)
                this.gameLoop.setUpdateRunning(false);
        }
    }

    /**
     * Carica la modalità
     */
    private void caricaModalita(String nomeLivello){
        try{
            String datiLivello = QueryExecutor.caricaLivello(nomeLivello);
            if(!datiLivello.equals("")){
                Livello livello = new Livello(datiLivello, true);
                ModalitaCreazione modalitaCreazione = new ModalitaCreazione(this.getStile(livello), new GameStatus(5, 0, this.modalitaControllo), livello);
                gameLoop.addGameComponent(modalitaCreazione);
                gameLoop.setShowFPS(true);
                gameLoop.start();

                this.containerModalita.setVisibility(View.VISIBLE);
                this.inGame = true;
            }
        }catch (Exception e){e.printStackTrace();}
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

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if(parent.equals(this.listaModalita)){
            String nomeLivello = ((TextView)view).getText().toString();
            this.caricaModalita(nomeLivello);
        }

        return true;
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

    }

    @Override
    public void event(String eventId) {

    }
}