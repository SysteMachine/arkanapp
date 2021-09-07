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
    private EditText hitBlockField;
    private EditText puntiPartitaField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
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
        this.pv_pal_label = view.findViewById(R.id.velocitaPallaLabel);
        this.pv_pad_label = view.findViewById(R.id.velocitaPaddleLabel);

        editor_activity activity = this.activity();

        this.pv_pal_seek = view.findViewById(R.id.PVPALSeek);
        if(this.pv_pal_seek != null && activity != null) {
            this.pv_pal_seek.setOnSeekBarChangeListener(this);
            this.pv_pal_seek.setProgress((int)(activity.getLivello().getLayerLivello(activity.getLayerCorrente()).getPercentualeIncrementoVelocitaPalla() * 100));
            this.pv_pal_seek.setMin((int)(LayerLivello.MIN_PVPALLA * 100));
            this.pv_pal_seek.setMax((int)(LayerLivello.MAX_PVPALLA * 100));
        }

        this.pv_pad_seek = view.findViewById(R.id.PVPADSeek);
        if(this.pv_pad_seek != null && activity != null) {
            this.pv_pad_seek.setOnSeekBarChangeListener(this);
            this.pv_pad_seek.setProgress((int)(activity.getLivello().getLayerLivello(activity.getLayerCorrente()).getPercentualeIncrementoVelocitaPaddle() * 100));
            this.pv_pad_seek.setMin((int)(LayerLivello.MIN_PVPADDLE * 100));
            this.pv_pad_seek.setMax((int)(LayerLivello.MAX_PVPADDLE * 100));
        }

        this.hitBlockField = view.findViewById(R.id.puntiColpoField);
        if(this.hitBlockField != null)
            this.hitBlockField.addTextChangedListener(this);

        this.puntiPartitaField = view.findViewById(R.id.puntiPartitaField);
        if(this.puntiPartitaField != null)
            this.puntiPartitaField.addTextChangedListener(this);

        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        if(s.equals(this.hitBlockField)){
            editor_activity activity = this.activity();
            if(activity != null){
                activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setPuntiPerColpo(Integer.valueOf(this.hitBlockField.getText().toString()));
            }
        }

        if(s.equals(this.puntiPartitaField)){
            editor_activity activity = this.activity();
            if(activity != null){
                activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setPuntiTerminazione(Integer.valueOf(this.puntiPartitaField.getText().toString()));
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar.equals(this.pv_pal_seek)){
            int value = this.pv_pal_seek.getProgress();
            if(this.pv_pal_label != null){
                this.pv_pal_label.setText(this.getContext().getText(R.string.parametri_fragment_velocita_palla) + ": " + value + "%");
            }
            editor_activity activity = this.activity();
            if(activity != null)
                activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setPercentualeIncrementoVelocitaPalla(value / 100.0f);
        }

        if(seekBar.equals(this.pv_pad_seek)){
            int value = this.pv_pad_seek.getProgress();
            if(this.pv_pad_label != null){
                this.pv_pad_label.setText(this.getContext().getText(R.string.parametri_fragment_velocita_paddle) + ": " + value + "%");
            }
            editor_activity activity = this.activity();
            if(activity != null)
                activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setPercentualeIncrementoVelocitaPaddle(value / 100.0f);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}