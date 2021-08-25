package com.example.android.arkanoid.GameElements.PowerUpMalusDefiniti;

import com.example.android.arkanoid.GameCore.GameLoop;
import com.example.android.arkanoid.GameElements.AlterazioniDefinite.AlterazioneVitaBlocchi;
import com.example.android.arkanoid.GameElements.ElementiBase.PM;
import com.example.android.arkanoid.R;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class BrickHealthUp extends PM {
    public BrickHealthUp(Vector2D position, Vector2D size, GameLoop gameLoop) {
        super(
                "brickHealthUp",
                position,
                size,
                new Vector2D(200, 200),
                new Sprite(R.drawable.alterazione_brick_health_up, gameLoop),
                new AlterazioneVitaBlocchi()
        );
    }
}
