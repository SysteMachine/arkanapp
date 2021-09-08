package com.example.android.arkanoid.Editor.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.android.arkanoid.Util.DBUtil;
import com.example.android.arkanoid.editor_activity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class info_fragment extends Fragment implements
        View.OnTouchListener,
        View.OnClickListener,
        AdapterView.OnItemSelectedListener,
        AdapterView.OnItemLongClickListener,
        TextWatcher,
        DialogInterface.OnClickListener{
    private final String QUERY_CONTROLLO_NOME_LIVELLO = "SELECT COUNT(*) AS N FROM creazioni WHERE creazioni.creazioni_nome LIKE NAME AND creazioni_user_email <> EMAIL";
    private final String QUERY_LIVELLI_CREATI = "SELECT creazioni_nome AS NOME FROM creazioni WHERE creazioni_user_email LIKE EMAIL";

    private EditText nomeLivelloField;
    private Spinner spinnerLayer;
    private Spinner spinnerStile;
    private ListView listaLivelliCreati;
    private ImageView aggiungiButton;
    private ImageView rimuoviButton;

    private AlertDialog dialogoEliminazioneLayer;
    private AlertDialog dialogoCaricamentoLivello;

    private String selectedLevel;                           //Livello da caricare selezionato

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        if (this.nomeLivelloField != null) {
            this.nomeLivelloField.addTextChangedListener(this);
            this.nomeLivelloField.setText(this.activity().getLivello().getNomeLivello());
        }

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
                layerString[i] = "Layer: " + i;
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
            String query = DBUtil.repalceJolly(this.QUERY_LIVELLI_CREATI, "EMAIL", recordSalvataggio.getEmail());
            try{
                String esito = DBUtil.executeQuery(query);
                if(!esito.equals("ERROR")){
                    BufferedReader reader = new BufferedReader(new InputStreamReader( new ByteArrayInputStream(esito.getBytes()) ) );
                    ArrayList<String> righe = new ArrayList<>();
                    String riga;
                    while((riga = reader.readLine()) != null)
                        righe.add(new JSONObject(riga).getString("NOME"));
                    this.listaLivelliCreati.setAdapter(new ArrayAdapter<String>(this.getContext(), R.layout.spinner_layout, righe.toArray(new String[0])));
                }
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
            activity.setLayerCorrente(ll.getPosizioneLivello());
            this.configuraSpinnerLayer();
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        //Eseguiamo un controllo sul testo inserito come nome per verificare che non sia inserito nel database
        RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
        String testo = this.nomeLivelloField.getText().toString();
        String query = DBUtil.repalceJolly(this.QUERY_CONTROLLO_NOME_LIVELLO, "NAME", testo);
        query = DBUtil.repalceJolly(query, "EMAIL", recordSalvataggio.getEmail());
        try{
            String esitoQuery = DBUtil.executeQuery(query);
            if(!esitoQuery.equals("ERROR")){
                JSONObject jsonObject = new JSONObject(esitoQuery);
                if(jsonObject.getInt("N") == 0){
                    //Se il nome non è mai stato utilizzato allora effettua la modifica
                    this.nomeLivelloField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontAviableColor));
                    editor_activity activity = this.activity();
                    if(activity != null)
                        activity.getLivello().setNomeLivello(testo);
                }else{
                    this.nomeLivelloField.setTextColor(ContextCompat.getColor(this.getContext(), R.color.fontErrorColor));
                    editor_activity activity = this.activity();
                    if(activity != null)
                        activity.getLivello().setNomeLivello("");
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

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
}