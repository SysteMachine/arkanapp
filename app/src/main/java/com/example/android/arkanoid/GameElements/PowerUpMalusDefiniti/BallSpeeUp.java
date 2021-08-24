package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVelocitaPalla;
import com.example.android.arkanoid.GameElements.BaseElements.AbstractPowerUpMalus;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class BallSpeeUp extends AbstractPowerUpMalus {
    public BallSpeeUp(Vector2D posizione, Vector2D size, GameLoop gameLoop) {
        super(
                posizione,
                size,
                300,
                new AlterazioneVelocitaPalla(15000, 1.1f),
                R.drawable.alterazione_ball_speed_up,
                gameLoop
        );

        this.setName("ballSpeedUp");
    }
}
