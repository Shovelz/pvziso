package com.pvz.game.ui;

import javax.xml.XMLConstants;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class Sun implements Clickable {

	private Texture sunSprite;
	private Vector2 pos = new Vector2();
	private Vector2 target = new Vector2();
	private float sunSpeed = 30f;
	private float lifetime;
	private float maxLifetime = 9.5f;
	private float flashTimer = 0.0f;
	private float flashSpeed = 1.5f;
	private float maxFlashLifetime = 3.0f;

	private SunManager sunManager;
	private Rectangle hitbox;
	private Vector2 uiSunPosition = new Vector2(158, 122);
	private boolean clicked;
	private float progress = 0f;
	private float moveSpeed = 200f;

	public Sun(Vector2 t, boolean startFromTop) {
		sunSprite = new Texture("sun.png");
		target.x = t.x;
		target.y = t.y;
		pos.x = t.x;
		pos.y = t.y + (startFromTop ? 150 : 10);
		hitbox = new Rectangle(pos.x, pos.y, 20, 20);

	}

	public boolean reachedTarget() {
		return pos.y <= target.y;
	}
	
	public void addSunManager(SunManager sm) {
		sunManager = sm;
	}
	
	public Rectangle getHitbox() {
		return hitbox;
	}

	private void move(float delta) {
		if(pos.y >= target.y) {
			pos.y -= sunSpeed * delta;
		}
		hitbox.y = pos.y;
	}

	public void setClicked(){
		clicked = true;
		target = pos;
	}

	public boolean getClicked(){
		return clicked;
	}

	private void moveClicked(float delta) {

		Vector2 direction = new Vector2(uiSunPosition).sub(pos).nor(); // Get direction
		float distance = pos.dst(uiSunPosition); // Distance to target
		float step = moveSpeed * delta; // Adjust speed

		if (distance <= step) {
			pos.set(uiSunPosition); // Snap to target
			sunManager.removeSun(this); // Remove once reached
		} else {
			pos.mulAdd(direction, step); // Move in a straight line
		}
	}

	public void update(float delta){
		if(reachedTarget()){
			lifetime += delta;
		}

		if(clicked) {
			moveClicked(delta);
		}else {
			move(delta);
		}
		if(flashTimer >= maxFlashLifetime) {
			sunManager.removeSun(this);
		}
	}

	public void render(SpriteBatch batch, float delta) {

        float opacity = 0.8f + 0.2f * (float) Math.sin(flashTimer * Math.PI); // Oscillates between 0.5 and 1
		if(lifetime >= maxLifetime && !clicked) {
			flashTimer += delta * flashSpeed;
			Color color = batch.getColor();

			float oldAlpha = color.a;
			color.a = opacity;

			batch.setColor(color);

			batch.draw(sunSprite, pos.x, pos.y);

			color.a = oldAlpha;
			batch.setColor(color);


		}else {
			batch.draw(sunSprite, pos.x, pos.y);
		}
	}

	@Override
	public boolean isHovered(Vector2 mousePosition, GameScreen gameScreen) {
		return hitbox.contains(mousePosition);
	}

	@Override
	public void onClick(GameScreen gameScreen) {
		if (!clicked) {
			SoundManager.getInstance().play("points");
			setClicked();
			gameScreen.addSun(25);
		}
	}
}
