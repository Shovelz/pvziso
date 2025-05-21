package com.pvz.game.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pvz.game.plants.Plant;
import com.pvz.game.tiles.UiSeedTile;
import com.pvz.game.ui.WinningItem;
import com.pvz.game.zombies.ZombieManager;

import java.util.ArrayList;

public abstract class Level {


    protected static final int levelNumber = 0;
    //Defaults for timers 0, 20
    protected float initialDelay = 0f, initialDelayTimer, timer, spawnZombieTimer = 1f;
    protected boolean hasStartedSpawning = false;
    protected WaveSpawner waveSpawner;
    protected ArrayList<WaveSpawner.Foe> zombies = new ArrayList<>();
    protected ZombieManager zombieManager;
    protected WaveSpawner.Foe[] foes;
    protected boolean levelWon = false;
    protected UiSeedTile rewardPacket;
    protected Plant rewardPlant;
    protected WinningItem winningItem;
    protected float difficultyRate;
    protected int maxWaveSize;
    private AssetManager assetManager;

    public Level(ZombieManager zombieManager, float difficultyRate, int maxWaveSize, AssetManager assetManager) {
        this.assetManager = assetManager;
        this.zombieManager = zombieManager;
        System.out.println(assetManager);
        generateFoes(assetManager);

        this.difficultyRate = difficultyRate;
        this.maxWaveSize = maxWaveSize;
    }

    public void startSpawningWaves() {
        waveSpawner = new WaveSpawner(foes, difficultyRate, maxWaveSize, assetManager);
    }


    protected abstract void spawnWinningItem();

    protected abstract void generateFoes(AssetManager assetManager);

    public UiSeedTile getRewardPacket() {
        return rewardPacket;
    }

    public Plant getRewardPlant() {
        return rewardPlant;
    }

    public ArrayList<WaveSpawner.Foe> getZombies() {
        return zombies;
    }

    public boolean hasWon() {
        return levelWon;
    }


    public void update(float delta) {

        if (!hasStartedSpawning && initialDelayTimer < initialDelay) {
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

        if (hasStartedSpawning) {
            timer += delta;

            if (timer >= spawnZombieTimer) {
                timer = 0;
                waveSpawner.incrementWave();
                ArrayList<WaveSpawner.Foe> nextWave = waveSpawner.spawnWave();

                if (!nextWave.isEmpty()) {
                    zombies = nextWave;
                    zombieManager.initializeZombiesFromLevel(zombies);
                } else {
                    // No more waves; check if the level is won
                    if (zombieManager.getZombies().isEmpty() && !levelWon) {
                        levelWon = true;
                        System.out.println("Level Won!!!");
                        spawnWinningItem();
                    }
                }
            }
        }





    }

    public void render(SpriteBatch batch, float delta) {
        if (winningItem != null) {
            winningItem.render(batch, delta);
        }
    }

    public WinningItem getWinningItem() {
        return winningItem;
    }
}
