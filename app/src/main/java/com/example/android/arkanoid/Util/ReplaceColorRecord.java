package com.example.android.arkanoid.Util;

public class ReplaceColorRecord {
    private int fromColor;
    private int targetColor;
    private float tollerance;

    public ReplaceColorRecord(int fromColor, int targetColor, float tollerance) {
        this.fromColor = fromColor;
        this.targetColor = targetColor;
        this.tollerance = tollerance;
    }

    public int getFromColor() {
        return fromColor;
    }

    public int getTargetColor() {
        return targetColor;
    }

    public float getTollerance() {
        return tollerance;
    }
}
