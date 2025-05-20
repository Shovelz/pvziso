package com.pvz.game.mower;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.SoundManager;

public class Mower {

	private Texture sprite;
	private float x, y;
	private float startX, startY;
	private float endX, endY;
	private float speed = 40;
	private MowerManager mowerManager;
	private int lane;
	private Rectangle hitbox;

	private boolean triggered;

	public Mower(float sX, float sY, float eX, float eY, int l) {
		sprite = new Texture("mower.png");
		startX = sX;
		startY = sY;
		x = startX + Tilemap.TILE_WIDTH / 4f;
		y = startY + Tilemap.TILE_HEIGHT / 4f;
		endX = eX;
		endY = eY;
		lane = l;
		hitbox = new Rectangle(x, y, Tilemap.TILE_WIDTH * 3 / 4f, Tilemap.TILE_HEIGHT);
	}

	public int getLane() {
		return lane;
	}

	private void updateHitbox() {
		hitbox.x = x;
		hitbox.y = y;
	}

	public Rectangle getHitbox() {
		return hitbox;
	}

	private void move(float delta) {
		x += speed * delta;
		y -= (speed / 2) * delta;

		updateHitbox();
	}

	public void trigger() {
		if (!triggered) {
			SoundManager.getInstance().play("mower");
		}
		triggered = true;
	}

	public void update(float delta) {

		if (triggered) {
			move(delta);
		}
		if (x > endX) {
			mowerManager.removeMower(this);
		}
	}


	public void render(SpriteBatch batch, float delta) {
		batch.draw(sprite, x, y);
	}

	public void addMowerManager(MowerManager mm) {
		mowerManager = mm;
	}

	public Vector2 getPosition() {
		return new Vector2(x, y);
	}
}