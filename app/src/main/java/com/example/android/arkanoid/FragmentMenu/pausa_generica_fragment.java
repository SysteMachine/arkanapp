package com.example.android.arkanoid.FragmentMenu;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.android.arkanoid.ActivityUtil.PausaEventListener;
import com.example.android.arkanoid.R;

public class pausa_generica_fragment extends Fragment implements View.OnTouchListener, View.OnClickListener {
    public static final int PAUSA = 0;
    public static final int GAMEOVER = 1;

    private int tipoPausa;                  //Tipo di pausa richiesta
    private PausaEventListener listener;    //Listener della pausa

    private ImageView immagineLabel;
    private Button primoPulsante;
    private Button secondoPulsante;


    /**
     * Carica lo stile della pausa
     */
    private void stilePausa(){
        if(this.immagineLabel != null)
            this.immagineLabel.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.pause_scritta));
        if(this.primoPulsante != null)
            this.primoPulsante.setText(this.getResources().getText(R.string.pausa_generica_fragment_pausa_primo_pulsante));
        if(this.secondoPulsante != null)
            this.secondoPulsante.setText(this.getResources().getText(R.string.pausa_generica_fragment_pausa_secondo_pulsante));
    }

    /**
     * Carica lo stile del gameOver
     */
    private void stileGameOver(){
        if(this.immagineLabel != null)
            this.immagineLabel.setImageDrawable(ContextCompat.getDrawable(this.getContext(), R.drawable.game_over_scritta));
        if(this.primoPulsante != null)
            this.primoPulsante.setText(this.getResources().getText(R.string.pausa_generica_fragment_gameover_primo_pulsante));
        if(this.secondoPulsante != null)
            this.secondoPulsante.setText(this.getResources().getText(R.string.pausa_generica_fragment_gameover_secondo_pulsante));
    }

    /**
     * Cambia la vista del menu
     */
    private void cambiaVistaPausa(){
        switch(this.tipoPausa){
            case 0:
                stilePausa();
                break;
            case 1:
                stileGameOver();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pausa_generica_fragment, container, false);
        view.setOnTouchListener(this);

        Bundle args = this.getArguments();
        if(args != null)
            this.tipoPausa = args.getInt("tipoPausa");
        else
            this.tipoPausa = pausa_generica_fragment.PAUSA;

        this.immagineLabel = view.findViewById(R.id.titleImage);
        this.primoPulsante = view.findViewById(R.id.firstButton);
        this.secondoPulsante = view.findViewById(R.id.secondButton);

        if(this.primoPulsante != null)
            this.primoPulsante.setOnClickListener(this);
        if(this.secondoPulsante != null)
            this.secondoPulsante.setOnClickListener(this);

        this.cambiaVistaPausa();

        return view;
    }

    @Override
    public void onClick(View v) {
        if(this.listener != null){
            if(v.equals(this.primoPulsante))
                this.listener.event("PRIMO");
            if(v.equals(this.secondoPulsante))
                this.listener.event("SECONDO");
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    //Beam
    public int getTipoPausa() {
        return tipoPausa;
    }

    public void setTipoPausa(int tipoPausa) {
        if(tipoPausa == 0 || tipoPausa == 1){
            this.tipoPausa = tipoPausa;
            this.cambiaVistaPausa();
        }
    }

    public PausaEventListener getListener() {
        return listener;
    }

    public void setListener(PausaEventListener listener) {
        this.listener = listener;
    }
}