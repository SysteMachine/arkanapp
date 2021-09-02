package com.example.android.arkanoid.Util;

import android.animation.Animator;

public abstract class AbstractAnimatorListener implements Animator.AnimatorListener {
    @Override
    public void onAnimationStart(Animator animation) {}
    @Override
    public void onAnimationCancel(Animator animation) {}
    @Override
    public void onAnimationRepeat(Animator animation) {}


    @Override
    public abstract void onAnimationEnd(Animator animation);
}
