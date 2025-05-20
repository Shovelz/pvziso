package com.pvz.game.levels;

import com.pvz.game.plants.Plant;
import com.pvz.game.tiles.UiSeedTile;
import com.pvz.game.zombies.NormalZombie;
import com.pvz.game.zombies.ZombieManager;

public class Level1 extends Level {
    private static final float LEVEL_DIFFICULTY = 0.5f;
    private static final int MAX_WAVE_SIZE = 5;
    protected UiSeedTile rewardPacket = zombieManager.getMap().getSeedPackets().get(0);
    protected Plant rewardPlant = zombieManager.getMap().getPlantStatic().get(0);

    public Level1(ZombieManager zm){
        super(zm, LEVEL_DIFFICULTY, MAX_WAVE_SIZE);
    }
    @Override
    protected void generateFoes() {
        this.foes = new WaveSpawner.Foe[]{
                new WaveSpawner.Foe(new NormalZombie(), 1, 1, 1),
        };

    }
}
