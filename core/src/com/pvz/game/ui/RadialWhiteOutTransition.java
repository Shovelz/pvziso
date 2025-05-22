package com.pvz.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;

public class RadialWhiteOutTransition {
    private Texture circleTexture;
    private float x, y;
    private float elapsed;
    private float duration;
    private float maxRadius;
    private boolean active;
    private Runnable onComplete;
    private Interpolation interpolation;
    private float screenWidth, screenHeight;

    public RadialWhiteOutTransition(float screenWidth, float screenHeight, float duration) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.circleTexture = new Texture("AwardGlow.png");
        this.maxRadius = (float) Math.sqrt(screenWidth * screenWidth + screenHeight * screenHeight);
        this.duration = duration;
        this.interpolation = Interpolation.linear;
        this.active = false;
    }


    public void start(float x, float y, Runnable onComplete) {
        this.x = x;
        this.y = y;
        this.elapsed = 0;
        this.active = true;
        this.onComplete = onComplete;
    }
    private float currentRadius = 0;

    public void update(float delta) {
        if (!active) return;

        elapsed += delta;

        float progress = Math.min(elapsed / duration, 1f);
        float alpha = interpolation.apply(progress);
        currentRadius = maxRadius * alpha;

        // ✅ Check when the circle fully covers the screen
        float requiredRadius = Math.max(screenWidth, screenHeight);
        if (currentRadius >= requiredRadius) {
            active = false;
            elapsed = duration;

            if (onComplete != null) {
                Runnable completed = onComplete;
                onComplete = null;
                completed.run();
            }
        }
    }

    public void render(Batch batch) {
        if (elapsed == 0) return;

        float drawSize = currentRadius * 2;
        batch.setColor(1, 1, 1, 1);
        batch.draw(circleTexture, x - currentRadius, y - currentRadius, drawSize, drawSize);
        batch.setColor(1, 1, 1, 1);
    }



    public boolean isActive() {
        return active;
    }
}
