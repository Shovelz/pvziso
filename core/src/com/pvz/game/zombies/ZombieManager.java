package com.pvz.game.zombies;

import java.util.*;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.levels.Level;
import com.pvz.game.levels.WaveSpawner;

public class ZombieManager {

    private ArrayList<Zombie> zombies = new ArrayList<Zombie>();
    private ArrayList<Zombie> deadZombies = new ArrayList<Zombie>();

    private Tilemap map;
    private static final int LANE_SPACING = 40; // Horizontal offset for stacking zombies
    private Vector2 prevZombiePos = null;
    private Map<Integer, List<Zombie>> laneZombies = new HashMap<>(); // Keeps track of zombies in each lane
    private Map<Integer, List<Zombie>> laneZombiesPerWave = new HashMap<>(); // Keeps track of zombies in each lane
    private Level level;
    private float groanTimer, nextGroanTime = 3f;


    public ZombieManager(Tilemap mp) {
        this.map = mp;
        for (int i = 0; i < map.map[0].length; i++) {
            laneZombies.put(i, new ArrayList<Zombie>());
            laneZombiesPerWave.put(i, new ArrayList<Zombie>());
        }

    }

    public void setLevel(Level l) {
        this.level = l;
    }


    public void replaceZombie(Zombie oldZombie, Zombie newZombie) {
        int lane = oldZombie.getLane();

        // Remove old zombie from lane list and main zombie list
        laneZombies.get(lane).remove(oldZombie);
        zombies.remove(oldZombie);

        // Add new zombie to both lane list and main zombie list
        laneZombies.get(lane).add(newZombie);
        zombies.add(newZombie);

        newZombie.setLane(lane);
        newZombie.setWorldPosition(oldZombie.getPosition()); // Preserve position
        newZombie.setHealth(oldZombie.getHealth());
        newZombie.addZombieManager(this);
    }

    public ArrayList<Zombie> getZombies() {
        return zombies;
    }

    /**
     * Adds zombies from the current wave without removing existing ones.
     */

    public void initializeZombiesFromLevel(List<WaveSpawner.Foe> newFoes) {
        if (newFoes.isEmpty()) {
            return;
        }


        for (List<Zombie> zombiesList : laneZombiesPerWave.values()) {
            zombiesList.clear();
        }

        for (WaveSpawner.Foe foe : newFoes) {
            if (foe == null || foe.getZombie() == null) {
                System.out.println("Null foe or zombie detected");
                continue;
            }

            Zombie zombie = foe.getZombie().clone(); // Clone to create a unique instance
            zombie.addZombieManager(this);
            zombie.setMap(map);
            // Get the lane with the fewest zombies (can be randomized among equally empty lanes)
            List<Integer> candidateLanes = new ArrayList<>();
            int minCount = Integer.MAX_VALUE;

            for (int lane = 0; lane < 6; lane++) {
                int count = laneZombies.get(lane).size(); // or laneZombiesPerWave.get(lane).size();
                if (count < minCount) {
                    candidateLanes.clear();
                    candidateLanes.add(lane);
                    minCount = count;
                } else if (count == minCount) {
                    candidateLanes.add(lane);
                }
            }
            int lane = 0;
// Pick randomly among equally empty lanes
            if (Math.random() < 0.2) {
                // 20% chance to choose randomly
                lane = new Random().nextInt(6);
            } else {
                lane = candidateLanes.get(new Random().nextInt(candidateLanes.size()));
            }


//            int lane = new Random().ints(0, 6)
//                    .findFirst()
//                    .getAsInt();

            int min = -(int) (Tilemap.TILE_WIDTH / 4);
            int max = (int) (Tilemap.TILE_WIDTH / 4);
            float offset = new Random().nextInt(max - min) + min;

            zombie.setStartPosition(lane, map, offset);
            zombies.add(zombie);
            laneZombiesPerWave.get(lane).add(zombie);
            laneZombies.get(lane).add(zombie);
            prevZombiePos = zombie.getPosition();
        }


        for (Map.Entry<Integer, List<Zombie>> entry : laneZombiesPerWave.entrySet()) {
            List<Zombie> zombies = entry.getValue();
            Random rand = new Random();
            int min = (int) (Tilemap.TILE_WIDTH / 4);
            int max = (int) (Tilemap.TILE_WIDTH + Tilemap.TILE_WIDTH / 3f);
            float offset = rand.nextInt(max - min) + min;
            if (zombies.size() > 1) {
                for (int i = 1; i < zombies.size(); i++) { // Start from index 1
                    Zombie zombie = zombies.get(i);
                    zombie.setStartPosition(zombie.lane, zombie.getMap(), offset); // Apply offset only to the zombies after the first one
                    offset += Tilemap.TILE_WIDTH / 8f;
                }
            }
        }


    }

