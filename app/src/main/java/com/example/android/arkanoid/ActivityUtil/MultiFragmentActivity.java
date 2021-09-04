package com.example.android.arkanoid.ActivityUtil;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.example.android.arkanoid.R;

public class MultiFragmentActivity extends AppCompatActivity implements View.OnTouchListener {
    private boolean fragmentVisible;                //Flag per controllare al momento del ripristino se il fragment era visibile
    private String fragmentClassName;               //Nome della classe del fragment
    private int fragmentLayoutId;                   //Id del fragment layout

    protected Fragment fragmentAttivo;              //Fragment attualmente attivo nel pannello
    protected FrameLayout frameContrasto;           //Frame di contrasto del' fragment
    protected FrameLayout fragmentLayout;           //Posizione per posizionare il fragment
    protected Animator openFragmentAnimation;       //Animazione di apertura del fragment
    protected Animator closeFragmentAnimation;      //Animazione di chiusura del fragment

    /**
     * Carica gli elementi essenziali per il funzionamento del pannello
     * @param savedInstanceState statoSalvato
     */
    protected void loadEssentials(Bundle savedInstanceState){
        if(savedInstanceState != null){
            this.fragmentVisible = savedInstanceState.getBoolean("FRAGMENT_VISIBLE");
            if(this.fragmentVisible){
                this.fragmentClassName = savedInstanceState.getString("FRAGMENT_CLASS_NAME");
                this.fragmentLayoutId = savedInstanceState.getInt("FRAGMENT_LAYOUT_ID");
                try{
                    Class classe = Class.forName(this.fragmentClassName);
                    this.showFragment(classe, false);
                }catch (Exception e){e.printStackTrace();}
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("FRAGMENT_VISIBLE", this.fragmentVisible);
        outState.putString("FRAGMENT_CLASS_NAME", this.fragmentClassName);
        outState.putInt("FRAGMENT_LAYOUT_ID", this.fragmentLayoutId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.hideFragment(false, false);
    }

    /**
     * Carica il layout dove verrà inserito il fragment
     * @param id Id del container del fragment
     */
    protected void loadFragmentLayout(int id){
        this.fragmentLayout = this.findViewById(id);
        this.openFragmentAnimation = AnimatorInflater.loadAnimator(this, R.animator.open_animation);
        this.closeFragmentAnimation = AnimatorInflater.loadAnimator(this, R.animator.close_animation);
    }

    /**
     * Carica il frame di contrasto
     * @param id Id del frame di contrasto
     */
    protected void loadFrameContrasto(int id){
        this.frameContrasto = this.findViewById(id);
        if(this.frameContrasto != null) {
            this.frameContrasto.setOnTouchListener(this);
            this.frameContrasto.setVisibility(View.GONE);
        }
    }

    /**
     * Rende visibile il fragment
     * @param classeFragment Classe del fragment da visualizzare
     * @param showAnimation Flag di visualizzazione dell'animazione
     * @return Restituisce false se l'operazione fallisce, true in caso contrario
     */
    public boolean showFragment(Class<? extends Fragment> classeFragment, boolean showAnimation){
        boolean esito = false;

        if(this.fragmentLayout != null && classeFragment != null){
            //Se è possibile inserire il fragment
            try{
                Fragment fragment = classeFragment.newInstance();
                if(this.frameContrasto != null)
                    this.frameContrasto.setVisibility(View.VISIBLE);

                if(showAnimation && this.openFragmentAnimation != null){
                    this.openFragmentAnimation.setTarget(this.fragmentLayout);
                    this.openFragmentAnimation.start();
                }

                this.getSupportFragmentManager()
                        .beginTransaction()
                        .add(this.fragmentLayout.getId(), fragment)
                        .commit();

                this.fragmentAttivo = fragment;
                this.fragmentVisible = true;
                this.fragmentClassName = this.fragmentAttivo.getClass().getName();
                this.fragmentLayoutId = this.fragmentLayout.getId();
                esito = true;
            }catch (Exception e){e.printStackTrace();}
        }

        return esito;
    }

    /**
     * Nasconde il fragment
     * @param showAnimation Flag di visualizzazione dell'animazione
     * @param resetStatus Flag per il reset dello status del fragment
     */
    public void hideFragment(boolean showAnimation, boolean resetStatus){
        if(this.fragmentAttivo != null){
            //Solo se il fragment è visibile
            if(showAnimation && this.closeFragmentAnimation != null && this.fragmentLayout != null){
                //Se è possibile eseguire l'animazione e l'utente l'ha richiesta la esegue
                class AL extends AbstractAnimatorListener{
                    private MultiFragmentActivity activity;
                    public AL(MultiFragmentActivity activity){
                        this.activity = activity;
                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(this.activity.frameContrasto != null)
                            this.activity.frameContrasto.setVisibility(View.GONE);
                        this.activity.getSupportFragmentManager()
                                .beginTransaction()
                                .remove(this.activity.fragmentAttivo)
                                .commit();
                        if(resetStatus){
                            this.activity.fragmentVisible = false;
                            this.activity.fragmentAttivo = null;
                            this.activity.fragmentLayoutId = -1;
                            this.activity.fragmentClassName = null;
                        }
                    }
                }
                this.closeFragmentAnimation.setTarget(this.fragmentLayout);
                this.closeFragmentAnimation.removeAllListeners();
                this.closeFragmentAnimation.addListener(new AL(this));
                this.closeFragmentAnimation.start();
            }else{
                //Altrimenti nasconde semplicemente il fragment
                if(this.frameContrasto != null)
                    this.frameContrasto.setVisibility(View.GONE);
                this.getSupportFragmentManager()
                        .beginTransaction()
                        .remove(this.fragmentAttivo)
                        .commit();
                if(resetStatus){
                    this.fragmentVisible = false;
                    this.fragmentAttivo = null;
                    this.fragmentLayoutId = -1;
                    this.fragmentClassName = null;
                }
            }
        }
    }

    /**
     * Nasconde il fragment
     * @param showAnimation Flag di visualizzazione dell'animazione
     */
    public void hideFragment(boolean showAnimation){
        this.hideFragment(showAnimation, true);
    }

    /**
     * Evento invocato ogni volta in cui il frame di contrasto viene toccato
     * @param v Vista che viene toccata
     * @param e Evento
     */
    protected void onFrameContrastoTouched(View v, MotionEvent e){
        if(v.equals(this.frameContrasto) && e.getAction() == MotionEvent.ACTION_UP)
            this.hideFragment(true);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.onFrameContrastoTouched(v, event);
        return true;
    }
}
