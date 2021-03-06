package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneLarghezzaPaddle;
import com.example.android.arkanoid.GameElements.ElementiGioco.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class PaddleDown extends PM {
    public PaddleDown(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "paddleUp",
                position,
                size,
                new Vector2D(300, 300),
                new Sprite(R.drawable.alterazione_paddle_down, gameLoop),
                new AlterazioneLarghezzaPaddle(5000, 0.7f)
        );
    }
}
