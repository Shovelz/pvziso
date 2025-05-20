package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class Shovel implements Clickable {


    private Texture shovelSprite;
    private Vector2 pos = new Vector2();
    private Vector2 target = new Vector2();
    private Vector2 uiShovelPosition = new Vector2(333, 39);
    private boolean clicked;
    private Rectangle hitbox;
    private Tilemap map;

    public Shovel(Tilemap m, Vector2 p, AssetManager assetManager) {
        assetManager.load("shovel.png", Texture.class);
        assetManager.finishLoading();
        shovelSprite = assetManager.get("shovel.png", Texture.class);
        map = m;
        pos = uiShovelPosition;
        hitbox = new Rectangle(pos.x, pos.y, 20,20);
    }


    public Rectangle getHitbox() {
        return hitbox;
    }

    public void setClicked(boolean s){
        clicked = s;
    }

    public boolean getClicked(){
        return clicked;
    }



    public void render(SpriteBatch batch, float delta, Vector2 mousePosition) {
        if(clicked){
            batch.draw(shovelSprite, mousePosition.x, mousePosition.y);
            hitbox.setPosition(mousePosition.x, mousePosition.y); // Update hitbox when dragging
            return;
        }
            batch.draw(shovelSprite, pos.x, pos.y);
        hitbox.setPosition(pos.x, pos.y);

    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition) && !clicked;
    }

    @Override
    public void onClick(Screen gameScreen) {
        SoundManager.getInstance().play("shovel");
        setClicked(true);
    }
    public void updatePosition(Vector2 newPos) {
        pos.set(newPos);
        hitbox.setPosition(newPos.x, newPos.y); // Ensure the hitbox moves with the shovel
    }

}
