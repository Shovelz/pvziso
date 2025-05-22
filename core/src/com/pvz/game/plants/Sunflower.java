package com.pvz.game.plants;

import com.badlogic.gdx.graphics.Texture;
import com.pvz.game.Tilemap;
import com.pvz.game.projectiles.Projectile;
import com.pvz.game.ui.Sun;
import com.pvz.game.ui.SunManager;
import com.badlogic.gdx.math.Vector2;

public class Sunflower extends Plant {

    public Sunflower() {
//        super("Sunflower", "Potato Mines pack a powerful punch, but they need a while to arm themselves. You should plant them ahead of zombies. They will explode on contact.",
                super("Sunflower", "Sunflowers are essential for you to produce extra sun. Try planting as many as you can!",
                new Texture("sunflower.png"), new Texture("sunflowerSheet.png"),
                0, 300, 24f, 12f, 50, 0, 7.5f, true);
    }


    @Override
    public String getPacketTexture(){
        return "1.png";
    }

    @Override
    public Sunflower clone() throws CloneNotSupportedException {
        Sunflower clonedSunflower = (Sunflower) super.clone();
        return clonedSunflower;
    }

    @Override
    public Projectile fire() {
        return null;
    }

    @Override
    public void action(SunManager sunManager) {
        sunManager.addSun(new Sun(new Vector2(this.getTile().getWorldPos().x + Tilemap.TILE_WIDTH / 4, this.getTile().getWorldPos().y + Tilemap.TILE_HEIGHT / 4), false));
    }

    public void setProjectile(Projectile pro) {
        this.projectile = null;
    }


    @Override
    public void setProjectile() {

    }


}
