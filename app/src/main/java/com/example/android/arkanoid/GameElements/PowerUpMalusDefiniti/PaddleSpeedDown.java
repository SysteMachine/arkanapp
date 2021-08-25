package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVelocitaPaddle;
import com.example.android.arkanoid.GameElements.ElementiBase.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class PaddleSpeedDown extends PM {
    public PaddleSpeedDown(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "paddleSpeedDown",
                position,
                size,
                new Vector2D(300, 300),
                new Sprite(R.drawable.alterazione_paddle_down, gameLoop),
                new AlterazioneVelocitaPaddle(15000, 0.6f)
        );
    }
}
