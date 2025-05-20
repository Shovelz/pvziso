package com.pvz.game.tiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.plants.Plant;
import com.pvz.game.projectiles.Explosion;
import com.pvz.game.projectiles.Projectile;
import com.pvz.game.ui.SunManager;
import com.pvz.game.zombies.ZombieManager;

public class PlantTile extends AbstractTile{


	private TextureRegion[] animationFrames;
	private Animation<TextureRegion> animation;
	private Plant plant;
	private float time = 0;
	public final static int animSpeed = 2;
	private float fireTime = 0;
	private Map<Vector2, AbstractTile> base = new HashMap<Vector2, AbstractTile>();

	private List<Projectile> projectiles = new ArrayList<Projectile>();

	private SunManager sunManager;
	private ZombieManager zombieManager;

	public PlantTile(Texture t, Vector2 tileMapPos, Vector2 worldPos, Map<Vector2, AbstractTile> base, SunManager sm, ZombieManager zm) {
		super(t, tileMapPos, worldPos);
		this.base = base;
		this.sunManager = sm;
		this.zombieManager = zm;
	}

	@Override
	public void render(SpriteBatch batch) {}

	public void updateProjectiles(float delta){
		for(Projectile pro : new ArrayList<Projectile>(projectiles)) {
			pro.move(delta);
			//Banger 1 liner to get last tile and check the projectile hasnt passed it
			if(pro.getLocation().y < base.get(new Vector2(this.getTilemapPos().x, 0)).getWorldPos().y + TILE_WIDTH/4f) {
				projectiles.remove(pro);
			}
			if(pro.checkForCollision() && !pro.isMultiHit()) {
				projectiles.remove(pro);
			}
			if(pro instanceof Explosion){
				projectiles.remove(pro);
			}
		}
	}

	public void update(float delta){
		updateProjectiles(delta);
		if(plant != null) {
			fireTime += delta;
			if(fireTime >= plant.getReload()) {
				plant.action(sunManager);
				fireTime -= plant.getReload();
			}
			time += animSpeed * delta;
			if(animation.isAnimationFinished(time)) {
				plant.endOfAnimation();
			}

		}
	}

	public void render(SpriteBatch batch, float delta) {
		renderProjectiles(batch, delta);

		if (animation != null && plant != null) {
			Vector2 centerOfTile = new Vector2(worldPos.x, worldPos.y - Tilemap.TILE_HEIGHT);
			batch.draw(animation.getKeyFrame(time, true), centerOfTile.x, centerOfTile.y);
		}
	}
	public void addProjectile(Projectile pro) {
		projectiles.add(pro);
	}

	public void renderProjectiles(SpriteBatch batch, float delta) {

		for(Projectile pro : new ArrayList<Projectile>(projectiles)) {
			pro.render(batch, delta);
		}
	}

	public int getLane(){
		return (int) getTilemapPos().x;
	}

	@Override
	public void setTexture(Texture tex) {
		this.texture = tex;
	}

	public void setAnimation(Animation<TextureRegion> anim, TextureRegion[] frames) {
		this.animationFrames = frames;
		this.animation = anim;
	}

	public Plant getPlant() {
		return plant;
	}

	public void setPlant(Plant plant) {
		this.plant = plant;
	}

	public void removePlant(){
		this.plant = null;
		time = 0;
		fireTime = 0;
	}

	public void setStartingFireTime(float start){
		fireTime = start;
	}

	public ZombieManager getZombieManager() {
		return zombieManager;
	}

}