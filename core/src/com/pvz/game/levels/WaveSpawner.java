package com.pvz.game.levels;

import com.pvz.game.zombies.BucketZombie;
import com.pvz.game.zombies.ConeZombie;
import com.pvz.game.zombies.NormalZombie;
import com.pvz.game.zombies.Zombie;

import java.util.*;

public class WaveSpawner {

    private int level = 0;
    private float globalPoints = 1f;
    private Foe[] foes;
    private float difficultyRate;
    private int maxWaveSize;
    private List<Foe> spawnableFoes = new ArrayList<>();

    private static final Random random = new Random();


    public static class Foe {
        Zombie zombie;
        int rate;  // How often it should appear (rarity control)
        int cost;  // Cost to spawn
        int spawnCount = 0;
        int minPoints = 0;

        public Foe(Zombie zombie, int rate, int cost, int minPoints) {
            this.zombie = zombie;
            this.rate = rate;
            this.cost = cost;
            this.minPoints = minPoints;
        }

        public Zombie getZombie() {
            return zombie;
        }

    }


    public WaveSpawner(Foe[] foes, float difficultyRate, int maxWaveSize) {
        this.foes = foes;
        if (foes == null) {
            //Default spawn rate for endless
            this.foes = new Foe[]{
                    new Foe(new NormalZombie(), 30, 1, 1),
                    new Foe(new ConeZombie(), 15, 3, 3),
                    new Foe(new BucketZombie(), 2, 4, 7)
            };
        }

        this.difficultyRate = difficultyRate;
        this.maxWaveSize = maxWaveSize;

//        simulateWaves(10);
    }


    public float getGlobalPoints() {
        return globalPoints;
    }

    public void update() {
        incrementWave();
    }

    public List<Foe> getSpawnableFoes(){
        return spawnableFoes;
    }

    public void generateSpawnableFoes(){
        spawnableFoes.clear();

        List<Foe> allFoes = Arrays.asList(foes);
        for (Foe f : allFoes) {
            if (f.cost <= globalPoints && f.minPoints <= globalPoints) {
                spawnableFoes.add(f);
            }
        }

    }

    /**
     * Simulates a number of waves, printing the foes spawned each wave.
     */
    private void simulateWaves(int waveCount) {
        for (int wave = 1; wave <= waveCount; wave++) {
            System.out.println("Wave " + wave + ":");

            List<Foe> spawnedFoes = spawnWave();
            if (spawnedFoes.isEmpty()) {
                System.out.println("  No foes spawned.");
            } else {
                for (Foe foe : spawnedFoes) {
                    System.out.println("  Spawned: " + foe);
                }
            }

            incrementWave();
            System.out.println();
        }
    }

    public void incrementWave() {
        globalPoints += difficultyRate;
    }

    /**
     * Creates a wave of foes based on available points.
     */
    public ArrayList<Foe> spawnWave() {

        generateSpawnableFoes();

        List<Foe> wave = new ArrayList<>();
        int[] weights = new int[spawnableFoes.size()];
        int pointsRemaining = (int) globalPoints;

        int waveSize = 0;

        while (pointsRemaining > 0 && waveSize < maxWaveSize && !spawnableFoes.isEmpty()) {
            // Recalculate weights dynamically
            for (int i = 0; i < spawnableFoes.size(); i++) {
                Foe f = spawnableFoes.get(i);
                weights[i] = (f.cost > pointsRemaining) ? 0
                        : (int) (((double) (f.rate - f.spawnCount) / f.rate) * 100);
            }

            int selectedIndex = pickWeightedRandom(weights);
            if (selectedIndex == -1) break;

            Foe selectedFoe = spawnableFoes.get(selectedIndex);
            wave.add(selectedFoe);
            pointsRemaining -= selectedFoe.cost;
            selectedFoe.spawnCount++;
            waveSize++;
        }

        return (ArrayList<Foe>) wave;
    }

    /**
     * Weighted random index selection based on array of weights.
     */
    private int pickWeightedRandom(int[] weights) {
        int total = Arrays.stream(weights).sum();
        if (total <= 0) return -1;

        int pick = random.nextInt(total);
        for (int i = 0; i < weights.length; i++) {
            if (pick < weights[i]) return i;
            pick -= weights[i];
        }

        return -1;
    }


}
