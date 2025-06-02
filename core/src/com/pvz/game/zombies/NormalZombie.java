package com.pvz.game.zombies;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.Tilemap;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.tiles.AbstractTile;
import com.pvz.game.tiles.PlantTile;
import com.pvz.game.tiles.TargetTile;

import java.util.Random;

public class NormalZombie extends Zombie {


    protected float speed = -2.3f;

    private enum State {IDLE, WALKING, EATING}

    ;
    protected float maxHealth = 200f;
    protected float health = maxHealth;
    protected Tilemap map;
    protected int width = 16, height = 33;

    private enum stateEnum {MOVE, EAT}

    ;
    private stateEnum state;
    private Animation<TextureRegion> walkAnimation, eatAnimation, deathAnimation, deathHeadAnimation, explosionDeathAnimation;
    protected Texture walkSheet, eatSheet, deathSheet, headSheet, explosionDeathSheet;
    protected int damage = 100;
    private int previousFrameIndex = -1;
    private boolean diedToExplosion = false;
    private AssetManager assetManager;


    public NormalZombie(AssetManager assetManager) {
        this.assetManager = assetManager;
        loadAssets();
        walkSheet = assetManager.get("zombieWalkSheet.png", Texture.class);
        eatSheet = assetManager.get("zombieEatSheet.png", Texture.class);
        deathSheet = assetManager.get("zombieDeadSheet.png", Texture.class);
        explosionDeathSheet = new Texture("zombieDeadExplosionSheet.png");
        worldPos = new Vector2(x, y);
        walkAnimation = loadAnimations(width, height, walkSheet);
        eatAnimation = loadAnimations(width, height, eatSheet);
        deathAnimation = loadAnimations(width + 1, height, deathSheet);
        hitbox = new Rectangle(worldPos.x, worldPos.y, 13, 22);
        currentAnimation = walkAnimation;
    }

    private void loadAssets(){

        assetManager.load("zombieWalkSheet.png", Texture.class);
        assetManager.load("zombieEatSheet.png", Texture.class);
        assetManager.load("zombieDeadSheet.png", Texture.class);

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
        if (state == stateEnum.MOVE && !isDead) {
            move(delta);
        }


        if (currentAnimation.isAnimationFinished(time)) {
            time = 0;  // Reset animation time
        }

        int currentFrameIndex = currentAnimation.getKeyFrameIndex(time);

        // Check if we just entered the first frame of the animation
        if (state == stateEnum.EAT && currentFrameIndex == 0 && previousFrameIndex != 0) {
            eat(damage);
        }

        previousFrameIndex = currentFrameIndex;
        time += animSpeed * delta;

    }

    @Override
    public void render(SpriteBatch batch, float delta) {

        batch.draw(shadow, worldPos.x, worldPos.y);
        batch.draw(currentAnimation.getKeyFrame(time, loopAnimation), worldPos.x-2, worldPos.y+3);

        if (isDead) {
            batch.draw(deathHeadAnimation.getKeyFrame(time, loopAnimation), worldPos.x, worldPos.y);
        }
        if (currentAnimation.isAnimationFinished(time)) {
            zombieManager.removeDeadZombie(this);
        }
    }


    public void takeDamage(float projectileDamage, boolean isExplosion) {
        if (projectileDamage >= maxHealth || health <= 0) {
            isDead = true;
            zombieManager.removeZombie(this, isExplosion);
            return;
        }
        health -= projectileDamage;
    }

    public void die(boolean isExplosion) {
        time = 0;
        isDead = true;
        loopAnimation = false;
        diedToExplosion = isExplosion;
        headSheet = new Texture(("zombieDeadHeadSheet" + new Random().nextInt(2) + ".png"));
        deathHeadAnimation = loadAnimations(width + 1, height, headSheet);
        if (isExplosion) {
            explosionDeathAnimation = loadAnimations(width + 1, height, explosionDeathSheet);
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
