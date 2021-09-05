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
import com.example.android.arkanoid.Util.DBUtil;
import com.example.android.arkanoid.modalita_activity;

public class game_over_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private final String QUERY_PUNTEGGIO = "INSERT INTO punteggio (punteggio_punteggio, punteggio_modalita, punteggio_user_email) VALUES (PUNTEGGIO, MODALITA, EMAIL)";

    private TextView riepilogoView;
    private Button nuovaPartitaButton;
    private Button condividiButton;
    private Button menuButton;

    private static boolean esitoPubblicato = false;     //Variabile statica in quanto il fragment durante la rotazione viene cancellato e ricreato manualmente e non da un processo di Android

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over_fragment, container, false);
        view.setOnTouchListener(this);

        this.riepilogoView = view.findViewById(R.id.riepilogoPunti);
        this.nuovaPartitaButton = view.findViewById(R.id.nuovaPartitaButton);
        this.condividiButton = view.findViewById(R.id.pubblicaRisultatiButton);
        this.menuButton = view.findViewById(R.id.menuPrincipaleButton);

        if(this.nuovaPartitaButton != null)
            this.nuovaPartitaButton.setOnClickListener(this);
        if(this.condividiButton != null) {
            RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
            if(recordSalvataggio.isLogin() || !game_over_fragment.esitoPubblicato)
                this.condividiButton.setOnClickListener(this);
            else
                this.condividiButton.setVisibility(View.GONE);
        }
        if(this.menuButton != null)
            this.menuButton.setOnClickListener(this);
        if(this.riepilogoView != null && modalita_activity.modalita != null){
            int punteggio = modalita_activity.modalita.getStatus().getPunteggio();
            this.riepilogoView.setText(punteggio + " " + this.getResources().getQuantityString(R.plurals.fragment_game_over_punteggio, punteggio));
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.menuButton)) {
            Activity activity = this.getActivity();
            if(activity != null){
                modalita_activity mActivity = (modalita_activity)activity;
                mActivity.tornaAlMenu();
                game_over_fragment.esitoPubblicato = false;
            }
        }

        if(v.equals(this.nuovaPartitaButton)){
            Activity activity = this.getActivity();
            if(activity != null){
                modalita_activity mActivity = (modalita_activity)activity;
                mActivity.caricaModalita();
                game_over_fragment.esitoPubblicato = false;
            }
        }

        if(v.equals(this.condividiButton)){
            Activity activity = this.getActivity();
            if(activity != null && modalita_activity.modalita != null && modalita_activity.modalita.getStatus() != null){
                RecordSalvataggio recordSalvataggio = new RecordSalvataggio(this.getContext());
                if(recordSalvataggio.isLogin()){
                    String query = DBUtil.repalceJolly(this.QUERY_PUNTEGGIO, "PUNTEGGIO", modalita_activity.modalita.getStatus().getPunteggio());
                    query = DBUtil.repalceJolly(query, "MODALITA", modalita_activity.modalita.getCodiceModalita());
                    query = DBUtil.repalceJolly(query, "EMAIL", recordSalvataggio.getEmail());
                    try{
                        String esitoQuery = DBUtil.executeQuery(query);
                        this.condividiButton.setVisibility(View.GONE);
                        Toast toast = Toast.makeText(this.getContext(), "", Toast.LENGTH_LONG);
                        if(!esitoQuery.equals("ERROR")){
                            toast.setText(this.getResources().getString(R.string.fragment_game_over_pubblicato));
                        }else
                            toast.setText(this.getResources().getString(R.string.fragment_game_over_non_pubblicato));
                        toast.show();
                        game_over_fragment.esitoPubblicato = true;
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}