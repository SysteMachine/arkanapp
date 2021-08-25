package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneNumeroPalle;
import com.example.android.arkanoid.GameElements.ElementiBase.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class MultiBall extends PM{
    public MultiBall(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "multiBall",
                position,
                size,
                new Vector2D(400, 400),
                new Sprite(R.drawable.alterazione_multi_ball, gameLoop),
                new AlterazioneNumeroPalle(10000, 2)
        );
    }
}
