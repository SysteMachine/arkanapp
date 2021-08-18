package com.example.android.arkanoid.GameElements;

import com.example.android.arkanoid.GameElements.BaseElements.AbstractEntity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Ball extends AbstractEntity {
    public Ball(float speed, Vector2D startPosition, Vector2D startDirection){
        this.speed = speed;
        this.direction = startDirection;
        this.position = startPosition;
    }

    @Override
    public void setup(int screenWidth, int screenHeight, ParamList params) {

    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {

    }
}