package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.UnlockPlantScreen;

public class UnlockPlantNextButton implements Clickable {

    private Texture buttonSprite, buttonSpriteHovered;
    private Rectangle hitbox;
    private boolean hovered;

    public UnlockPlantNextButton(Vector2 position){
        buttonSprite = new Texture("unlockPlantNextButton.png");
        buttonSpriteHovered = new Texture("unlockPlantNextButtonHovered.png");

        hitbox = new Rectangle(position.x - buttonSprite.getWidth()*2f, position.y - buttonSprite.getHeight()*2f, buttonSprite.getWidth()*4f, buttonSprite.getHeight()*4f);
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        hovered = true;
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen screen) {
        ((UnlockPlantScreen) screen).toLevelScreen();
    }

    public void render(SpriteBatch batch){

        batch.draw(hovered ? buttonSpriteHovered : buttonSprite , hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public Rectangle getHitbox(){
        return hitbox;
    }


    public void setHovered(boolean hovered) {
        this.hovered = hovered;
    }
}
