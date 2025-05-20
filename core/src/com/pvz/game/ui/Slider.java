package com.pvz.game.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Slider {

    private SliderButton button;
    private Vector2 centerPos;
    private float width;
    private Texture sprite;
    private Texture icon;

    public Slider(Vector2 startingPos, int width, SliderAction action, Texture icon){
        this.width = width;
        centerPos = startingPos;
        button = new SliderButton(new Vector2(0f, 1f), new Vector2(startingPos.x - width/2f, startingPos.y), new Vector2(startingPos.x + width/2f, startingPos.y), 0.5f, action);
        sprite = new Texture("slider.png");
        this.icon = icon;
    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, centerPos.x - width/2f, centerPos.y+sprite.getHeight()/4f, width+button.getHitbox().getWidth(), sprite.getHeight());
        button.render(batch);

        if(icon != null){
            batch.draw(icon, centerPos.x - width/2f - 12, centerPos.y - sprite.getHeight()/4f);
        }
    }

    public boolean getSelected(){
        return button.getSelected();
    }

    public void update(Vector2 mousePosition) {
        button.update(mousePosition);
    }

    public SliderButton getButton() {
        return button;
    }

    public Rectangle getHitbox() {
        return button.getHitbox();
    }
}
