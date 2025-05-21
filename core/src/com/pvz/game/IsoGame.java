package com.pvz.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.screens.LevelSelectScreen;

public class IsoGame extends Game {
    private SpriteBatch batch;
    private LevelSelectScreen levelSelectScreen;
    private GameScreen gameScreen;
    private AssetManager assetManager;

    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.assetManager = new AssetManager();

        levelSelectScreen = (new LevelSelectScreen(batch, this, assetManager));
        gameScreen = new GameScreen(batch, this, assetManager);
        gameScreen.loadLevels();
        setScreen(levelSelectScreen);
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
        super.render();
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
        levelSelectScreen.reset();
//        gameScreen.getCurrentLevel();
        levelSelectScreen.show();
        setScreen(levelSelectScreen);
        gameScreen.hide();
    }
}
