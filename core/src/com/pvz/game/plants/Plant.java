package com.pvz.game.plants;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pvz.game.projectiles.*;
import com.pvz.game.tiles.PlantTile;
import com.pvz.game.ui.SunManager;

public abstract class Plant implements Cloneable {

    private String name;
    private String description;
    private Texture texture;
    private Texture spriteSheet;
    private int damage;
    private int health;
    private float reload; // reload speed
    private float startingReload; // reload speed
    private int cost; // sun cost

    private int range; // range
    private float recharge; // seed packet time
    protected Projectile projectile = null;
    protected ArrayList<Projectile> projectiles;

    private TextureRegion[] animationFrames;
    private Animation<TextureRegion> animation;
    protected PlantTile tile = null;
    protected int lane;
    protected boolean interactable;



    public String getPacketTexture() {
        return "Peashooter.png";
    }

    private enum State {
        IDLE, FIRING
    }



    public Plant(String name, String description, Texture texture, Texture spriteSheet, int damage, int health, float reload, float startingReload, int cost, int range,
                 float recharge, boolean interactable) {
        this.name = name;
        this.description = description;
        this.texture = texture;
        this.spriteSheet = spriteSheet;
        this.damage = damage;
        this.health = health;
        this.reload = reload;
        this.startingReload = startingReload;
        this.cost = cost;
        this.range = range;
        this.recharge = recharge;
        this.interactable = interactable;

        this.projectiles = new ArrayList<Projectile>();
        loadAnimations();
    }

    public PlantTile getTile() {
        return tile;
    }

    public void setTile(PlantTile tile) {
        this.tile = tile;
        tile.setStartingFireTime(startingReload);
    }

    public void endOfAnimation() {

    }

    public int getLane() {
        return lane;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public void takeDamage(float d) {
        if (health <= 0) {
            tile.removePlant();
            return;
        }
        health -= d;
    }


    public void setLane(int lane) {
        this.lane = lane;
    }

    public abstract void setProjectile();

    // Were loading animations in the Plant and not the Tiles so we only have to
    // load it once
    private void loadAnimations() {

        TextureRegion[][] tmpFrames = TextureRegion.split(spriteSheet, 48, 48);

        animationFrames = new TextureRegion[tmpFrames.length * tmpFrames[0].length];
        int index = 0;
        for (int row = 0; row < tmpFrames.length; row++) {
            for (int col = 0; col < tmpFrames[tmpFrames.length - 1].length; col++) {
                animationFrames[index++] = tmpFrames[row][col];
            }

        }
        animation = new Animation<TextureRegion>(1f / 4f, animationFrames);

    }


    public abstract void action(SunManager sunManager);

    public abstract Projectile fire();

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void removeProjectile(Projectile pro) {
        projectiles.remove(pro);
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public Texture getSpriteSheet() {
        return spriteSheet;
    }

    public void setSpriteSheet(Texture spriteSheet) {
        this.spriteSheet = spriteSheet;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public float getReload() {
        return reload;
    }

    public void setReload(float reload) {
        this.reload = reload;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public float getRecharge() {
        return recharge;
    }

    public void setRecharge(float recharge) {
        this.recharge = recharge;
    }

    public Projectile getProjectile() {
        return projectile;
    }


    public TextureRegion[] getAnimationFrames() {
        return animationFrames;
    }

    public void setAnimationFrames(TextureRegion[] animationFrames) {
        this.animationFrames = animationFrames;
    }

    public Animation<TextureRegion> getAnimation() {
        return animation;
    }

    public void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
    }

    public Plant cloneWithTile(PlantTile newTile) throws CloneNotSupportedException {
        try {
            Plant clonedPlant = (Plant) this.clone();
            clonedPlant.setTile(newTile); // Assign the new tile
            clonedPlant.setProjectile();
            return clonedPlant;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning failed", e);
        }
    }

    @Override
    public Plant clone() throws CloneNotSupportedException {
        try {
            // Create a new instance of the subclass using reflection
            Plant clonedPlant = this.getClass().getDeclaredConstructor().newInstance();

            // Copy properties from the current instance to the clone
            clonedPlant.texture = new Texture(this.texture.getTextureData());
            clonedPlant.spriteSheet = new Texture(this.spriteSheet.getTextureData());
            clonedPlant.damage = this.damage;
            clonedPlant.health = this.health;
            clonedPlant.reload = this.reload;
            clonedPlant.cost = this.cost;
            clonedPlant.range = this.range;
            clonedPlant.recharge = this.recharge;
            clonedPlant.projectile = this.projectile; // Ensure projectile is cloneable or handled properly
            clonedPlant.tile = this.tile;

            return clonedPlant;
        } catch (Exception e) {
            throw new AssertionError("Cloning failed", e);
        }
    }

}
