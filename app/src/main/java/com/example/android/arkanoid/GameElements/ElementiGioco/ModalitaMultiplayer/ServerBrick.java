package com.example.android.arkanoid.GameElements.ElementiGioco.ModalitaMultiplayer;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.android.arkanoid.GameElements.ElementiGioco.Brick;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class ServerBrick extends Brick {
    public ServerBrick(Vector2D position, Vector2D size, int health) {
        super(position, size, null, null, health);
    }

    @Override
    public void drawEntity(Canvas canvas, Paint paint) {}
}
