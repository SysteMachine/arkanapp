package com.example.android.arkanoid.GameElements;

import com.example.android.arkanoid.GameElements.BaseElements.AbstractEntity;
import com.example.android.arkanoid.Util.ParamList;
import com.example.android.arkanoid.VectorMat.Vector2D;

public class Brick extends AbstractEntity {
    private final int maxHealth;
    private int health;

    public Brick(int maxHealth, Vector2D position, Vector2D size){
        this.maxHealth = maxHealth;
        this.health = maxHealth;

        this.setPosition(position);
        this.setSpeed(0);
        this.setDirection(new Vector2D(0, 0));
        this.setSize(size);
        this.setName("Brick");
    }

    @Override
    public void logica(float dt, int screenWidth, int screenHeight, ParamList params) {

    }
}
