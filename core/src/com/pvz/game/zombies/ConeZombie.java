package com.pvz.game.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.tiles.AbstractTile;
import com.pvz.game.tiles.PlantTile;
import com.pvz.game.tiles.TargetTile;

import java.util.Random;

public class ConeZombie extends Zombie {


    //	protected Texture sprite = new Textureg
    protected float speed = -2f;

    private enum State {IDLE, WALKING, EATING}

    ;
    protected float maxHealth = 640f;
    protected float health = maxHealth;
    protected Tilemap map;
    protected int width = 16, height = 40;

    private enum stateEnum {MOVE, EAT}

    ;
    private stateEnum state;
    private Animation<TextureRegion> walkAnimation, eatAnimation, deathAnimation, deathHeadAnimation, explosionDeathAnimation;
    protected Texture walkSheet, eatSheet, deathSheet, headSheet, explosionDeathSheet;
    protected int damage = 100;
    private int previousFrameIndex = -1;
    private Zombie transitionZombie;
    private boolean diedToExplosion = false;
    private AssetManager assetManager;


    public ConeZombie(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadAssets();
        walkSheet = assetManager.get("coneZombieWalkSheet.png", Texture.class);
        eatSheet = assetManager.get("coneZombieEatSheet.png", Texture.class);
        deathSheet = assetManager.get("zombieDeadSheet.png", Texture.class);
        explosionDeathSheet = assetManager.get("zombieDeadExplosionSheet.png", Texture.class);

        worldPos = new Vector2(x, y);
        walkAnimation = loadAnimations(width, height, walkSheet);
        eatAnimation = loadAnimations(width, height, eatSheet);
        deathAnimation = loadAnimations(width + 1, 33, deathSheet);
        hitbox = new Rectangle(worldPos.x, worldPos.y, 13, 22);
        currentAnimation = walkAnimation;
    }


    private void loadAssets(){

        assetManager.load("coneZombieWalkSheet.png", Texture.class);
        assetManager.load("coneZombieEatSheet.png", Texture.class);
        assetManager.load("zombieDeadSheet.png", Texture.class);
        assetManager.load("zombieDeadExplosionSheet.png", Texture.class);

        assetManager.finishLoading();
    }


    @Override
    protected void move(float delta) {
        worldPos = new Vector2(worldPos.x + (speed * 2 * delta), worldPos.y - (speed * delta));
        updateHitbox();
    }


    @Override
    public void update(float delta, Tilemap m) {

        updateState(m);
        if (state == ConeZombie.stateEnum.MOVE && !isDead) {
            move(delta);
        }

        if (currentAnimation.isAnimationFinished(time)) {
            time = 0;  // Reset animation time
        }

        int currentFrameIndex = currentAnimation.getKeyFrameIndex(time);

        // Check if we just entered the first frame of the animation
        if (state == ConeZombie.stateEnum.EAT && currentFrameIndex == 0 && previousFrameIndex != 0) {
            eat(damage);
        }

        previousFrameIndex = currentFrameIndex;
        time += animSpeed * delta;

    }

    @Override
    public void render(SpriteBatch batch, float delta) {

        batch.draw(shadow, worldPos.x+2, worldPos.y-3);
        batch.draw(currentAnimation.getKeyFrame(time, loopAnimation), worldPos.x, worldPos.y);

        if (isDead) {
            batch.draw(deathHeadAnimation.getKeyFrame(time, loopAnimation), worldPos.x, worldPos.y);
        }
        if (currentAnimation.isAnimationFinished(time)) {
            zombieManager.removeDeadZombie(this);
        }
    }

    @Override
    public void takeDamage(float damage, boolean isExplosion) {

        if (damage >= maxHealth) {
            isDead = true;
            zombieManager.removeZombie(this, isExplosion);
            return;
        }
        if (health <= maxHealth / 2f) {  // Replace only when below 50% HP
            Zombie z = new NormalZombie(assetManager);
            z.setLane(lane);
            z.setWorldPosition(worldPos);
            z.setHealth(health);

            zombieManager.replaceZombie(this, z);

        }
        health -= damage;
    }


    public void die(boolean isExplosion) {
        time = 0;
        isDead = true;
        loopAnimation = false;
        diedToExplosion = isExplosion;
        headSheet = new Texture(("zombieDeadHeadSheet" + new Random().nextInt(2) + ".png"));
        deathHeadAnimation = loadAnimations(width + 1, 33, headSheet);
        if (isExplosion) {
            explosionDeathAnimation = loadAnimations(width + 1, 33, explosionDeathSheet);
        }
    }


    @Override
    protected void updateState(Tilemap mp) {
        //Get target plant (closest plant infront of it)
        //Walk towards it
        target = getTarget(mp);
        if (isDead) {
            currentAnimation = diedToExplosion ? explosionDeathAnimation : deathAnimation;
            return;
        }

        if (target instanceof TargetTile || (target instanceof PlantTile && ((PlantTile) target).getPlant().isInteractable())) {
            if (target.getWorldPos().dst(this.worldPos) <= range) {
                state = stateEnum.EAT;
                currentAnimation = eatAnimation;
            } else {
                state = stateEnum.MOVE;
                currentAnimation = walkAnimation;
            }
        }
    }

    protected AbstractTile getTarget(Tilemap mp) {
        for (int i = 0; i < map.map[0].length; i++) {
            if (mp.getPlantLayer().get(new Vector2(lane, i)).getPlant() != null) {
                return mp.getPlantLayer().get(new Vector2(lane, i));
            }
        }

        return mp.getTargetLayer().get(new Vector2(lane, map.map[0].length - 1));
    }


}
