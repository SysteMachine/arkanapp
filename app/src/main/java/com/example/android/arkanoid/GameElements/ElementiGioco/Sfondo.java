package com.example.android.arkanoid.GameElements.ElementiGioco;

import com.example.android.arkanoid.GameElements.ElementiBase.AbstractEntity;
import com.example.android.arkanoid.Util.SpriteUtil.Sprite;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Sfondo extends AbstractEntity {
    public Sfondo(Vector2D position, Vector2D size, Sprite sprite) {
        super(
                "sfondo",
                position,
                new Vector2D(0, 0),
                size,
                new Vector2D(0, 0),
                sprite
        );
    }
}
