package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVelocitaPaddle;
import com.example.android.arkanoid.GameElements.ElementiBase.AbstractAlterazione;
import com.example.android.arkanoid.GameElements.ElementiBase.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class PaddleSpeedUp extends PM {

    public PaddleSpeedUp(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "paddleSpeedUp",
                position,
                size,
                new Vector2D(600, 600),
                new Sprite(R.drawable.alterazione_paddle_speed_up, gameLoop),
                new AlterazioneVelocitaPaddle(15000, 1.4f)
        );
    }
}
