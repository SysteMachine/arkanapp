package com.example.android.arkanoid.GameElements.SceneDefinite;

import android.view.MotionEvent;
import android.view.View;

import com.example.android.arkanoid.GameElements.ElementiBase.GameStatus;
import com.example.android.arkanoid.GameElements.ElementiBase.Stile;
import com.example.android.arkanoid.Multiplayer.ClientMultiplayer;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ModalitaMultiplayer extends AbstractModalita{
    private ClientMultiplayer client;


    public ModalitaMultiplayer(ClientMultiplayer client) {
        super(new Stile(), new GameStatus(3, 0, GameStatus.TOUCH));
        this.client = client;
    }

    @Override
    protected void logicaRipristinoPosizioni() {

    }

    @Override
    protected void logicaEliminazioneVita() {

    }

    @Override
    protected void logicaAvanzamentoLivello() {

    }

    @Override
    protected void iniziallizzazioneEntita(int screenWidth, int screenHeight) {

    }

    @Override
    protected void inizializzazioneAudio() {

    }

    @Override
    protected void inserimentoEntita() {

    }

    @Override
    public void touchEvent(Vector2D position, View v, MotionEvent e) {

    }

    @Override
    public void orientationChanged(float degree) {

    }
}
