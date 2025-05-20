package com.pvz.game.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class SunManager {


    private ArrayList<Sun> suns = new ArrayList<Sun>();

    public SunManager() {
    }

    public void addSun(Sun sun) {
        suns.add(sun);
        sun.addSunManager(this);
    }

    public void removeSun(Sun sun) {
        suns.remove(sun);
    }

    public void removeAllSun() {
        suns.clear();
    }

    public ArrayList<Sun> getSuns() {
        return suns;
    }

    public void update(float delta) {
        ArrayList<Sun> suns2 = new ArrayList(suns);
        for (Sun sun : suns2) {
            sun.update(delta);
        }
    }

    public void render(SpriteBatch batch, float delta) {
        ArrayList<Sun> suns2 = new ArrayList(suns);
        for (Sun sun : suns2) {
            sun.render(batch, delta);
        }

    }

}
