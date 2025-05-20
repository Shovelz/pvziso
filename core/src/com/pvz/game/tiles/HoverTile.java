package com.pvz.game.tiles;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class HoverTile extends AbstractTile implements Clickable {


    public HoverTile(Vector2 tileMapPos, Vector2 worldPos) {
        super(null, tileMapPos, worldPos);
    }

    @Override
    public void render(SpriteBatch batch) {
        if (this.texture != null) {
            Color color = batch.getColor();

            float oldAlpha = color.a;
            color.a = 0.5f;

            Vector2 centerOfTile = new Vector2(worldPos.x, worldPos.y - Tilemap.TILE_HEIGHT);
            batch.setColor(color);
            batch.draw(texture, centerOfTile.x, centerOfTile.y);

            color.a = oldAlpha;
            batch.setColor(color);

        }
    }

    @Override
    public void setTexture(Texture tex) {
        this.texture = tex;
    }


    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        // Default hover behavior for tiles
        if (((GameScreen) gameScreen).getMap().get(mousePosition.x, mousePosition.y) == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(Screen gameScreen) {
//        if (gameScreen.getCurrentPlant() != null && gameScreen.getSunAmount() >= gameScreen.getCurrentPlant().getCost()) {
//            gameScreen.setSunAmount(gameScreen.getSunAmount() - gameScreen.getCurrentPlant().getCost());
//            gameScreen.getMap().plant(this, gameScreen.getCurrentPlant());
//            gameScreen.setHoverPlant(null);
//        }


    }
}