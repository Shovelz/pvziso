package com.pvz.game.levels;

import com.badlogic.gdx.assets.AssetManager;
import com.pvz.game.plants.Plant;
import com.pvz.game.tiles.UiSeedTile;
import com.pvz.game.ui.WinningItem;
import com.pvz.game.zombies.NormalZombie;
import com.pvz.game.zombies.ZombieManager;

public class Level2 extends Level {
    private static final float LEVEL_DIFFICULTY = 0.5f;
    private static final int MAX_WAVE_SIZE = 2;
    protected UiSeedTile rewardPacket;
    protected Plant rewardPlant;
    private AssetManager assetManager;

    public Level2(ZombieManager zm, AssetManager assetManager){
        super(zm, LEVEL_DIFFICULTY, MAX_WAVE_SIZE, assetManager);
        // Initialize the inherited fields here
        this.rewardPacket = zm.getMap().getSeedPackets().get(2);
        this.rewardPlant  = zm.getMap().getPlantStatic().get(2);
        this.assetManager = assetManager;
    }

    @Override
    protected void generateFoes(AssetManager assetManager) {
        this.foes = new WaveSpawner.Foe[]{
                new WaveSpawner.Foe(new NormalZombie(assetManager), 5, 1, 1),
//                new WaveSpawner.Foe(new ConeZombie(), 15, 3, 3),
//                new WaveSpawner.Foe(new BucketZombie(), 2, 4, 7)
//                new WaveSpawner.Foe(new NormalZombie(), 2, 1, 1),
        };

    }

    public void spawnWinningItem() {
        winningItem = new WinningItem(rewardPlant, rewardPacket);
    }
}
