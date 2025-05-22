package com.pvz.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.pvz.game.Tilemap;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.tiles.UiTile;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class Ui {

	private UiTile ui;
	private Texture uiBG;
	private BitmapFont font;
	private Vector2 corner;
	private GameScreen screen;
	private Shovel shovel;
	private PauseButton pauseButton;
	private Menu menu;
	private AssetManager assetManager;


	public Ui(Vector2 c, GameScreen s, AssetManager assetManager) {
		this.assetManager = assetManager;
		assetManager.load("ui.png", Texture.class);
		assetManager.finishLoading();
		uiBG = assetManager.get("ui.png", Texture.class);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/upheavtt.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 20; // Set the font size
		font = generator.generateFont(parameter);
		font.setColor(1f, 1f, 1f, 1f);
		// Disable anti-aliasing
		parameter.minFilter = Texture.TextureFilter.Nearest;
		parameter.magFilter = Texture.TextureFilter.Nearest;
		parameter.genMipMaps = false;

		corner = c;

		ui = new UiTile(uiBG, new Vector2(0, 0), new Vector2(corner.x + Tilemap.horribleBackgroundOffset.x,
				corner.y + Tilemap.horribleBackgroundOffset.y));
		screen = s;
		shovel = new Shovel(screen.getMap(), ui.getWorldPos(),assetManager);
		pauseButton = new PauseButton(corner);

		menu = new Menu(screen.getCamera());
	}

	public void updateMenu(Vector2 mousePosition){
		menu.update(mousePosition);
	}

	public Shovel getShovel(){
		return shovel;
	}

	public void renderShovel(SpriteBatch batch, float delta){

		shovel.render(batch, delta, screen.getWorldMousePosition());
	}

	public void renderMenu(SpriteBatch batch, float delta){

		if(screen.getPaused()) {
			menu.render(batch);
		}
	}
	public void render(SpriteBatch batch, float delta) {

		ui.render(batch);

		pauseButton.render(batch);

		Integer sun = screen.getSunAmount();
		float startX = screen.getMap().getPackets().get(0).getWorldPos().x;
		float currentY =  screen.getMap().getPackets().get(0).getWorldPos().y + screen.getMap().getPackets().get(0).getHitbox().height + 18;
		float yOffset = 5;

		// Draw each character with an increasing offset
		for (int i = 0; i < String.valueOf(sun).length(); i++) {
			char c = String.valueOf(sun).charAt(i);

			font.draw(batch, String.valueOf(c), startX, currentY);

			currentY -= yOffset;

			startX += 13;
		}

	}

	public void updateUiPosition(int width, int height) {
		shovel.updatePosition(ui.getWorldPos());
	}

	public Clickable getMenuButton() {
		return menu.getMenuButton();
	}

	public Clickable getPauseButton() {
		return pauseButton;
	}

	public ArrayList<SliderButton> getSlider() {

		return menu.getSliderButtons();
	}
}
