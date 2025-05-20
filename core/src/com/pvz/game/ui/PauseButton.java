package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class PauseButton implements Clickable {

    private Texture buttonSprite;
    private Rectangle hitbox;

    public PauseButton(Vector2 corner){
        buttonSprite = new Texture("pause_button.png");
        hitbox = new Rectangle(corner.x + 145, corner.y + 132 ,  buttonSprite.getWidth(), buttonSprite.getHeight());
    }

    public void render(SpriteBatch batch){
        batch.draw(buttonSprite, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen gameScreen) {
        gameScreen.pause();
        SoundManager.getInstance().play("pause");
    }
}
