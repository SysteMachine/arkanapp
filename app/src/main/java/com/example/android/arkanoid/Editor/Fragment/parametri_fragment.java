package com.example.android.arkanoid.Editor.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.editor_activity;

public class parametri_fragment extends Fragment implements View.OnTouchListener, TextWatcher, SeekBar.OnSeekBarChangeListener {
    private TextView pv_pal_label;
    private TextView pv_pad_label;
    private SeekBar pv_pal_seek;
    private SeekBar pv_pad_seek;
    private EditText puntiColpoField;
    private EditText puntiPartitaField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parametri_fragment, container, false);
        view.setOnTouchListener(this);
        editor_activity activity = this.activity();
        this.pv_pal_label = view.findViewById(R.id.velocitaPallaLabel);
        this.pv_pad_label = view.findViewById(R.id.velocitaPaddleLabel);
        this.pv_pal_seek = view.findViewById(R.id.PVPALSeek);
        this.pv_pad_seek = view.findViewById(R.id.PVPADSeek);
        this.puntiColpoField = view.findViewById(R.id.puntiColpoField);
        this.puntiPartitaField = view.findViewById(R.id.puntiPartitaField);

        if(this.pv_pal_seek != null && activity != null) {
            this.pv_pal_seek.setOnSeekBarChangeListener(this);
            this.pv_pal_seek.setMax((int)(LayerLivello.MAX_PVPALLA * 100));
            this.pv_pal_seek.setMin((int)(LayerLivello.MIN_PVPALLA * 100));
        }
        if(this.pv_pad_seek != null && activity != null) {
            this.pv_pad_seek.setOnSeekBarChangeListener(this);
            this.pv_pad_seek.setMax((int)(LayerLivello.MAX_PVPADDLE * 100));
            this.pv_pad_seek.setMin((int)(LayerLivello.MIN_PVPADDLE * 100));
        }
        if(this.puntiColpoField != null && activity != null)
            this.puntiColpoField.addTextChangedListener(this);
        if(this.puntiPartitaField != null && activity != null)
            this.puntiPartitaField.addTextChangedListener(this);

        this.setStileComponenti();
        return view;
    }

    /**
     * Imposta lo stile delle componenti
     */
    private void setStileComponenti(){
        editor_activity activity = this.activity();
        if(activity != null){
            LayerLivello layerLivello = activity.getLivello().getLayerLivello(activity.getLayerCorrente());
            if(this.pv_pal_seek != null && this.pv_pal_label != null){
                int progresso = (int)(layerLivello.getPercentualeIncrementoVelocitaPalla() * 100);
                this.pv_pal_seek.setProgress(progresso);
                this.pv_pal_label.setText(new StringBuilder().append(this.getContext().getText(R.string.parametri_fragment_velocita_palla)).append(": ").append(progresso).append("%").toString());
            }
            if(this.pv_pad_seek != null && this.pv_pad_label != null) {
                int progresso = (int)(layerLivello.getPercentualeIncrementoVelocitaPaddle() * 100);
                this.pv_pad_seek.setProgress(progresso);
                this.pv_pad_label.setText(new StringBuilder().append(this.getContext().getText(R.string.parametri_fragment_velocita_paddle)).append(": ").append(progresso).append("%").toString());
            }
            if(this.puntiColpoField != null)
                this.puntiColpoField.setText("" + layerLivello.getPuntiPerColpo());
            if(this.puntiPartitaField != null)
                this.puntiPartitaField.setText("" + layerLivello.getPuntiTerminazione());
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        editor_activity activity = this.activity();
        if(!s.toString().equals("") && activity != null){
            LayerLivello layerLivello = activity.getLivello().getLayerLivello(activity.getLayerCorrente());

            if(s.equals(this.puntiColpoField.getText()))
                layerLivello.setPuntiPerColpo(Integer.valueOf(s.toString()));

            if(s.equals(this.puntiPartitaField.getText()))
                layerLivello.setPuntiTerminazione(Integer.valueOf(s.toString()));
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(fromUser){
            editor_activity activity = this.activity();
            if(activity != null){
                LayerLivello layerLivello = activity.getLivello().getLayerLivello(activity.getLayerCorrente());

                if(seekBar.equals(this.pv_pal_seek)){
                    float valore = progress / 100.0f;
                    layerLivello.setPercentualeIncrementoVelocitaPalla(valore);
                }

                if(seekBar.equals(this.pv_pad_seek)){
                    float valore = progress / 100.0f;
                    layerLivello.setPercentualeIncrementoVelocitaPaddle(valore);
                }
            }
            this.setStileComponenti();
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}