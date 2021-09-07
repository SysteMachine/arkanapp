package com.example.android.arkanoid.Editor.Fragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.ToggleButton;

import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.Editor.PannelloMappa;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.editor_activity;

public class mappa_fragment extends Fragment implements View.OnTouchListener, NumberPicker.OnValueChangeListener, View.OnClickListener{
    private NumberPicker altezzaPicker;
    private NumberPicker numeroColonnePicker;
    private NumberPicker numeroRighePicker;
    private NumberPicker vitaBloccoPicker;
    private ToggleButton infHealthButton;

    private GameLoop gameLoop;
    private PannelloMappa pannelloMappa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(this.gameLoop != null)
            this.gameLoop.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(this.gameLoop != null)
            this.gameLoop.start();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mappa_fragment, container, false);
        view.setOnTouchListener(this);

        ViewGroup containerMappa = view.findViewById(R.id.frameMappa);
        if(containerMappa != null){
            this.gameLoop = new GameLoop(this.getContext(), 10, 720, 720);
            this.gameLoop.setShowFPS(false);
            containerMappa.addView(this.gameLoop);
            this.gameLoop.start();
        }

        editor_activity activity = this.activity();
        if(activity != null){
            this.pannelloMappa = new PannelloMappa(activity.getLivello().getLayerLivello(activity.getLayerCorrente()));
            if(this.gameLoop != null)
                this.gameLoop.addGameComponent(this.pannelloMappa);
        }

        this.altezzaPicker = view.findViewById(R.id.altezzaMappaPicker);
        if(this.altezzaPicker != null){
            this.altezzaPicker.setMinValue(LayerLivello.MIN_ALTEZZA);
            this.altezzaPicker.setMaxValue(LayerLivello.MAX_ALTEZZA);
            if(activity != null)
                this.altezzaPicker.setValue(activity.getLivello().getLayerLivello(activity.getLayerCorrente()).getAltezza());
            this.altezzaPicker.setOnValueChangedListener(this);
        }

        this.numeroColonnePicker = view.findViewById(R.id.numeroColonnePicker);
        if(this.numeroColonnePicker != null){
            this.numeroColonnePicker.setMinValue(LayerLivello.MIN_COLONNE);
            this.numeroColonnePicker.setMaxValue(LayerLivello.MAX_COLONNE);
            if(activity != null)
                this.numeroColonnePicker.setValue(activity.getLivello().getLayerLivello(activity.getLayerCorrente()).getColonne());
            this.numeroColonnePicker.setOnValueChangedListener(this);
        }

        this.numeroRighePicker = view.findViewById(R.id.numeroRighePicker);
        if(this.numeroRighePicker != null){
            this.numeroRighePicker.setMinValue(LayerLivello.MIN_RIGHE);
            this.numeroRighePicker.setMaxValue(LayerLivello.MAX_RIGHE);
            if(activity != null)
                this.numeroRighePicker.setValue(activity.getLivello().getLayerLivello(activity.getLayerCorrente()).getRighe());
            this.numeroRighePicker.setOnValueChangedListener(this);
        }

        this.vitaBloccoPicker = view.findViewById(R.id.vitaBrickPicker);
        if(this.vitaBloccoPicker != null){
            this.vitaBloccoPicker.setMinValue(0);
            this.vitaBloccoPicker.setMaxValue(10);
            if(activity != null)
                this.vitaBloccoPicker.setValue(0);
            this.vitaBloccoPicker.setOnValueChangedListener(this);
        }

        this.infHealthButton = view.findViewById(R.id.infHealthButton);
        if(this.infHealthButton != null)
            this.infHealthButton.setOnClickListener(this);

        return view;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        editor_activity activity = this.activity();
        if(picker.equals(this.altezzaPicker) && activity != null){
            activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setAltezza(newVal);
        }
        if(picker.equals(this.numeroColonnePicker) && activity != null){
            activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setColonne(newVal);
        }
        if(picker.equals(this.numeroRighePicker) && activity != null){
            activity.getLivello().getLayerLivello(activity.getLayerCorrente()).setRighe(newVal);
        }
        if(picker.equals(this.vitaBloccoPicker) && this.pannelloMappa != null && this.infHealthButton != null){
            if(!this.infHealthButton.isChecked()){
                this.pannelloMappa.setVitaBrick(newVal);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.infHealthButton) && this.vitaBloccoPicker != null){
            if(this.infHealthButton.isChecked())
                this.pannelloMappa.setVitaBrick(-1);
            else
                this.pannelloMappa.setVitaBrick(this.vitaBloccoPicker.getValue());
        }
    }
}