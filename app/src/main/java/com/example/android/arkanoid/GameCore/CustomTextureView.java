package com.example.android.arkanoid.GameCore;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

public abstract class CustomTextureView extends TextureView implements TextureView.SurfaceTextureListener {
    public CustomTextureView(Context context) {
        super(context);
    }

    @Override
    public abstract void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);

    @Override
    public abstract void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}
}
