package com.pvz.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pvz.game.plants.Plant;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.screens.LevelSelectScreen;
import com.pvz.game.screens.UnlockPlantScreen;

public class IsoGame extends Game {
    private SpriteBatch batch;
    private LevelSelectScreen levelSelectScreen;
    private GameScreen gameScreen;
    private UnlockPlantScreen unlockPlantScreen;
    private AssetManager assetManager;
    private boolean screenWhite;
    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.assetManager = new AssetManager();

        levelSelectScreen = (new LevelSelectScreen(batch, this, assetManager));
        gameScreen = new GameScreen(batch, this, assetManager);
        unlockPlantScreen = new UnlockPlantScreen(batch, this, assetManager);
        gameScreen.loadLevels();
        setScreen(levelSelectScreen);
        shapeRenderer = new ShapeRenderer();
    }


    public void startGame(int level) {

        gameScreen.reset();
        gameScreen.setLevel(level);
        levelSelectScreen.hide();
        setScreen(gameScreen);
        gameScreen.startGame();
        gameScreen.show();
    }

    @Override
    public void render() {

        //Force clear screen just in case a screen forgot to do that before rendering
        if (screenWhite) {
            ScreenUtils.clear(1, 1, 1, 1, true);
        }else{
            super.render();
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (assetManager != null) {
            assetManager.dispose();
        }
    }

    public SpriteBatch getBatch() {
        return this.batch;

    }

    public void backToLevelSelect() {
        gameScreen.reset();
        gameScreen.hide();
        levelSelectScreen.reset();
//        gameScreen.getCurrentLevel();
        levelSelectScreen.show();
        setScreen(levelSelectScreen);
    }

    public void unlockPlant(Plant plant){
        screenWhite = true;
        gameScreen.hide();
        unlockPlantScreen.setPlant(plant);
        setScreen(unlockPlantScreen);
        screenWhite = false; // Let UnlockPlantScreen handle fade-in
    }
}
