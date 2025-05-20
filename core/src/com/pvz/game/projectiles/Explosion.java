package com.pvz.game.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.DebuggerSingleton;
import com.pvz.game.TileMapSingleton;
import com.pvz.game.tiles.AbstractTile;
import com.pvz.game.tiles.PlantTile;
import com.pvz.game.zombies.Zombie;

import java.util.ArrayList;

public class Explosion extends Projectile {

	public Explosion(Vector2 start, int damage, PlantTile plantTile) {
		super(0f, 0f, new Texture("pea.png"), start, damage, 10, plantTile, true);

	}
	@Override
	public void render(SpriteBatch batch, float delta) {}

	public void move(float delta){}

	public boolean checkForCollision() {

		ArrayList<AbstractTile> tiles = new ArrayList<>();

		for(int i = -1; i < 2; i++){
			for(int j = -1; j < 2; j++) {
				AbstractTile t = TileMapSingleton.getInstance().getMap().get(new Vector2(((int) plant.getTilemapPos().x + i), ((int) plant.getTilemapPos().y + j)));
				if(t != null){
					tiles.add(t);
				}
			}
		}

		DebuggerSingleton.getInstance().setTiles(tiles);

		ArrayList<Zombie> zombiesCopy = new ArrayList<>(zombieManager.getzombies());
		for(Zombie zombie: zombiesCopy) {
			if (tiles.contains(zombie.getTile())) {
				zombie.takeDamage(damage, true);
			}
		}
		return false;
	}


	@Override
	public void update(float delta) {

	}

    @Override
    public Projectile clone() {
        return new Explosion(new Vector2(getLocation()), getDamage(), getPlant());
    }

}
