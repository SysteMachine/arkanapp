package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVelocitaPalla;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractPowerUpMalus;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class BallSpeedDown extends AbstractPowerUpMalus {
    public BallSpeedDown(Vector2D posizione, Vector2D size, GameLoop gameLoop) {
        super(
                posizione,
                size,
                300,
                new AlterazioneVelocitaPalla(15000, 0.6f),
                R.drawable.alterazione_ball_speed_down,
                gameLoop
        );
        this.setName("BallSpeedDown");
    }
}
