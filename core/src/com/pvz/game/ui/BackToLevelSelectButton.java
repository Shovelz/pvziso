package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class BackToLevelSelectButton implements Clickable {

    private Texture buttonSprite;
    private Rectangle hitbox;
    private Camera camera;

    public BackToLevelSelectButton(Camera cam){
        camera = cam;
        buttonSprite = new Texture("BackToMenuButton.png");

        Vector2 newDimensions = new Vector2(buttonSprite.getWidth(),buttonSprite.getHeight());
        hitbox = new Rectangle( camera.position.x - newDimensions.x/2, camera.position.y - newDimensions.y/2 - 7, newDimensions.x, newDimensions.y);
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen gameScreen) {
        ((GameScreen) gameScreen).toLevelScreen();
    }

    public void render(SpriteBatch batch){
        batch.draw(buttonSprite, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }

    public Rectangle getHitbox(){
        return hitbox;
    }


}
