package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;

import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneLarghezzaPaddle;
import com.example.android.arkanoid.GameElements.ElementiBase.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class PaddleUp extends PM {
    public PaddleUp(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "paddleUp",
                position,
                size,
                new Vector2D(300, 300),
                new Sprite(R.drawable.alterazione_paddle_up, gameLoop),
                new AlterazioneLarghezzaPaddle(5000, 1.3f)
        );
    }
}
