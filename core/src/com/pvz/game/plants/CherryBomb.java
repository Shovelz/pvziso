package com.pvz.game.plants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.projectiles.Explosion;
import com.pvz.game.projectiles.Projectile;
import com.pvz.game.ui.SunManager;

public class CherryBomb extends Plant{

	public CherryBomb() {
		super("Cherry-Bomb", "Cherry Bombs can blow up all zombies in an area. They have a short fuse so plant them near zombies.",
				new Texture("cherry.png"), new Texture("cherrySheet.png"),
				0, 300, 1.5f, 0f, 150, 0, 45f, false);
	}

	@Override
	public String getPacketTexture(){
		return "3.png";
	}

	@Override
	public CherryBomb clone() throws CloneNotSupportedException {
	    CherryBomb clonedCherryBomb = (CherryBomb) super.clone();
	    return clonedCherryBomb;
	}

	@Override
	public void endOfAnimation(){
		tile.removePlant();
	}

	public void setProjectile() {
		projectile = (new Explosion(new Vector2(tile.getWorldPos().x, tile.getWorldPos().y), 3000, tile));
	}

	public void action(SunManager sunManager) {
		tile.addProjectile(fire());
	}

	public Projectile fire() {
		SoundManager.getInstance().play("cherrybomb");

		Projectile newExplosion = projectile.clone();
		projectiles.add(newExplosion);

		newExplosion.addZombieManager(tile.getZombieManager());
		newExplosion.setLocation(new Vector2(tile.getWorldPos()).add(new Vector2(Tilemap.TILE_WIDTH, Tilemap.TILE_HEIGHT/2)));
		newExplosion.setLane(tile.getLane());
		return newExplosion;

	}


}
