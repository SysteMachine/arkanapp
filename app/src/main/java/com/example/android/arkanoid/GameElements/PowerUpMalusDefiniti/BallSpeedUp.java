package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVelocitaPalla;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractPM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class BallSpeedUp extends AbstractPM {
    public BallSpeedUp(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "ballSpeedUp",
                position,
                size,
                new Vector2D(300, 300),
                new Sprite(R.drawable.alterazione_ball_speed_up, gameLoop),
                new AlterazioneVelocitaPalla(15000, 1.4f)
        );
    }
}
