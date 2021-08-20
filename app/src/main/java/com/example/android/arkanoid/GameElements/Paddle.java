package com.example.android.arkanoid.GameElements;

import com.example.android.arkanoid.GameElements.BaseElements.AbstractEntity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Paddle extends AbstractEntity {
    private final Vector2D startPosition;

    public Paddle(float speed, Vector2D startPosition, int width, int height) {
        this.startPosition = startPosition;

        this.setSpeed(speed);
        this.setDirection(new Vector2D(0, 1));
        this.setSize(new Vector2D(width, height));
        this.setPosition(startPosition);
        this.setName("Paddle");
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {
        super.setup(screenWidth, screenHeight, params);
        this.setPosition(this.startPosition);
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {

    }
}
