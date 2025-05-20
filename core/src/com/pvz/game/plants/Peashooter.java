package com.pvz.game.plants;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.projectiles.Pea;
import com.pvz.game.projectiles.Projectile;
import com.pvz.game.ui.SunManager;
import com.badlogic.gdx.math.Vector2;
public class Peashooter extends Plant{

	public Peashooter() {
		super(new Texture("peashooter.png"), new Texture("peashooterSheet.png"), 20, 300, 1.5f, 0f,100, 2, 7.5f, true);
	}

	@Override
	public String getPacketTexture(){
		return "0.png";
	}

	public void setProjectile() {
		projectile = (new Pea(new Vector2(tile.getWorldPos().x, tile.getWorldPos().y), 20, tile));
	}

	public void action(SunManager sunManager) {
		SoundManager.getInstance().play("pea_shoot"+ MathUtils.random(1,2));
		tile.addProjectile(fire());
	}

	public Projectile fire() {
		Projectile newPea = projectile.clone();
		projectiles.add(newPea);
		newPea.addZombieManager(tile.getZombieManager());
		newPea.setLocation(new Vector2(tile.getWorldPos()).add(new Vector2(Tilemap.TILE_WIDTH, Tilemap.TILE_HEIGHT/2)));

		newPea.setLane(tile.getLane());
		return newPea;

	}

	@Override
	public Peashooter clone() throws CloneNotSupportedException {
	    return (Peashooter) super.clone();
	}


}
