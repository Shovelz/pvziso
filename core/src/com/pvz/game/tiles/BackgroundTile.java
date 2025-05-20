package com.pvz.game.tiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class BackgroundTile extends AbstractTile{
	
	
	public BackgroundTile(Texture t, Vector2 tileMapPos, Vector2 worldPos) {
		super(t, tileMapPos, worldPos);
	}
	
	@Override
	public void render(SpriteBatch batch) {
		batch.draw(texture, worldPos.x, worldPos.y);
	}
	
	@Override
	public void setTexture(Texture tex) {
		this.texture = tex;
	}
	
	
	
	
}