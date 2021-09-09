package com.example.android.arkanoid.Editor.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.QueryExecutor;
import com.example.android.arkanoid.editor_activity;

public class info_fragment extends Fragment implements
        View.OnTouchListener,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        AdapterView.OnItemLongClickListener,
        DialogInterface.OnClickListener,
        Runnable{

    private EditText nomeLivelloField;
    private Spinner spinnerLayer;
    private Spinner spinnerStile;
    private ListView listaLivelliCreati;
    private ImageView aggiungiButton;
    private ImageView rimuoviButton;

    private AlertDialog dialogoEliminazioneLayer;
    private AlertDialog dialogoCaricamentoLivello;

    private String selectedLevel;                           //Livello da caricare selezionato
    private boolean runningControllo;                       //Flag per il thread di controllo
    private Thread threadControllo;                         //Thread di controllo

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.runningControllo = true;
        this.threadControllo = new Thread(this);
        this.threadControllo.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.runningControllo = false;
        try{
            this.threadControllo.join();
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_fragment, container, false);

        view.setOnTouchListener(this);

        this.dialogoEliminazioneLayer = new AlertDialog.Builder(this.getContext())
                .setTitle(this.getResources().getString(R.string.editor_activity_cancellazione))
                .setMessage(this.getResources().getString(R.string.editor_activity_cancellazione_descrizione))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .create();
        this.dialogoCaricamentoLivello = new AlertDialog.Builder(this.getContext())
                .setTitle(this.getResources().getString(R.string.editor_activity_caricamento))
                .setMessage(this.getResources().getString(R.string.editor_activity_caricamento_descrizione))
                .setPositiveButton(android.R.string.yes, this)
                .setNegativeButton(android.R.string.no, null)
                .create();

        this.nomeLivelloField = view.findViewById(R.id.nomeLivelloField);
        this.spinnerLayer = view.findViewById(R.id.spinnerLayer);
        this.configuraSpinnerLayer();
        this.spinnerStile = view.findViewById(R.id.spinnerStile);
        this.configuraSpinnerStile();
        this.listaLivelliCreati = view.findViewById(R.id.listaLivelliCreati);
        this.configuraListaLivelliCreati();
        this.aggiungiButton = view.findViewById(R.id.aggiungiButton);
        this.rimuoviButton = view.findViewById(R.id.rimuoviButton);

        if (this.spinnerLayer != null)
            this.spinnerLayer.setOnItemSelectedListener(this);
        if (this.spinnerStile != null)
            this.spinnerStile.setOnItemSelectedListener(this);
        if (this.listaLivelliCreati != null)
            this.listaLivelliCreati.setOnItemLongClickListener(this);
        if (this.aggiungiButton != null)
            this.aggiungiButton.setOnClickListener(this);
        if (this.rimuoviButton != null)
            this.rimuoviButton.setOnClickListener(this);
        if (this.nomeLivelloField != null)
            this.nomeLivelloField.setText(this.activity().getLivello().getNomeLivello());

        return view;
    }

    /**
     * Restituisce l'activity del fragment
     *
     * @return Restituisce l'activity del fragment o null in caso di problemi
     */
    private editor_activity activity() {
        editor_activity activity = null;
        if (this.getActivity() != null) {
            activity = (editor_activity) this.getActivity();
        }
        return activity;
    }

    /**
     * Configura il contenuto dello spinnerLayer
     */
    private void configuraSpinnerLayer() {
        editor_activity activity = this.activity();
        if (activity != null) {
            int numeroLivelli = activity.getLivello().getNLivelli();
            String[] layerString = new String[numeroLivelli];
            for (int i = 0; i < numeroLivelli; i++)
                layerString[i] = this.getResources().getString(R.string.info_fragment_nome_layer) + ": " + (i + 1);
            if (this.spinnerLayer != null) {
                this.spinnerLayer.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_layout, layerString));
                this.spinnerLayer.setSelection(activity.getLayerCorrente());
            }
        }
    }

    /**
     * Configura il contenuto dello spinnerLayer
     */
    private void configuraSpinnerStile() {
        editor_activity activity = this.activity();
        if (activity != null) {
            int numeroLivelli = activity.getLivello().getContatoreLivello();
            String[] stiliString = new String[4];

            stiliString[Stile.ID_STILE] = Stile.NOME_STILE;
            stiliString[StileSpaziale.ID_STILE] = StileSpaziale.NOME_STILE;
            stiliString[StileAtzeco.ID_STILE] = StileAtzeco.NOME_STILE;
            stiliString[StileFuturistico.ID_STILE] = StileFuturistico.NOME_STILE;

            if (this.spinnerStile != null) {
                this.spinnerStile.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_layout, stiliString));
                this.spinnerStile.setSelection(activity.getLivello().getIndiceStile());
            }
        }
    }

    /**
     * Configura la lista delle creazioni del giocatore
     */
    private void configuraListaLivelliCreati() {
        if (this.listaLivelliCreati != null) {
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
            try{
                String[] livelliCreati = QueryExecutor.recuperoLivelliCreati(recordSalvataggio.getEmail());
                this.listaLivelliCreati.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_layout, livelliCreati));
            }catch (Exception e){e.printStackTrace();}
        }
    }

    //Eventi del fragment

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        editor_activity activity = this.activity();

        if (v.equals(this.aggiungiButton) && activity != null) {
            LayerLivello ll = activity.getLivello().creaLayerLivello();
            if(ll != null){
                activity.setLayerCorrente(ll.getPosizioneLivello());
                this.configuraSpinnerLayer();
            }
        }

        if (v.equals(this.rimuoviButton) && activity != null) {
            if (activity.getLivello().getNLivelli() - 1 >= 1) {
                this.dialogoEliminazioneLayer.show();
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tView = (TextView)view;
        this.selectedLevel = tView.getText().toString();
        this.dialogoCaricamentoLivello.show();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        editor_activity activity = this.activity();

        if (parent.equals(this.spinnerLayer) && activity != null) {
            activity.setLayerCorrente(position);
        }

        if (parent.equals(this.spinnerStile) && activity != null) {
            activity.getLivello().setIndiceStile(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if(dialog.equals(this.dialogoEliminazioneLayer)){
            editor_activity activity = this.activity();
            if(activity != null){
                activity.getLivello().eliminaLayer(activity.getLayerCorrente());
                activity.setLayerCorrente(0);
                this.configuraSpinnerLayer();
            }
        }

        if(dialog.equals(this.dialogoCaricamentoLivello)){
            editor_activity activity = this.activity();
            if(activity != null)
                activity.caricaLivello(this.selectedLevel);
        }
    }

    @Override
    public void run() {
        //Eseguiamo un controllo sul testo inserito come nome per verificare che non sia inserito nel database
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
        while (this.runningControllo){
            String nomeLivello= this.nomeLivelloField.getText().toString();
            try{
                if(this.nomeLivelloField != null){
                    editor_activity activity = this.activity();
                    if(QueryExecutor.controlloEsistenzaNomeLivelloLocale(nomeLivello, recordSalvataggio.getEmail())){
                        this.nomeLivelloField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontAviableColor));
                        if(activity != null)
                            activity.getLivello().setNomeLivello(nomeLivello);
                    }else{
                        this.nomeLivelloField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontErrorColor));
                        if(activity != null)
                            activity.getLivello().setNomeLivello("");
                    }
                }
                Thread.sleep(200);
            }catch (Exception e){e.printStackTrace();}
        }
    }
}