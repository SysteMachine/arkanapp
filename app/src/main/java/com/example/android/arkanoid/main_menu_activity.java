package com.example.android.arkanoid;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.android.arkanoid.FragmentMenu.selezione_modalita_fragment;
import com.example.android.arkanoid.Util.AbstractAnimatorListener;

public class main_menu_activity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    private FrameLayout fragmentLayout;                     //Posizione del fragment
    private ConstraintLayout mainFrame;                     //Main frame dell'activity
    private FrameLayout frameContrasto;                     //Frame di contrasto

    private static Fragment fragmentCorrente;               //Fragment correntemente aperto

    private Button pulsanteGioca;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        this.fragmentLayout = findViewById(R.id.containerFragment);
        this.mainFrame = findViewById(R.id.mainFrame);
        this.frameContrasto = findViewById(R.id.frameContrasto);
        if(this.frameContrasto != null)
            this.frameContrasto.setOnTouchListener(this);

        this.pulsanteGioca = findViewById(R.id.pulsanteGioca);
        if(this.pulsanteGioca != null)
            this.pulsanteGioca.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Se il fragment è visibile impostiamo il frag di ripristino
        if(this.fragmentCorrente != null)
            outState.putBoolean("FRAGMENT_VISIBLE", true);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState.getBoolean("FRAGMENT_VISIBLE")){
            //Se il fragment era visibile prima della ricreazione dell'interfaccia allora reinseriamo il fragment
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.containerFragment, this.fragmentCorrente)
                    .commit();

            //Rendiamo visibile il frame di contrasto
            if(this.frameContrasto != null)
                this.frameContrasto.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.hideFragment();
    }

    /**
     * Mostra il fragment
     * @param fragmentClass Classe del fragment da instanziare
     * @param idFragmentLayout Id del layout che conterrà il fragment
     */
    public void showFragment(Class<? extends Fragment> fragmentClass, int idFragmentLayout){
        try{
            if(this.fragmentCorrente == null){
                //Se nessun fragment è visibile

                //Mostrimao il fragment di contrasto
                if(this.frameContrasto != null)
                    this.frameContrasto.setVisibility(View.VISIBLE);

                //Inseriamo il nuovo fragment
                Fragment fragment = fragmentClass.newInstance();
                this.fragmentCorrente = fragment;
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .add(idFragmentLayout, fragment)
                        .commit();

                //Avviamo l'animazione di apertura
                if(fragmentLayout != null){
                    Animator animation = AnimatorInflater.loadAnimator(this, R.animator.open_animation);
                    animation.setTarget(this.fragmentLayout);
                    animation.start();
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    /**
     * Nasconde e cancella il fragment
     */
    public void hideFragment(){
        //Listener per la chiusura del fragment
        class AL extends AbstractAnimatorListener {
            private AppCompatActivity activity;
            public AL(AppCompatActivity activity){
                this.activity = activity;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(main_menu_activity.fragmentCorrente != null){
                    //Se il fragment è visibile lo nasconde
                    this.activity.getSupportFragmentManager()
                            .beginTransaction()
                            .remove(main_menu_activity.fragmentCorrente)
                            .commit();
                }
                main_menu_activity.fragmentCorrente = null;

                //Nasconde il frame di contrasto
                if(((main_menu_activity)this.activity).frameContrasto != null)
                    ((main_menu_activity)this.activity).frameContrasto.setVisibility(View.GONE);
            }
        }

        //Avvia l'animazione di chiusura e imposta il listener per eliminare il fragment alla fine dell'animazione
        if(fragmentLayout != null){
            Animator animation = AnimatorInflater.loadAnimator(this, R.animator.close_animation);
            animation.setTarget(this.fragmentLayout);
            animation.addListener(new AL(this));
            animation.start();
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.pulsanteGioca))
            this.showFragment(selezione_modalita_fragment.class, R.id.containerFragment);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Se premuto il frame di contrasto viene nascosto il fragment
        if(event.getAction() == MotionEvent.ACTION_UP)
            this.hideFragment();
        return true;
    }
}