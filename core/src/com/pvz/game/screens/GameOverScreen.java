package com.pvz.game.screens;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pvz.game.audio.SoundManager;

public class GameOverScreen {

    private Texture gameOverSprite;
    private float timer = 0f, timerMax = 100f, timerRate = 50f;
    private Texture blackOverlay;
    private boolean soundPlayed = false;

    public GameOverScreen(){
        gameOverSprite = new Texture("gameOverScreen.png");
        // Create 1x1 black texture for overlay
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.BLACK);
        pixmap.fill();
        blackOverlay = new Texture(pixmap);
        pixmap.dispose();
    }

    public void render(SpriteBatch batch, float delta, Camera camera, Viewport port){
        if(!soundPlayed){
            SoundManager.getInstance().play("game_over");
            SoundManager.getInstance().play("lose_music");
            soundPlayed = true;
        }
        timer += delta * timerRate;
        if(timer > 100f){
            timer = 100f;
        }

        float t = timer / 100f;
        float scale = Interpolation.swingOut.apply(t);  // ease-in

        batch.setColor(0, 0, 0, 0.5f);  // RGBA: 50% opacity black
        batch.draw(blackOverlay, camera.position.x - port.getWorldWidth() / 2, camera.position.y - port.getWorldHeight() / 2, port.getWorldWidth(), port.getWorldHeight());
        batch.setColor(1, 1, 1, 1); // Reset color to full opacity white

        Vector2 newDimensions = new Vector2(gameOverSprite.getWidth() * scale, gameOverSprite.getHeight() * scale);
        batch.draw(gameOverSprite, camera.position.x - newDimensions.x/2, camera.position.y - newDimensions.y/2, newDimensions.x, newDimensions.y);
    }

    public void dispose() {
        gameOverSprite.dispose();
        blackOverlay.dispose();
    }

}
