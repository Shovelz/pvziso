package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.LevelSelectScreen;

public class LevelSelect implements Clickable {

    private Rectangle hitbox;
    private Texture sprite;
    private int level;
    private AssetManager assetManager;

    public LevelSelect(Vector2 pos, int level, AssetManager assetManager){
        this.assetManager = assetManager;
        this.level = level;

        assetManager.load("levels/" + level + ".png", Texture.class);
        assetManager.finishLoading();
        sprite = assetManager.get("levels/" + level + ".png", Texture.class);

        hitbox = new Rectangle(pos.x, pos.y, sprite.getWidth(), sprite.getHeight());
    }

    public void render(SpriteBatch batch, float delta){
        batch.draw(sprite, hitbox.x, hitbox.y);
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen gameScreen) {
        System.out.println("start level!!");
        ((LevelSelectScreen) gameScreen).startLevel(level);
    }
}
