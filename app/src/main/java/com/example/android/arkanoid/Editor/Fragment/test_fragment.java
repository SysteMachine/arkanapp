package com.example.android.arkanoid.Editor.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.android.arkanoid.Editor.LayerLivello;
import com.example.android.arkanoid.Editor.Livello;
import com.example.android.arkanoid.Editor.ModalitaTestLivello;
import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.ElementiBase.GameOverListener;
import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileAtzeco;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileFuturistico;
import com.example.android.arkanoid.GameElements.StiliDefiniti.StileSpaziale;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.VectorMat.Vector2D;
import com.example.android.arkanoid.editor_activity;

public class test_fragment extends Fragment implements View.OnTouchListener, GameOverListener, View.OnClickListener {
    private Button riavviaButton;

    private GameLoop gameLoop;
    private ModalitaTestLivello testLivello;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_fragment, container, false);

        view.setOnTouchListener(this);
        this.riavviaButton = view.findViewById(R.id.riavviaButton);
        if(this.riavviaButton != null)
            this.riavviaButton.setOnClickListener(this);

        this.gameLoop = new GameLoop(this.getContext(), 60, 720, 1280);
        this.gameLoop.setShowFPS(true);
        FrameLayout mainFrame = view.findViewById(R.id.mainFrame);
        editor_activity activity = this.activity();
        if(mainFrame != null && activity != null){
            mainFrame.addView(this.gameLoop);
            this.gameLoop.start();

            this.testLivello = new ModalitaTestLivello(
                    this.creazioneStilePartita(),
                    new GameStatus(5, 0, GameStatus.TOUCH),
                    activity.getLivello().getLayerLivello(activity.getLayerCorrente())
            );
            this.testLivello.setGameOverListener(this);

            this.gameLoop.addGameComponent(this.testLivello);
        }

        return view;
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

    /**
     * Crea lo stile della partita
     * @return Restituisce lo stile personalizzato
     */
    private Stile creazioneStilePartita(){
        Stile stile = new Stile();

        editor_activity activity = this.activity();
        if(activity != null){
            Livello l = activity.getLivello();
            LayerLivello ll = l.getLayerLivello(activity.getLayerCorrente());

            if(l.getIndiceStile() == StileSpaziale.ID_STILE)
                stile = new StileSpaziale();
            if(l.getIndiceStile() == StileFuturistico.ID_STILE)
                stile = new StileFuturistico();
            if(l.getIndiceStile() == StileAtzeco.ID_STILE)
                stile = new StileAtzeco();


            stile.setNumeroRigheMappa(ll.getRighe());
            stile.setNumeroColonneMappa(ll.getColonne());
            stile.setPercentualeDimensioneMappa(new Vector2D(1, ll.getAltezza() / 1280.0f));
            stile.setVelocitaInizialePalla(stile.getVelocitaInizialePalla() * ll.getPercentualeIncrementoVelocitaPalla());
            stile.setVelocitaInizialePaddle(stile.getVelocitaInizialePaddle() * ll.getPercentualeIncrementoVelocitaPaddle());
        }

        return stile;
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void gameOver(GameStatus status) {
        if(this.gameLoop != null){
            this.gameLoop.setUpdateRunning(false);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(this.riavviaButton)){
            editor_activity activity = this.activity();
            if(this.gameLoop != null && this.testLivello != null && activity != null){
                this.gameLoop.stop();
                this.gameLoop.setUpdateRunning(true);
                this.gameLoop.removeAll();
                this.testLivello = new ModalitaTestLivello(
                        this.creazioneStilePartita(),
                        new GameStatus(5, 0, GameStatus.TOUCH),
                        activity.getLivello().getLayerLivello(activity.getLayerCorrente())
                );
                this.testLivello.setGameOverListener(this);
                this.gameLoop.addGameComponent(this.testLivello);
                this.gameLoop.start();
            }
        }
    }
}