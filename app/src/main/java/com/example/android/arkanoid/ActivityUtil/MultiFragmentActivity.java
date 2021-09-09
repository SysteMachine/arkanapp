package com.example.android.arkanoid.ActivityUtil;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.android.arkanoid.R;

public class MultiFragmentActivity extends SoundControlActivity implements View.OnTouchListener, Animator.AnimatorListener {
    private FrameLayout frameContrasto;                   //Frame di contrasto
    private FrameLayout containerFragment;                //Contenitore del fragment

    private boolean fragmentVisible;                      //Flag per il controllo della visibilità del fragment

    protected Animator animazioneAperturaFragment;        //Animazione di apertura del fragment
    protected Animator animazioneChiusuraFragment;        //Animazione di chiusura del fragment

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.animazioneAperturaFragment = AnimatorInflater.loadAnimator(this, R.animator.open_animation);
        this.animazioneChiusuraFragment = AnimatorInflater.loadAnimator(this, R.animator.close_animation);
        this.fragmentVisible = false;
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        this.creaStrutturaFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("fragmentVisible", this.fragmentVisible);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.fragmentVisible = savedInstanceState.getBoolean("fragmentVisible");
        this.cambiaVisibilitaContainer(fragmentVisible);
    }

    /**
     * Restituisce la view root dell'activity
     * @return Restituisce la viewgroup parent
     */
    private ViewGroup getRootView(){
        return (ViewGroup) ((ViewGroup)this.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * Crea il frame di contrasto dell'activity
     * @return Restituisce un FrameLayout configurato per essere usato come pannello di contrasto
     */
    private FrameLayout creaFrameContrasto(){
        FrameLayout frameContrasto = new FrameLayout(this);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)frameContrasto.getLayoutParams();
        if(params != null){
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            frameContrasto.setLayoutParams(params);
        }

        frameContrasto.setId(R.id.frame_contrasto);
        frameContrasto.setZ(10000);
        frameContrasto.setAlpha(0.5f);
        frameContrasto.setBackgroundColor(Color.rgb(0, 0, 0));
        frameContrasto.setVisibility(View.GONE);

        return frameContrasto;
    }

    /**
     * Crea il container del fragment
     * @return Fragment Container
     */
    private FrameLayout creaFragmentContainer(){
        FrameLayout fragmentContainer = new FrameLayout(this);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)fragmentContainer.getLayoutParams();
        if(params != null){
            params.height = 200;
            params.width = 200;
            fragmentContainer.setLayoutParams(params);
        }

        fragmentContainer.setId(R.id.container_fragment);
        fragmentContainer.setZ(11000);
        fragmentContainer.setVisibility(View.GONE);

        return fragmentContainer;
    }

    /**
     * Crea la struttura che ospita il fragment
     */
    private void creaStrutturaFragment(){
        this.containerFragment = this.creaFragmentContainer();
        this.frameContrasto = this.creaFrameContrasto();
        this.frameContrasto.setOnTouchListener(this);

        ViewGroup rootView = this.getRootView();
        rootView.setId(View.generateViewId());
        if(rootView != null){
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.frameContrasto.setLayoutParams(params);
            rootView.addView(this.frameContrasto);

            params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
            params.rightMargin = this.getResources().getDimensionPixelSize(R.dimen.ten_dp);
            params.leftMargin = this.getResources().getDimensionPixelSize(R.dimen.ten_dp);
            params.bottomToBottom = rootView.getId();
            params.leftToLeft = rootView.getId();
            params.rightToRight = rootView.getId();
            params.topToTop = rootView.getId();
            this.containerFragment.setLayoutParams(params);
            rootView.addView(this.containerFragment);
        }
    }

    /**
     * Cambia la visibilità del container
     * @param fragmentVisible Visibilità da impostare
     */
    private void cambiaVisibilitaContainer(boolean fragmentVisible){
        if(fragmentVisible){
            this.frameContrasto.setVisibility(View.VISIBLE);
            this.containerFragment.setVisibility(View.VISIBLE);
        }else{
            this.frameContrasto.setVisibility(View.GONE);
            this.containerFragment.setVisibility(View.GONE);
        }
    }

    /**
     * Mostra il fragment sullo schermo
     * @param fragment Fragment da visualizzare
     * @param showAnimation Flag di visualizzazione dell'animazione
     */
    public void mostraFragment(Fragment fragment, boolean showAnimation){
        if(!this.fragmentVisible){
            if(fragment != null){
                fragment.setRetainInstance(true);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(this.containerFragment.getId(), fragment)
                        .commit();
                this.fragmentVisible = true;
                this.cambiaVisibilitaContainer(this.fragmentVisible);
                if(showAnimation){
                    this.animazioneAperturaFragment.setTarget(this.containerFragment);
                    this.animazioneAperturaFragment.start();
                }
            }
        }
    }

    /**
     * Rimuove dal container il fragment attivo
     */
    private void rimuoviFragmentAttivo(){
        Fragment fragmentAttivo = this.getSupportFragmentManager().findFragmentById(this.containerFragment.getId());
        if(fragmentAttivo != null){
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragmentAttivo)
                    .commit();
        }
    }

    /**
     * Nasconde il fragment attivo sul pannello
     * @param showAnimation Flad di animazione visualizzata
     */
    public void nascondiFragment(boolean showAnimation){
        if(this.fragmentVisible){
            if(!showAnimation){
                this.rimuoviFragmentAttivo();
                this.fragmentVisible = false;
            }else{
                this.animazioneChiusuraFragment.removeAllListeners();
                this.animazioneChiusuraFragment.addListener(this);
                this.animazioneChiusuraFragment.setTarget(this.containerFragment);
                this.animazioneChiusuraFragment.start();
            }
        }
    }

    /**
     * Evento invocato quando viene toccato il frame di contrasto
     * @param event Evento
     */
    public void frameContrastoToccato(MotionEvent event){}

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        if(v.equals(this.frameContrasto))
            this.frameContrastoToccato(event);
        return true;
    }

    //Gestione delle animazioni

    @Override
    public void onAnimationStart(Animator animation) {}

    @Override
    public void onAnimationEnd(Animator animation) {
        this.rimuoviFragmentAttivo();
        this.fragmentVisible = false;
        this.cambiaVisibilitaContainer(this.fragmentVisible);
    }

    @Override
    public void onAnimationCancel(Animator animation) {}

    @Override
    public void onAnimationRepeat(Animator animation) {}
}
