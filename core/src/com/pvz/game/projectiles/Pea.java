package com.pvz.game.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.audio.SoundManager;
import com.pvz.game.tiles.PlantTile;
import com.pvz.game.zombies.Zombie;
import com.pvz.game.zombies.ZombieManager;

import java.util.ArrayList;

public class Pea extends Projectile {

    public Pea(Vector2 start, int damage, PlantTile tile) {
        super(0f, 20f, new Texture("pea.png"), start, damage, 10, tile, false);
    }

    @Override
    public void render(SpriteBatch batch, float delta) {
        batch.draw(sprite, location.x, location.y);

    }

    public void move(float delta) {
        location = new Vector2(location.x + (speed * 2 * delta), location.y - (speed * delta));
    }

    public boolean checkForCollision() {

        updateHitbox();

        ArrayList<Zombie> zombiesCopy = new ArrayList<>(zombieManager.getzombies());
        for (Zombie zombie : zombiesCopy) {
            if (zombie.getLane() == lane && Intersector.overlaps(hitbox, zombie.getHitbox())) {
                SoundManager.getInstance().play("splat" + MathUtils.random(1, 3));
                zombie.takeDamage(damage, false);
                return true;
            }
        }
        return false;
    }


    @Override
    public void update(float delta) {
    }


    @Override
    public Projectile clone() {
        return new Pea(new Vector2(getLocation()), getDamage(), getPlant());
    }

}
