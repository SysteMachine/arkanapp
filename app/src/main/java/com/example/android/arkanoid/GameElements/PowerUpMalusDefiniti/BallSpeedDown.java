package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVelocitaPalla;
import com.example.android.arkanoid.GameElements.ElementiGioco.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class BallSpeedDown extends PM {
    public BallSpeedDown(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "ballSpeedDown",
                position,
                size,
                new Vector2D(300, 300),
                new Sprite(R.drawable.alterazione_ball_speed_down, gameLoop),
                new AlterazioneVelocitaPalla(15000, 0.6f)
        );
    }
}
