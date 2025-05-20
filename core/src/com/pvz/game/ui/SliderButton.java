package com.pvz.game.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pvz.game.screens.Clickable;
import com.pvz.game.screens.GameScreen;

public class SliderButton implements Clickable {

    private Vector2 bounds = new Vector2();
    private Vector2 boundsPosLeft = new Vector2();
    private Vector2 boundsPosRight = new Vector2();
    private float value;
    private Texture sprite = new Texture("sliderButton.png");
    private Vector2 pos;

    private boolean selected = false;

    private Rectangle hitbox;
    private SliderAction action;

    public SliderButton(Vector2 bounds, Vector2 boundsLeft, Vector2 boundsRight, float startingValue, SliderAction action){
        this.bounds = bounds;
        boundsPosLeft = boundsLeft;
        boundsPosRight = boundsRight;
        value = startingValue;
        pos = new Vector2(boundsLeft.x + (boundsRight.x - boundsLeft.x)/2f, boundsLeft.y);
        hitbox = new Rectangle(pos.x, pos.y, 6, 6);
        this.action = action;
    }

    public void update(Vector2 mousePosition){
        if(selected) {
            pos.x = Math.max(boundsPosLeft.x, Math.min(boundsPosRight.x, mousePosition.x - hitbox.getWidth()/2f));
            updateHitbox();
        }
    }

    public void updateHitbox(){
        hitbox.x = pos.x;
        hitbox.y = pos.y;
    }

    public void render(SpriteBatch batch){
        batch.draw(sprite, pos.x, pos.y);
    }

    @Override
    public boolean isHovered(Vector2 mousePosition, Screen gameScreen) {
        return hitbox.contains(mousePosition);
    }

    @Override
    public void onClick(Screen gameScreen){
        selected = !((GameScreen) gameScreen).getMouseReleased();
        if(!selected){
            value = (pos.x - boundsPosLeft.x) / (boundsPosRight.x - boundsPosLeft.x);
            action.apply(value);
        }
    }

    public boolean getSelected(){
        return selected;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }
}
