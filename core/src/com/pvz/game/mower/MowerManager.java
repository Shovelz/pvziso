package com.pvz.game.mower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.zombies.Zombie;
import com.pvz.game.zombies.ZombieManager;

public class MowerManager {
	private Map<Integer, Mower> mowers = new HashMap<>();
	private int range = 15;
	private ZombieManager zombies;

	public MowerManager(ZombieManager zm) {
		zombies = zm;
	}

	public void addMower(int lane, Mower mower) {
		mowers.put(lane, mower);
	}

	public void removeMower(Mower mower) {
		mowers.remove(mower.getLane());
	}

	public Map<Integer, Mower> getmowers() {
		return mowers;
	}

	public void update(float delta){
		for(Map.Entry<Integer, Mower> entry: new HashMap<Integer,Mower>(mowers).entrySet()){
			int lane = entry.getKey();
			Mower mower = entry.getValue();
			ArrayList<Zombie> laneZombies = new ArrayList<>(zombies.getLanezombies().get(lane));
			for(Zombie zombie : laneZombies){
				if(new Vector2(mower.getHitbox().x, mower.getHitbox().y).dst(new Vector2(zombie.getHitbox().x, zombie.getHitbox().y)) <= range){
					mower.trigger();
					zombies.removeZombie(zombie, false);
				}
			}
			mower.update(delta);
		}

	}

	public void render(SpriteBatch batch, float delta) {

		Map<Integer, Mower> mowers2 = new HashMap<>(mowers);
		for(Map.Entry<Integer, Mower> mower : mowers2.entrySet()){
			mower.getValue().render(batch, delta);
		}

	}

}
