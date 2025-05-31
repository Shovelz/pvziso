package com.pvz.game.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.pvz.game.plants.Plant;
import com.pvz.game.ui.WinningItem;
import com.pvz.game.zombies.BucketZombie;
import com.pvz.game.zombies.ConeZombie;
import com.pvz.game.zombies.NormalZombie;
import com.pvz.game.zombies.ZombieManager;

public class Level4 extends Level {

    private static final float LEVEL_DIFFICULTY = 1f;
    private static final int MAX_WAVE_SIZE = 7;
    protected Texture rewardPacket;
    protected Plant rewardPlant;
    private AssetManager assetManager;

    public Level4(ZombieManager zm, AssetManager assetManager){
        super(zm, LEVEL_DIFFICULTY, MAX_WAVE_SIZE, assetManager);
        // Initialize the inherited fields here
        this.rewardPlant  = zm.getMap().getPlantStatic().get(3);

        this.rewardPacket = new Texture("packets/" + rewardPlant.getName() + ".png");
        this.assetManager = assetManager;
        this.levelNumber = 4;
    }

    @Override
    protected void generateFoes(AssetManager assetManager) {
        this.foes = new WaveSpawner.Foe[]{
                new WaveSpawner.Foe(new NormalZombie(assetManager), 6, 1, 1),
                new WaveSpawner.Foe(new ConeZombie(assetManager), 12, 3, 3),
                new WaveSpawner.Foe(new BucketZombie(assetManager), 2, 4, 7)
        };

    }

    public void spawnWinningItem() {
        winningItem = new WinningItem(rewardPlant, rewardPacket);
    }
}
