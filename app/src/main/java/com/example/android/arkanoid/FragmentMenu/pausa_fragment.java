package com.example.android.arkanoid.FragmentMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.AudioUtil;
import com.example.android.arkanoid.modalita_activity;

public class pausa_fragment extends Fragment implements View.OnClickListener, View.OnTouchListener {
    private ToggleButton audioButton;
    private Button riprendiButton;
    private Button esciButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pausa_fragment, container, false);
        view.setOnTouchListener(this);

        this.audioButton = view.findViewById(R.id.audioButton);
        if(this.audioButton != null) {
            this.audioButton.setOnClickListener(this);
            this.audioButton.setChecked(AudioUtil.getGlobalAudio() == 0);
        }
        this.riprendiButton = view.findViewById(R.id.riprendiButton);
        if(this.riprendiButton != null)
            this.riprendiButton.setOnClickListener(this);
        this.esciButton = view.findViewById(R.id.esciButton);
        if(this.esciButton != null)
            this.esciButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.audioButton)){
            //Pulsante dell'audio
            if(this.audioButton.isChecked())
                AudioUtil.setGlobalAudio(0);
            else
                AudioUtil.setGlobalAudio(100);
        }

        if(v.equals(this.riprendiButton) && this.getActivity() != null){
            ((modalita_activity)this.getActivity()).nascondiMenuPausa();
        }

        if(v.equals(this.esciButton) && this.getActivity() != null){
            ((modalita_activity)this.getActivity()).tornaAlMenu();
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}