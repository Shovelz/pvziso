package com.pvz.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pvz.game.screens.GameScreen;

public class IsoGame extends Game {
	private SpriteBatch batch;
	private GameScreen screen;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		screen = (new GameScreen(batch));
		setScreen(screen);
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
	
	public SpriteBatch getBatch() {
		return this.batch;
		
	}
}
