package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.levels.Level;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.screens.LevelSelectScreen;
import com.pvz.game.tiles.UiSeedTile;

public class LevelSelect implements Clickable {

    private Rectangle hitbox;
    private Texture sprite;
    private int level;
    private AssetManager assetManager;
    private boolean hovered;
    private Vector2 position;
    private Vector2 animate;
    private float timer, timerRate = 300f;
    private int animationDirection;
    private GameScreen gameScreen;
    private boolean nonSelectable;

    public LevelSelect(Vector2 pos, int level, AssetManager assetManager, GameScreen gameScreen){
        this.assetManager = assetManager;
        this.level = level;

        assetManager.load("levels/" + level + ".png", Texture.class);
        assetManager.finishLoading();
        sprite = assetManager.get("levels/" + level + ".png", Texture.class);

        position = pos;
        animate = new Vector2(pos).add(0, 8);
        hitbox = new Rectangle(pos.x, pos.y, sprite.getWidth(), sprite.getHeight());
        this.gameScreen = gameScreen;
    }

    public void setNonSelectable(boolean s){
        nonSelectable = s;
    }

    public boolean getNonSelectable(){
        return nonSelectable;
    }

    public void render(SpriteBatch batch, float delta){
        // Update animation direction based on hover state
        if (hovered) {
            animationDirection = 1;
        } else {
            animationDirection = -1;
        }

        // Update and clamp timer
        timer += delta * timerRate * animationDirection;
        timer = MathUtils.clamp(timer, 0f, 100f); // Clamp between 0 and 100

        // Interpolation factor from 0 to 1
        float t = timer / 100f;

        // Interpolate Y position
        hitbox.y = Interpolation.sine.apply(position.y, animate.y, t);

        if(nonSelectable){
            batch.setShader(UiSeedTile.grayscaleShader);
        }
        // Draw sprite at interpolated Y
        batch.draw(sprite, hitbox.x, hitbox.y);
        batch.setShader(null);
    }

    public Level getLevel(){
        return this.gameScreen.getLevel(level);
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return !nonSelectable && (hovered = hitbox.contains(mousePosition));

    }

    @Override
    public void onClick(Screen gameScreen) {
        ((LevelSelectScreen) gameScreen).startLevel(level);
    }
}
