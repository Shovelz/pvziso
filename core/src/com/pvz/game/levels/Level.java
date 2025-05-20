package com.pvz.game.levels;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.plants.Plant;
import com.pvz.game.tiles.UiSeedTile;
import com.pvz.game.ui.WinningItem;
import com.pvz.game.zombies.ZombieManager;

import java.util.ArrayList;

public abstract class Level {


    protected final Vector2 levelNumber;
    //Defaults for timers 0, 20
    protected float initialDelay = 0f, initialDelayTimer, timer, spawnZombieTimer = 0f;
    protected boolean hasStartedSpawning = false;
    protected WaveSpawner waveSpawner;
    protected ArrayList<WaveSpawner.Foe> zombies = new ArrayList<>();
    protected ZombieManager zombieManager;
    protected WaveSpawner.Foe[] foes;
    protected boolean levelWon = false;
    protected UiSeedTile rewardPacket;
    protected Plant rewardPlant;
    protected WinningItem winningItem;

    public Level(ZombieManager zombieManager, float difficultyRate, int maxWaveSize) {
        levelNumber = new Vector2(1, 1);
        this.zombieManager = zombieManager;
        generateFoes();
        waveSpawner = new WaveSpawner(foes, difficultyRate, maxWaveSize);

    }


    public void spawnWinningItem(){

        winningItem = new WinningItem(rewardPlant, rewardPacket);
    }

    protected abstract void generateFoes();

    public UiSeedTile getRewardPacket(){
        return rewardPacket;
    }

    public Plant getRewardPlant(){
        return rewardPlant;
    }

    public ArrayList<WaveSpawner.Foe> getZombies() {
        return zombies;
    }

    public boolean hasWon(){
        return levelWon;
    }

    public void update(float delta) {
        if (initialDelayTimer < initialDelay) {
            initialDelayTimer += delta;
            return;
        }

        if (!hasStartedSpawning && initialDelayTimer >= initialDelay) {
            hasStartedSpawning = true;
            zombies = waveSpawner.spawnWave();
            zombieManager.initializeZombiesFromLevel(zombies);
            timer = 0;
            return;
        }

        timer += delta;

        // Every spawnZombieTimer seconds, advance the wave and load new zombie weights for the next wave
        if (timer >= spawnZombieTimer) {
            timer = 0;
            waveSpawner.incrementWave();
            zombies = waveSpawner.spawnWave();
            zombieManager.initializeZombiesFromLevel(zombies);

        }
        System.out.println(waveSpawner.getSpawnableFoes().isEmpty());
        waveSpawner.generateSpawnableFoes();
        if(waveSpawner.getSpawnableFoes().isEmpty()){
            levelWon = true;
            System.out.println("Level WOn!!!");
            spawnWinningItem();
        }
    }

    public void render(SpriteBatch batch, float delta){
        if(winningItem != null) {
            winningItem.render(batch, delta);
        }
    }

    public WinningItem getWinningItem(){
        return winningItem;
    }
}
