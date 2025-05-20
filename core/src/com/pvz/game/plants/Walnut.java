package com.pvz.game.plants;

import com.badlogic.gdx.graphics.Texture;
import com.pvz.game.projectiles.Projectile;
import com.pvz.game.ui.SunManager;

public class Walnut extends Plant {

    public Walnut() {
        super(new Texture("walnut.png"), new Texture("walnutSheet.png"), 0, 4000, 0, 0, 50, 0, 30f, true);
    }


    @Override
    public Walnut clone() throws CloneNotSupportedException {
        return (Walnut) super.clone();
    }


    @Override
    public Projectile fire() {
        return null;
    }

    @Override
    public void action(SunManager sunManager) {
    }

    public void setProjectile(Projectile pro) {
        this.projectile = null;
    }


    @Override
    public void setProjectile() {
    }


}
