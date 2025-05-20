package com.pvz.game.zombies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.tiles.AbstractTile;
import com.pvz.game.tiles.PlantTile;
import com.pvz.game.tiles.TargetTile;

import java.util.Random;

public abstract class Zombie implements Cloneable {

	protected Texture spriteSheet;
	protected Animation<TextureRegion> animation;
	protected float time = 0f;
	protected float animSpeed = 3f;
	protected Vector2 worldPos;
	protected AbstractTile tile;
	protected float health;
	protected int damage = 0;
	protected Rectangle hitbox;
	protected Tilemap map;
	protected ZombieManager zombieManager;
	protected int lane;
	protected float x,y;
	protected boolean zombieTransformedYet;
	protected Zombie zombieTransform;
	protected int zombieTransformThreshold;
	protected int range = 20;
	protected Animation<TextureRegion> currentAnimation;
	protected AbstractTile target;
	protected boolean isDead;
	protected boolean loopAnimation = true;
	protected float speed = -2f;
	protected float maxHealth = 270f;
	protected Texture shadow = new Texture("shadow.png");


	protected abstract void move(float delta);
	public abstract void render(SpriteBatch batch, float delta);
	public abstract void update(float delta, Tilemap m);
	protected abstract void updateState(Tilemap mp);


	public abstract void takeDamage(float damage, boolean isExplosion);

	public Rectangle getHitbox() {
		return hitbox;
	}

	public int getLane(){
		return lane;
	}

	public void updateHitbox(){
		hitbox.x = worldPos.x;
		hitbox.y = worldPos.y;
	}

	protected abstract AbstractTile getTarget(Tilemap mp);

	public void setStartPosition(int lane, Tilemap m, float offset) {
		float x = m.getBase().get(new Vector2(lane, 0)).getWorldPos().x + offset * 2 + Tilemap.TILE_WIDTH*2;
		float y = m.getBase().get(new Vector2(lane, 0)).getWorldPos().y - offset - Tilemap.TILE_HEIGHT*2;
		worldPos = new Vector2(x, y);
		this.lane = lane;

		float randomTime = new Random().nextFloat() * currentAnimation.getAnimationDuration();
		time = randomTime;

		updateHitbox();
	}


	public AbstractTile getTile() {
		tile = map.get(hitbox.x + 5, hitbox.y + 5);
		return tile;
	}
	protected Animation<TextureRegion> loadAnimations(int width, int height, Texture sheet) {

		TextureRegion[][] tmpFrames = TextureRegion.split(sheet, width, height);

		TextureRegion[] animationFrames = new TextureRegion[tmpFrames.length * tmpFrames[0].length];
		int index = 0;
		for(int row = 0; row < tmpFrames.length;row++) {
			for(int col = 0; col < tmpFrames[tmpFrames.length-1].length; col++) {
				animationFrames[index++] = tmpFrames[row][col];
			}

		}
		return new Animation<TextureRegion>(1f/4f, animationFrames);


	}

	public Tilemap getMap() {
		return map;
	}

	public void setMap(Tilemap m) {
		this.map = m;
	}

	public void addZombieManager(ZombieManager zm) {
		zombieManager = zm;
	}

	public abstract void die(boolean isExplosion);

	public float getHealth(){
		return health;
	}

	public Vector2 getPosition(){
		return worldPos;
	}

	@Override
	public Zombie clone() {
		try {
			Zombie clonedZombie = (Zombie) super.clone();
			clonedZombie.hitbox = new Rectangle(this.hitbox); // Create a new Rectangle instance
			clonedZombie.worldPos = new Vector2(this.worldPos); // Clone Vector2 as well
			clonedZombie.map = this.map;
			return clonedZombie;
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void eat(int damage){
		if (target instanceof PlantTile){
			PlantTile t = (PlantTile) target;
			t.getPlant().takeDamage(damage);
		}
		if (target instanceof TargetTile) {
			map.getScreen().gameOver();
		}
	}

	protected void setLane(int l) {
		lane = l;
	}

	protected void setWorldPosition(Vector2 w) {
		this.worldPos = w;
	}

	protected void setHealth(float h) {
		health = h;
	}
}
