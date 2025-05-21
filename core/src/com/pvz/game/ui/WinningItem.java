package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.TileMapSingleton;
import com.pvz.game.Tilemap;
import com.pvz.game.plants.Plant;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.tiles.AbstractTile;
import com.pvz.game.tiles.UiSeedTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;


public class WinningItem implements Clickable {

    private Plant plant;
    private UiSeedTile uiSeedTile;
    private Rectangle hitbox;
    private Vector2 position;
    private Texture arrow = new Texture("arrow.png");
    private float arrowPointA, arrowPointB;
    private float timer, timerRate = 50f, arrowAnimationDirection = 1;

    public WinningItem(Plant plant, UiSeedTile uiSeedTile){
        this.plant = plant;
        Map<Vector2, AbstractTile> map = TileMapSingleton.getInstance().getMap();
        List<Vector2> keys = new ArrayList<>(map.keySet());
        Vector2 randomPos = keys.get(new Random().nextInt(keys.size()));

        position = TileMapSingleton.getInstance().getMap().get(randomPos).getWorldPos();
        hitbox = new Rectangle(position.x, position.y, 14, 24);
        arrowPointA = position.y - 2;
        arrowPointB = position.y + 10;
        this.uiSeedTile = uiSeedTile;
    }

    public void render(SpriteBatch batch, float delta){

        // Linear interpolation between A and B
        timer += delta * timerRate * arrowAnimationDirection;
        if(timer >= 100f){
            arrowAnimationDirection = -1;
        }
        if(timer <= 0){
            arrowAnimationDirection = 1;
        }

        float t = timer / 100f;
        float arrowY = com.badlogic.gdx.math.Interpolation.sine.apply(arrowPointA, arrowPointB, t);

        batch.draw(uiSeedTile.getTexture(), position.x, position.y);
        batch.draw(arrow, position.x + 2, arrowY + uiSeedTile.getTexture().getHeight());

    }


    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen gameScreen) {
        ((GameScreen) gameScreen).finishGame();
    }
}
