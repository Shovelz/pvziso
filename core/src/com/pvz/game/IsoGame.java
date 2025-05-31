package com.pvz.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.pvz.game.levels.Level;
import com.pvz.game.plants.Plant;
import com.pvz.game.screens.GameScreen;
import com.pvz.game.screens.LevelSelectScreen;
import com.pvz.game.screens.UnlockPlantScreen;

import java.util.HashMap;

public class IsoGame extends Game {
    private SpriteBatch batch;
    private LevelSelectScreen levelSelectScreen;
    private GameScreen gameScreen;
    private UnlockPlantScreen unlockPlantScreen;
    private AssetManager assetManager;
    private boolean screenWhite;
    private ShapeRenderer shapeRenderer;
    private HashMap<Integer, Level> completedLevels = new HashMap<>();


    @Override
    public void create() {
        this.batch = new SpriteBatch();
        this.assetManager = new AssetManager();

        gameScreen = new GameScreen(batch, this, assetManager);
        levelSelectScreen = (new LevelSelectScreen(batch, this, assetManager));
        unlockPlantScreen = new UnlockPlantScreen(batch, this, assetManager);
        gameScreen.loadLevels();
        setScreen(levelSelectScreen);
        levelSelectScreen.show();
        shapeRenderer = new ShapeRenderer();
    }

    public GameScreen getGameScreen(){
        return gameScreen;
    }

    public void startGame(int level) {

        System.out.println("Start game" + level);
        gameScreen.setLevel(level);
        gameScreen.reset();
        levelSelectScreen.hide();
        setScreen(gameScreen);
        gameScreen.startGame();
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


    public void addCompletedLevel(Level level){
        completedLevels.put(level.getLevelNumber(), level);
    }

    public HashMap<Integer, Level> getCompletedLevels(){
        return completedLevels;
    }

    public SpriteBatch getBatch() {
        return this.batch;

    }

    public void backToLevelSelect() {
        gameScreen.reset();
        gameScreen.hide();
        levelSelectScreen.reset();
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

    public boolean isPrevLevelCompleted(Level level) {
        System.out.println(level.getLevelNumber());
        // 1) Level 1 is always allowed:
        if (level.getLevelNumber() == 1) {
            return true;
        }

        // 2) If they haven’t even finished any won levels yet, then they can’t pick level 2+:
        if (completedLevels.isEmpty()) {
            return false;
        }

        // 3) Finally, check if they have completed level (n-1):
        return completedLevels.containsKey(level.getLevelNumber() - 1);
    }

}