    public void addZombie(Zombie zombie) {
        zombies.add(zombie);
        zombie.addZombieManager(this);
    }

    public void removeZombie(Zombie zombie, boolean isExplosion) {
        deadZombies.add(zombie);
        zombie.die(isExplosion);
        zombies.remove(zombie);
        laneZombies.get(zombie.lane).remove(zombie);
    }

    public Tilemap getMap() {
        return map;
    }

    public void removeDeadZombie(Zombie zombie) {
        deadZombies.remove(zombie);
    }

    public ArrayList<Zombie> getzombies() {
        return zombies;
    }

    public Map<Integer, List<Zombie>> getLanezombies() {
        return laneZombies;
    }

    public void update(float delta) {

        groanSounds(delta);

        level.update(delta);

        for (Zombie zombie : new ArrayList<>(zombies)) {
            zombie.setMap(map);
            zombie.update(delta, map);
        }

        for (Zombie zombie : new ArrayList<>(deadZombies)) {
            zombie.update(delta, map);
        }
    }

    public void groanSounds(float delta) {

        System.out.println(zombies.size());
        // Only proceed if there are zombies
        if (zombies.isEmpty()) {
            groanTimer = 0; // Reset timer when no zombies
            nextGroanTime = Integer.MAX_VALUE;
            return;
        }

        if (nextGroanTime == Integer.MAX_VALUE) {
            // New zombies appeared, reset groan timing
            nextGroanTime = 2f; // Or something reasonable like 1-3 seconds
            groanTimer = 0;
        }

        groanTimer += delta;

        if (groanTimer >= nextGroanTime) {
            System.out.println("Play zombie sound");
            SoundManager.getInstance().play("zombie_groan" + MathUtils.random(1, 6));
            groanTimer = 0;

            // Clamp zombie count to a max of 3 to limit how fast groans can occur
            int zombieCount = Math.min(zombies.size(), 3);


            float minTime = 3;
            float maxTime = Math.max(1.5f, 7.0f - (zombieCount - 1) * 0.5f);


            nextGroanTime = MathUtils.random(minTime, maxTime);
        }
    }



    public void render(SpriteBatch batch, float delta) {
        ArrayList<Zombie> zombies2 = new ArrayList(zombies);
        Collections.sort(zombies2, new Comparator<Zombie>() {
            @Override
            public int compare(Zombie z1, Zombie z2) {
                return Float.compare(z2.getPosition().y, z1.getPosition().y); // Sort descending by y
            }
        });

        for (Zombie zombie : zombies2) {
            zombie.setMap(map);
            zombie.render(batch, delta);
        }

        ArrayList<Zombie> deadZombies2 = new ArrayList(deadZombies);
        for (Zombie zombie : deadZombies2) {
            zombie.render(batch, delta);
        }

        level.render(batch, delta);
    }

    public Level getLevel() {
        return level;
    }


    public void reset() {
        zombies.clear();
        deadZombies.clear();

        laneZombies.clear();
        laneZombiesPerWave.clear();
        prevZombiePos = null;

        for (int i = 0; i < map.map[0].length; i++) {
            laneZombies.put(i, new ArrayList<Zombie>());
            laneZombiesPerWave.put(i, new ArrayList<Zombie>());
        }

    }

    public void startSpawning() {
        level.startSpawningWaves();
    }
}
