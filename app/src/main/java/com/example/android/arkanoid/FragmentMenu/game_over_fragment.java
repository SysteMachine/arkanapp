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
import android.widget.Toast;

import com.example.android.arkanoid.DataStructure.RecordSalvataggio;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.QueryExecutor;
import com.example.android.arkanoid.modalita_activity;

public class game_over_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private TextView riepilogoView;
    private Button nuovaPartitaButton;
    private Button condividiButton;
    private Button menuButton;

    private boolean risulatoPubblicato;                  //Flag per controllare che il risultato sia stato pubblicato

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over_fragment, container, false);
        view.setOnTouchListener(this);

        if(savedInstanceState != null)
            risulatoPubblicato = savedInstanceState.getBoolean("risultatoPubblicato");

        this.riepilogoView = view.findViewById(R.id.riepilogoPunti);
        this.nuovaPartitaButton = view.findViewById(R.id.nuovaPartitaButton);
        this.condividiButton = view.findViewById(R.id.pubblicaRisultatiButton);
        this.menuButton = view.findViewById(R.id.menuPrincipaleButton);

        if (this.nuovaPartitaButton != null)
            this.nuovaPartitaButton.setOnClickListener(this);
        if (this.condividiButton != null) {
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
            if (recordSalvataggio.isLogin() && !this.risulatoPubblicato)
                this.condividiButton.setOnClickListener(this);
            else
                this.condividiButton.setVisibility(View.GONE);
        }
        if (this.menuButton != null)
            this.menuButton.setOnClickListener(this);
        if (this.riepilogoView != null && modalita_activity.modalita != null) {
            int punteggio = modalita_activity.modalita.getStatus().getPunteggio();
            this.riepilogoView.setText(punteggio + " " + this.getResources().getQuantityString(R.plurals.fragment_game_over_punteggio, punteggio));
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("risultatoPubblicato", this.risulatoPubblicato);
    }

    @Override
    public void onClick(View v) {
        Activity activity = this.getActivity();

        if (v.equals(this.menuButton)) {
            if (activity != null) {
                modalita_activity mActivity = (modalita_activity) activity;
                mActivity.tornaAlMenu();
            }
        }

        if (v.equals(this.nuovaPartitaButton)) {
            if (activity != null) {
                modalita_activity mActivity = (modalita_activity) activity;
                mActivity.caricaModalita();
            }
        }

        if (v.equals(this.condividiButton)) {
            if (modalita_activity.modalita != null && modalita_activity.modalita.getStatus() != null) {
                RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
                if (recordSalvataggio.isLogin()) {
                    try {
                        if (QueryExecutor.pubblicaPunteggio(modalita_activity.modalita.getStatus().getPunteggio(), modalita_activity.modalita.getCodiceModalita(), recordSalvataggio.getEmail())) {
                            Toast.makeText(this.getContext(), this.getResources().getString(R.string.fragment_game_over_pubblicato), Toast.LENGTH_LONG).show();
                            this.condividiButton.setVisibility(View.GONE);
                            this.risulatoPubblicato = true;
                        }else
                            Toast.makeText(this.getContext(), this.getResources().getString(R.string.fragment_game_over_non_pubblicato), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        return true;
    }
}