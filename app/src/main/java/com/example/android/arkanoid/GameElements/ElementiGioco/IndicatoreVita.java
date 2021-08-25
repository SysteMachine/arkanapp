package com.example.android.arkanoid.GameElements.ElementiGioco;

import com.example.android.arkanoid.GameElements.ElementiBase.Entity;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class IndicatoreVita extends Entity {
    public IndicatoreVita(Vector2D position, Vector2D size, Sprite sprite) {
        super(
                "indicatoreVita",
                position,
                new Vector2D(0, 0),
                size,
                new Vector2D(0, 0),
                sprite
        );
    }
}
