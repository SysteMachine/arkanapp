package com.example.android.arkanoid.FragmentMenu;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.arkanoid.R;
import com.example.android.arkanoid.modalita_activity;

public class game_over_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private TextView riepilogoView;
    private Button nuovaPartitaButton;
    private Button condividiButton;
    private Button menuButton;

    private int punti;                      //Punti effettuati dal giocatore

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over_fragment, container, false);

        if(savedInstanceState != null){
            this.punti = savedInstanceState.getInt("POINTS");
        }

        if(view != null) {
            view.setOnTouchListener(this);
            this.riepilogoView = view.findViewById(R.id.riepilogoPunti);
            if(this.riepilogoView != null){
                this.riepilogoView.setText(this.punti + " " + this.getResources().getQuantityString(R.plurals.fragment_game_over_punteggio, this.punti));
            }
            this.nuovaPartitaButton = view.findViewById(R.id.nuovaPartitaButton);
            if(this.nuovaPartitaButton != null)
                this.nuovaPartitaButton.setOnClickListener(this);
            this.condividiButton = view.findViewById(R.id.pubblicaRisultatiButton);
            if(this.condividiButton != null)
                this.condividiButton.setOnClickListener(this);
            this.menuButton = view.findViewById(R.id.menuPrincipaleButton);
            if(this.menuButton != null)
                this.menuButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("POINTS", this.punti);
    }

    /**
     * Imposta la stringa di riepilogo dei punti
     * @param punti Punti da visualizzare
     */
    public void setRiepilogoPunti(int punti){
        this.punti = punti;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.menuButton)) {
            Activity activity = this.getActivity();
            if(activity != null){
                modalita_activity mActivity = (modalita_activity)activity;
                mActivity.tornaAlMenu();
            }
        }

        if(v.equals(this.nuovaPartitaButton)){
            Activity activity = this.getActivity();
            if(activity != null){
                modalita_activity mActivity = (modalita_activity)activity;
                mActivity.nascondiMenuGameOver();
                mActivity.caricaModalita();
            }
        }
    }
}