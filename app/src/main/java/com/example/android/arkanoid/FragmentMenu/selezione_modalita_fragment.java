package com.example.android.arkanoid.FragmentMenu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.android.arkanoid.R;
import com.example.android.arkanoid.modalita_activity;

public class selezione_modalita_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    private Button modalitaClassicaButton;
    private Button modalitaChaosButton;
    private Button modalitaSpacePaddle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selezione_modalita_fragment, container, false);
        view.setOnTouchListener(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.modalitaClassicaButton = view.findViewById(R.id.pulsanteModalitaClassica);
        this.modalitaChaosButton = view.findViewById(R.id.pulsanteModalitaChaos);
        this.modalitaSpacePaddle = view.findViewById(R.id.pulsanteModalitaSpacePaddle);

        if(this.modalitaClassicaButton != null)
            this.modalitaClassicaButton.setOnClickListener(this);
        if(this.modalitaChaosButton != null)
            this.modalitaChaosButton.setOnClickListener(this);
        if(this.modalitaSpacePaddle != null)
            this.modalitaSpacePaddle.setOnClickListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        if(v.equals(this.modalitaClassicaButton)){
            intent = new Intent(this.getContext(), modalita_activity.class);
            intent.putExtra(modalita_activity.EXTRA_MODALITA, modalita_activity.CODICE_MODALITA_CLASSICA);
        }

        if(v.equals(this.modalitaChaosButton)){
            intent = new Intent(this.getContext(), modalita_activity.class);
            intent.putExtra(modalita_activity.EXTRA_MODALITA, modalita_activity.CODICE_MODALITA_CHAOS);
        }

        if(v.equals(this.modalitaSpacePaddle)){
            intent = new Intent(this.getContext(), modalita_activity.class);
            intent.putExtra(modalita_activity.EXTRA_MODALITA, modalita_activity.CODICE_MODALITA_SPACE_PADDLE);
        }

        if(intent != null)
            this.startActivity(intent);

    }


}